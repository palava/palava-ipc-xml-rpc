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

import javax.annotation.concurrent.ThreadSafe;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

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

//    private HttpRequest request;
    
    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws Exception {
        final Object message = event.getMessage();
        if (message instanceof HttpRequest) {
            final HttpRequest request = HttpRequest.class.cast(message);
            context.setAttachment(request);
            LOG.trace("Decoding {} into channel buffer", request);
            Channels.fireMessageReceived(context, request.getContent(), event.getRemoteAddress());
        } else {
            context.sendUpstream(event);
        }
    }
    
    @Override
    public void writeRequested(ChannelHandlerContext context, MessageEvent event) throws Exception {
        final Object message = event.getMessage();
        if (message instanceof ChannelBuffer) {
            final HttpRequest request = HttpRequest.class.cast(context.getAttachment());
            Preconditions.checkState(request != null, "No incoming request");
            final ChannelBuffer content = ChannelBuffer.class.cast(message);
            LOG.trace("Encoding {} into http response", content);
            final HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.setHeader(Names.CONTENT_TYPE, "text/xml");
            response.setHeader(Names.CONTENT_LENGTH, content.readableBytes());
            response.setContent(content);
            
            final ChannelFuture future = event.getFuture();
            Channels.write(context, future, response, event.getRemoteAddress());
            
            if (HttpHeaders.isKeepAlive(request)) {
                LOG.trace("Http request was marked keep-alive, not closing the connection.");
            } else {
                LOG.trace("Http request was not marked as keep-alive, closing connection...");
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } else {
            context.sendDownstream(event);
        }
    }
    
}