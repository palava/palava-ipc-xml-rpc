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

import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.parsers.DocumentBuilder;

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
import org.w3c.dom.Document;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;

import de.cosmocode.collections.Procedure;
import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;
import de.cosmocode.palava.ipc.IpcCommandExecutor;
import de.cosmocode.palava.ipc.IpcConnectionCreateEvent;
import de.cosmocode.palava.ipc.IpcConnectionDestroyEvent;
import de.cosmocode.palava.ipc.netty.ChannelConnection;
import de.cosmocode.palava.ipc.protocol.DetachedConnection;


/**
 * A {@link ChannelHandler} which processes incoming xml
 * requests using configured protocols.
 * 
 * @since 1.0
 * @author Willi Schoenborn
 */
@Sharable
@ThreadSafe
final class XmlRpcHandler extends SimpleChannelHandler {

    private static final Logger LOG = LoggerFactory.getLogger(XmlRpcHandler.class);
    
    private final ConcurrentMap<Channel, DetachedConnection> connections = new MapMaker().makeMap();
    
    private final Registry registry;
    
    private IpcCommandExecutor executor;
    
    private final DocumentBuilder builder;
    
    @Inject
    public XmlRpcHandler(Registry registry, IpcCommandExecutor executor, DocumentBuilder builder) {
        this.registry = Preconditions.checkNotNull(registry, "Registry");
        this.executor = Preconditions.checkNotNull(executor, "Executor");
        this.builder = Preconditions.checkNotNull(builder, "Builder");
    }
    
    @Override
    public void channelConnected(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
        final Channel channel = event.getChannel();
        final DetachedConnection connection = new ChannelConnection(channel);
        connections.put(channel, connection);
        
        registry.notify(IpcConnectionCreateEvent.class, new Procedure<IpcConnectionCreateEvent>() {
           
            @Override
            public void apply(IpcConnectionCreateEvent input) {
                input.eventIpcConnectionCreate(connection);
            }
            
        });
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws Exception {
        if (event.getMessage() instanceof XmlRpcCall) {
            final XmlRpcCall call = XmlRpcCall.class.cast(event.getMessage());
            final Channel channel = event.getChannel();
            final DetachedConnection connection = connections.get(channel);
            call.attachTo(connection);
            final Object result = process(call);
            event.getChannel().write(result);
        } else {
            throw new IllegalStateException(String.format("Unknown message {}", event.getMessage()));
        }
    }
    
    private Object process(XmlRpcCall call) {
        // TODO scope, event, clear
        try {
            return executor.execute(call.getMethodName(), call);
        /* CHECKSTYLE:OFF */
        } catch (RuntimeException e) {
        /* CHECKSTYLE:ON */
            LOG.error("Unexpected exception in protocol", e);
            final Document document = builder.newDocument();
            Error.set(document, e);
            return document;
        } catch (IpcCommandExecutionException e) {
            LOG.error("Exception in protocol", e);
            final Document document = builder.newDocument();
            Error.set(document, e);
            return document;
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

}
