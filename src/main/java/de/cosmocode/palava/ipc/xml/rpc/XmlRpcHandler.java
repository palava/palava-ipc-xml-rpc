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

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.ThreadSafe;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;

import de.cosmocode.collections.Procedure;
import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;
import de.cosmocode.palava.ipc.IpcConnectionCreateEvent;
import de.cosmocode.palava.ipc.IpcConnectionDestroyEvent;
import de.cosmocode.palava.ipc.protocol.DefaultDetachedConnection;
import de.cosmocode.palava.ipc.protocol.DetachedConnection;
import de.cosmocode.palava.ipc.protocol.Protocol;
import de.cosmocode.palava.ipc.protocol.ProtocolException;
import de.cosmocode.palava.ipc.xml.Xml;
import de.cosmocode.palava.jmx.MBeanService;


/**
 * A {@link ChannelHandler} which processes incoming xml
 * requests using configured protocols.
 * 
 * @since 1.0
 * @author Willi Schoenborn
 */
@Sharable
@ThreadSafe
final class XmlRpcHandler extends SimpleChannelHandler implements Initializable, Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(XmlRpcHandler.class);
    
    private final ConcurrentMap<Channel, DetachedConnection> connections = new MapMaker().makeMap();
    
    private final Registry registry;
    
    private final Iterable<Protocol> protocols;
    
    private final MBeanService mBeanService;
    
    @Inject
    public XmlRpcHandler(Registry registry, @Xml Iterable<Protocol> protocols, MBeanService mBeanService) {
        this.registry = Preconditions.checkNotNull(registry, "Registry");
        this.protocols = Preconditions.checkNotNull(protocols, "Protocols");
        this.mBeanService = Preconditions.checkNotNull(mBeanService, "MBeanService");
    }
    
    @Override
    public void initialize() throws LifecycleException {
        mBeanService.register(this);
    }
    
    @Override
    public void channelConnected(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
        final DetachedConnection connection = new DefaultDetachedConnection();
        connections.put(event.getChannel(), connection);
        
        registry.notify(IpcConnectionCreateEvent.class, new Procedure<IpcConnectionCreateEvent>() {
           
            @Override
            public void apply(IpcConnectionCreateEvent input) {
                input.eventIpcConnectionCreate(connection);
            }
            
        });
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws Exception {
        final Object request = event.getMessage();
        final Channel channel = event.getChannel();
        
        final Protocol protocol = findProtocol(request);
        final DetachedConnection connection = connections.get(channel);
        final Object response = process(protocol, request, connection);
        
        if (response == Protocol.NO_RESPONSE) {
            LOG.trace("Omitting response as requested by {}", protocol);
        } else {
            channel.write(response);
        }
    }
    
    private Protocol findProtocol(Object request) {
        for (Protocol protocol : protocols) {
            if (protocol.supports(request)) return protocol;
        }
        throw new NoSuchElementException("No protocol found which can handle " + request);
    }

    private Object process(Protocol protocol, Object request, DetachedConnection connection) {
        try {
            LOG.trace("Processing request of type {} using {}", request.getClass(), protocol);
            return protocol.process(request, connection);
        } catch (ProtocolException e) {
            LOG.warn("Error in protocol", e);
            return protocol.onError(e, request);
        /* CHECKSTYLE:OFF */
        } catch (RuntimeException e) {
        /* CHECKSTYLE:ON */
            LOG.error("Unexpected exception in protocol", e);
            return protocol.onError(e, request);
        }
    }
    
    @Override
    public void channelClosed(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
        final DetachedConnection connection = connections.remove(event.getChannel());
        
        registry.notifySilent(IpcConnectionDestroyEvent.class, new Procedure<IpcConnectionDestroyEvent>() {
            
            @Override
            public void apply(IpcConnectionDestroyEvent input) {
                input.eventIpcConnectionDestroy(connection);
            }
            
        });
        
        connection.clear();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) throws Exception {
        final Channel channel = event.getChannel();
        LOG.error("Exception in channel " + channel, event.getCause());
        channel.close();
    }

    @Override
    public void dispose() throws LifecycleException {
        mBeanService.unregister(this);
    }
    
}
