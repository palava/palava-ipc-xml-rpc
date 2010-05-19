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
import de.cosmocode.palava.ipc.xml.rpc.generated.I4;
import de.cosmocode.palava.ipc.xml.rpc.generated.Member;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;
import de.cosmocode.palava.ipc.xml.rpc.generated.String;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodResponse.Fault.Value.Struct;

/**
 * A {@link Struct} to {@link Throwable} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class StructThrowableAdapter implements Adapter<Struct, Throwable> {

    static final TypeLiteral<Adapter<Struct, Throwable>> LITERAL =
        new TypeLiteral<Adapter<Struct, Throwable>>() { };

    private final ObjectFactory factory;
    
    @Inject
    public StructThrowableAdapter(@XmlRpc ObjectFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }

    @Override
    public Throwable decode(Struct input) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Struct encode(Throwable throwable) {
        final Struct struct = factory.createMethodResponseFaultValueStruct();
        
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
        
        return struct;
    }
    
}
