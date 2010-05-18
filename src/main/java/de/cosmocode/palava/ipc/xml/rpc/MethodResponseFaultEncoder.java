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

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.ipc.xml.rpc.generated.I4;
import de.cosmocode.palava.ipc.xml.rpc.generated.Member;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodResponse;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;
import de.cosmocode.palava.ipc.xml.rpc.generated.String;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodResponse.Fault;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodResponse.Fault.Value.Struct;

/**
 * An encoder which encodes {@link Throwable}s to {@link MethodResponse}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class MethodResponseFaultEncoder extends OneToOneEncoder {

    private static final Logger LOG = LoggerFactory.getLogger(MethodResponseFaultEncoder.class);
    
    private final ObjectFactory factory;
    
    @Inject
    public MethodResponseFaultEncoder(@XmlRpc ObjectFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }
    
    @Override
    protected Object encode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (message instanceof Throwable) {
            final Throwable throwable = Throwable.class.cast(message);
            final MethodResponse response = factory.createMethodResponse();
            final Fault fault = factory.createMethodResponseFault();
            response.setFault(fault);
            final Fault.Value value = factory.createMethodResponseFaultValue();
            fault.setValue(value);
            final Struct struct = factory.createMethodResponseFaultValueStruct();
            value.setStruct(struct);
            
            final Member faultCode = factory.createMember();
            faultCode.setName(XmlRpc.FAULT_CODE);
            final I4 code = factory.createI4();
            code.setI4(throwable.hashCode());
            faultCode.setValue(code);
            struct.getContent().add(factory.createMethodResponseFaultValueStructMember(faultCode));
            
            final Member faultString = factory.createMember();
            faultString.setName(XmlRpc.FAULT_STRING);
            final String string = factory.createString();
            string.setString(throwable.toString());
            faultString.setValue(string);
            struct.getContent().add(factory.createMethodResponseFaultValueStructMember(faultString));
            
            LOG.trace("Decoded {} into {}", throwable, response);
            return response;
        } else {
            return message;
        }
    }
    
}
