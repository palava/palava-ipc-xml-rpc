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

import java.util.AbstractList;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.MapIpcArguments;
import de.cosmocode.palava.ipc.xml.rpc.adapters.Adapter;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodCall;
import de.cosmocode.palava.ipc.xml.rpc.generated.Param;
import de.cosmocode.palava.ipc.xml.rpc.generated.Struct;
import de.cosmocode.palava.ipc.xml.rpc.generated.Value;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodCall.Params;

/**
 * A decoder which decodes {@link MethodCall}s into {@link XmlRpcCall}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
@Sharable
@ThreadSafe
final class MethodCallDecoder extends OneToOneDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(MethodCallDecoder.class);
    
    private final Adapter<Struct, Map<String, Object>> mapAdapter;
    private final Adapter<Value, Object> objectAdapter;
    
    @Inject
    public MethodCallDecoder(Adapter<Struct, Map<String, Object>> mapAdapter, Adapter<Value, Object> objectAdapter) {
        this.mapAdapter = Preconditions.checkNotNull(mapAdapter, "MapAdapter");
        this.objectAdapter = Preconditions.checkNotNull(objectAdapter, "ObjectAdapter");
    }

    @Override
    protected Object decode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (message instanceof MethodCall) {
            final MethodCall methodCall = MethodCall.class.cast(message);
            final String methodName = methodCall.getMethodName();
            
            final Params params = methodCall.getParams();
            final IpcArguments arguments;

            if (params == null) {
                LOG.trace("No params provided");
                arguments = MapIpcArguments.empty();
            } else if (params.getParam().isEmpty()) {
                LOG.trace("Empty params provided");
                arguments = MapIpcArguments.empty();
            } else if (params.getParam().size() == 1 && params.getParam().get(0).getValue() instanceof Struct) {
                LOG.trace("Treating single struct param as named params");
                // single struct parameter will be treated as named parameters
                final Struct struct = Struct.class.cast(params.getParam().get(0).getValue());
                arguments = named(struct);
            } else {
                LOG.trace("Treating params as positional");
                // positional parameters
                arguments = positional(params);
            }
            
            return new XmlRpcCall(methodName, arguments);
        } else {
            return message;
        }
    }

    private IpcArguments named(Struct struct) {
        return new XmlRpcArguments(mapAdapter.decode(struct));
    }
    
    private IpcArguments positional(Params params) {
        return new XmlRpcArguments(new ParamsList(params.getParam(), objectAdapter));
    }
    
    /**
     * {@link List} of {@link Param} backed {@link List} implementation.
     *
     * @since 1.0
     * @author Willi Schoenborn
     */
    private static final class ParamsList extends AbstractList<Object> {
        
        private final List<Param> params;
        private final Adapter<Value, Object> objectAdapter;
        
        public ParamsList(List<Param> params, Adapter<Value, Object> objectAdapter) {
            this.params = params;
            this.objectAdapter = objectAdapter;
        }

        @Override
        public Object get(int index) {
            return objectAdapter.decode(params.get(index).getValue());
        }

        @Override
        public int size() {
            return params.size();
        }
        
    }
    
}
