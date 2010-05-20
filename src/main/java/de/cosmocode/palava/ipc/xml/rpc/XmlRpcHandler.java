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

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
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

import de.cosmocode.palava.core.Registry.Proxy;
import de.cosmocode.palava.core.Registry.SilentProxy;
import de.cosmocode.palava.ipc.IpcCallCreateEvent;
import de.cosmocode.palava.ipc.IpcCallDestroyEvent;
import de.cosmocode.palava.ipc.IpcCallScope;
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
    
    private IpcCommandExecutor executor;
    
    private final IpcConnectionCreateEvent connectionCreateEvent;
    
    private final IpcConnectionDestroyEvent connectionDestroyEvent;
    
    private final IpcCallCreateEvent callCreateEvent;
    
    private final IpcCallDestroyEvent callDestroyEvent;
    
    private final IpcCallScope scope;
    
    @Inject
    public XmlRpcHandler(IpcCommandExecutor executor,
        @Proxy IpcConnectionCreateEvent connectionCreateEvent,
        @SilentProxy IpcConnectionDestroyEvent connectionDestroyEvent,
        @Proxy IpcCallCreateEvent callCreateEvent, 
        @SilentProxy IpcCallDestroyEvent callDestroyEvent,
        IpcCallScope scope) {
        this.executor = Preconditions.checkNotNull(executor, "Executor");
        this.connectionCreateEvent = Preconditions.checkNotNull(connectionCreateEvent, "ConnectionCreateEvent");
        this.connectionDestroyEvent = Preconditions.checkNotNull(connectionDestroyEvent, "ConnectionDestroyEvent");
        this.callCreateEvent = Preconditions.checkNotNull(callCreateEvent, "CallCreateEvent");
        this.callDestroyEvent = Preconditions.checkNotNull(callDestroyEvent, "CallDestroyEvent");
        this.scope = Preconditions.checkNotNull(scope, "Scope");
    }
    
    @Override
    public void channelConnected(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
        final Channel channel = event.getChannel();
        final DetachedConnection connection = new ChannelConnection(channel);
        connections.put(channel, connection);
        connectionCreateEvent.eventIpcConnectionCreate(connection);
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws Exception {
        if (event.getMessage() instanceof XmlRpcCall) {
            final XmlRpcCall call = XmlRpcCall.class.cast(event.getMessage());
            final Channel channel = event.getChannel();
            final DetachedConnection connection = connections.get(channel);
            call.attachTo(connection);
            final Object result = process(call);
            event.getChannel().write(result).addListener(ChannelFutureListener.CLOSE);
        } else {
            throw new IllegalStateException(String.format("Unknown message {}", event.getMessage()));
        }
    }
    
    private Object process(XmlRpcCall call) throws IpcCommandExecutionException {
        callCreateEvent.eventIpcCallCreate(call);
        scope.enter(call);
        try {
            return executor.execute(call.getMethodName(), call);
        } finally {
            callDestroyEvent.eventIpcCallDestroy(call);
            scope.exit();
            call.clear();
        }
    }
    
    @Override
    public void channelClosed(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
        final DetachedConnection connection = connections.remove(event.getChannel());
        connectionDestroyEvent.eventIpcConnectionDestroy(connection);
        connection.clear();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) throws Exception {
        final Channel channel = event.getChannel();
        LOG.error("Exception in channel " + channel, event.getCause());
        channel.write(event.getCause()).addListener(ChannelFutureListener.CLOSE);
    }

}
