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

import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.ipc.xml.rpc.adapters.Adapter;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodResponse;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodResponse.Fault;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodResponse.Params;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;
import de.cosmocode.palava.ipc.xml.rpc.generated.Param;
import de.cosmocode.palava.ipc.xml.rpc.generated.Value;

/**
 * An encoder which encodes {@link Map}s into {@link MethodResponse}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
@Sharable
@ThreadSafe
final class MethodResponseEncoder extends OneToOneEncoder {

    private static final Logger LOG = LoggerFactory.getLogger(MethodResponseEncoder.class);
    
    private final ObjectFactory factory;
    private final Adapter<Value, Object> objectAdapter;
    private final Adapter<Fault.Value, Throwable> throwableAdapter;
    
    @Inject
    public MethodResponseEncoder(@XmlRpc ObjectFactory factory, 
        Adapter<Value, Object> objectAdapter, Adapter<Fault.Value, Throwable> throwableAdapter) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
        this.objectAdapter = Preconditions.checkNotNull(objectAdapter, "ObjectAdapter");
        this.throwableAdapter = Preconditions.checkNotNull(throwableAdapter, "ThrowableAdapter");
    }
    
    @Override
    protected Object encode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (message instanceof Map<?, ?>) {
            final Map<?, ?> map = Map.class.cast(message);
            final MethodResponse response = factory.createMethodResponse();
            
            final Params params = factory.createMethodResponseParams();
            response.setParams(params);
            
            final Param param = factory.createParam();
            params.setParam(param);
            
            final Value value = objectAdapter.encode(map);
            param.setValue(value);
            
            LOG.trace("Encoded {} into {}", map, response);
            return response;
        } else if (message instanceof Throwable) {
            final Throwable throwable = Throwable.class.cast(message);
            
            final MethodResponse response = factory.createMethodResponse();
            
            final Fault fault = factory.createMethodResponseFault();
            response.setFault(fault);
            
            final Fault.Value value = throwableAdapter.encode(throwable);
            fault.setValue(value);
            
            return response;
        } else {
            return message;
        }
    }

}
