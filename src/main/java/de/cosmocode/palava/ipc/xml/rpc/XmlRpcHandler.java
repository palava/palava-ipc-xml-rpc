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

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.core.Registry.Proxy;
import de.cosmocode.palava.core.Registry.SilentProxy;
import de.cosmocode.palava.ipc.IpcCallCreateEvent;
import de.cosmocode.palava.ipc.IpcCallDestroyEvent;
import de.cosmocode.palava.ipc.IpcCallScope;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;
import de.cosmocode.palava.ipc.IpcCommandExecutor;
import de.cosmocode.palava.ipc.netty.ConnectionManager;
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
    
    private final ConnectionManager manager;
    
    private final IpcCommandExecutor executor;
    
    private final IpcCallCreateEvent createEvent;
    
    private final IpcCallDestroyEvent destroyEvent;
    
    private final IpcCallScope scope;
    
    @Inject
    public XmlRpcHandler(
        ConnectionManager manager,
        IpcCommandExecutor executor,
        @Proxy IpcCallCreateEvent createEvent, 
        @SilentProxy IpcCallDestroyEvent destroyEvent,
        IpcCallScope scope) {
        this.manager = Preconditions.checkNotNull(manager, "Manager");
        this.executor = Preconditions.checkNotNull(executor, "Executor");
        this.createEvent = Preconditions.checkNotNull(createEvent, "CreateEvent");
        this.destroyEvent = Preconditions.checkNotNull(destroyEvent, "DestroyEvent");
        this.scope = Preconditions.checkNotNull(scope, "Scope");
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws Exception {
        if (event.getMessage() instanceof XmlRpcCall) {
            final XmlRpcCall call = XmlRpcCall.class.cast(event.getMessage());
            final Channel channel = event.getChannel();
            final DetachedConnection connection = manager.get(channel);
            call.attachTo(connection);
            final Object result = process(call);
            event.getChannel().write(result);
        } else {
            throw new IllegalStateException(String.format("Unknown message %s", event.getMessage()));
        }
    }
    
    private Object process(XmlRpcCall call) {
        createEvent.eventIpcCallCreate(call);
        scope.enter(call);
        try {
            return executor.execute(call.getMethodName(), call);
        } catch (IpcCommandExecutionException e) {
            return e;
        /* CHECKSTYLE:OFF */
        } catch (RuntimeException e) {
        /* CHECKSTYLE:ON */
            return e;
        } finally {
            destroyEvent.eventIpcCallDestroy(call);
            scope.exit();
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) throws Exception {
        final Channel channel = event.getChannel();
        LOG.error("Exception in channel " + channel, event.getCause());
        channel.close();
    }

}
