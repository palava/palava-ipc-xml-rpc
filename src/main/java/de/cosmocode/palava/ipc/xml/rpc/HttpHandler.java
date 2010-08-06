/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.ipc.xml.rpc;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.cosmocode.palava.ipc.IpcSession;
import de.cosmocode.palava.ipc.IpcSessionProvider;
import de.cosmocode.palava.ipc.netty.ConnectionManager;
import de.cosmocode.palava.ipc.protocol.DetachedConnection;

/**
 * A {@link ChannelHandler} which decodes {@link HttpRequest}s
 * into {@link ChannelBuffer}s and encodes {@link ChannelBuffer}s into
 * {@link HttpResponse}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
@Sharable
@ThreadSafe
final class HttpHandler extends SimpleChannelHandler {

    private static final Logger LOG = LoggerFactory.getLogger(HttpHandler.class);
    
    private final IpcSessionProvider provider;
    
    private final ConnectionManager manager;

    private String cookieName = "psessid";

    @Inject
    public HttpHandler(IpcSessionProvider provider, ConnectionManager manager) {
        this.provider = Preconditions.checkNotNull(provider, "Provider");
        this.manager = Preconditions.checkNotNull(manager, "Manager");
    }

    @Inject(optional = true)
    public void setCookieName(@Named(XmlRpc.COOKIE_NAME) String cookieName) {
        this.cookieName = Preconditions.checkNotNull(cookieName, "CookieName");
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws Exception {
        final Object message = event.getMessage();
        if (message instanceof HttpRequest) {
            final HttpRequest request = HttpRequest.class.cast(message);
            final SocketAddress remoteAddress = event.getRemoteAddress();
            
            final Set<Cookie> cookies = getCookies(request);
            final String sessionId = findSessionId(cookies);
            
            final String identifier;
            if (remoteAddress instanceof InetSocketAddress) {
                identifier = InetSocketAddress.class.cast(remoteAddress).getHostName();
            } else {
                identifier = null;
            }
            
            final IpcSession session = provider.getSession(sessionId, identifier);
            final DetachedConnection connection = manager.get(event.getChannel());
            connection.attachTo(session);
            
            context.setAttachment(new Attachment(request, cookies));
            LOG.trace("Decoding {} into channel buffer", request);
            Channels.fireMessageReceived(context, request.getContent(), remoteAddress);
        } else {
            context.sendUpstream(event);
        }
    }
    
    private Set<Cookie> getCookies(HttpRequest request) {
        final CookieDecoder decoder = new CookieDecoder();
        final String cookieString = request.getHeader(Names.COOKIE);
        if (StringUtils.isBlank(cookieString)) return Collections.emptySet();
        return decoder.decode(cookieString);
    }
    
    private String findSessionId(Set<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                LOG.trace("Found sessionId in cookie: {}", cookie.getValue());
                return cookie.getValue();
            }
        }
        return null;
    }
    
    @Override
    public void writeRequested(ChannelHandlerContext context, MessageEvent event) throws Exception {
        final Object message = event.getMessage();
        if (message instanceof ChannelBuffer) {
            final Attachment attachment = Attachment.class.cast(context.getAttachment());
            Preconditions.checkState(attachment != null, "No attachment set");
            final ChannelBuffer content = ChannelBuffer.class.cast(message);
            
            LOG.trace("Encoding {} into http response", content);
            final HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            
            // setting all requires headers
            response.setHeader(Names.CONTENT_TYPE, "text/xml");
            response.setHeader(Names.CONTENT_LENGTH, content.readableBytes());
            
            final DetachedConnection connection = manager.get(event.getChannel());
            final IpcSession session = connection.getSession();
            
            final CookieEncoder cookieEncoder = new CookieEncoder(true);
            for (Cookie cookie : attachment.getCookies()) {
                cookieEncoder.addCookie(cookie);
            }
            
            final String sessionId = session.getSessionId();
            LOG.trace("Adding session cookie {}/{}", cookieName, sessionId);
            cookieEncoder.addCookie(cookieName, sessionId);
            
            response.setHeader(Names.SET_COOKIE, cookieEncoder.encode());
            
            response.setContent(content);
            
            final ChannelFuture future = event.getFuture();
            Channels.write(context, future, response, event.getRemoteAddress());
            
            if (HttpHeaders.isKeepAlive(attachment.getRequest())) {
                LOG.trace("Http request was marked keep-alive, not closing the connection.");
            } else {
                LOG.trace("Http request was not marked as keep-alive, closing connection...");
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } else {
            context.sendDownstream(event);
        }
    }
    
    /**
     * Internal attachment which can be used to pass an {@link HttpRequest} and
     * a set of {@link Cookie}s as an attachment using {@link ChannelHandlerContext#setAttachment(Object)}.
     *
     * @since 1.0
     * @author Willi Schoenborn
     */
    private static final class Attachment {
        
        private final HttpRequest request;
        
        private final Set<Cookie> cookies;

        public Attachment(HttpRequest request, Set<Cookie> cookies) {
            this.request = request;
            this.cookies = cookies;
        }
        
        public HttpRequest getRequest() {
            return request;
        }
        
        public Set<Cookie> getCookies() {
            return cookies;
        }
        
    }
    
}
