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

package de.cosmocode.palava.ipc.xml.rpc.adapters;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.ipc.xml.rpc.XmlRpc;
import de.cosmocode.palava.ipc.xml.rpc.generated.Member;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodResponse.Fault;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;
import de.cosmocode.palava.ipc.xml.rpc.generated.Value;

/**
 * A {@link Fault.Value.Struct} to {@link Throwable} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class ThrowableAdapter implements Adapter<Fault.Value, Throwable> {

    static final TypeLiteral<Adapter<Fault.Value, Throwable>> LITERAL =
        new TypeLiteral<Adapter<Fault.Value, Throwable>>() { };

    private final ObjectFactory factory;
    
    @Inject
    public ThrowableAdapter(@XmlRpc ObjectFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }

    @Override
    public Throwable decode(Fault.Value input) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Fault.Value encode(Throwable throwable) {
        Preconditions.checkNotNull(throwable, "Throwable");
        final Fault.Value value = factory.createMethodResponseFaultValue();
        final Fault.Value.Struct struct = factory.createMethodResponseFaultValueStruct();
        value.setStruct(struct);
        
        final Member faultCode = factory.createMember();
        faultCode.setName(XmlRpc.FAULT_CODE);
        final Value code = factory.createValue();
        code.getContent().add(factory.createValueI4(throwable.hashCode()));
        faultCode.setValue(code);
        struct.getMember().add(faultCode);
        
        final Member faultString = factory.createMember();
        faultString.setName(XmlRpc.FAULT_STRING);
        final Value string = factory.createValue();
        string.getContent().add(factory.createValueString(throwable.toString()));
        faultString.setValue(string);
        struct.getMember().add(faultString);
        
        return value;
    }
    
}
