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

import javax.xml.bind.JAXBElement;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.ipc.xml.rpc.XmlRpc;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;
import de.cosmocode.palava.ipc.xml.rpc.generated.Value;

/**
 * A {@link Value} to {@link Boolean} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class BooleanAdapter implements Adapter<Value, Boolean> {

    static final TypeLiteral<Adapter<Value, Boolean>> LITERAL =
        new TypeLiteral<Adapter<Value, Boolean>>() { };

    private final ObjectFactory factory;
    
    @Inject
    public BooleanAdapter(@XmlRpc ObjectFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }

    @Override
    public Boolean decode(Value input) {
        Preconditions.checkNotNull(input, "Input");
        @SuppressWarnings("unchecked")
        final JAXBElement<Boolean> element = JAXBElement.class.cast(input.getContent().get(0));
        return element.getValue();
    }
    
    @Override
    public Value encode(Boolean input) {
        Preconditions.checkNotNull(input, "Input");
        final Value value = factory.createValue();
        value.getContent().add(factory.createValueBoolean(input));
        return value;
    }
    
}
