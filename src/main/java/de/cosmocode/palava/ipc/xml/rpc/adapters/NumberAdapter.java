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
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;
import de.cosmocode.palava.ipc.xml.rpc.generated.Value;

/**
 * A {@link Value} to {@link Number} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class NumberAdapter implements Adapter<Value, Number> {

    static final TypeLiteral<Adapter<Value, Number>> LITERAL =
        new TypeLiteral<Adapter<Value, Number>>() { };

    private final ObjectFactory factory;
    
    @Inject
    public NumberAdapter(@XmlRpc ObjectFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }

    @Override
    public Number decode(Value input) {
        Preconditions.checkNotNull(input, "Input");
        final ValueType type = ValueType.of(input);
        switch (type) {
            case I4: {
                return input.getI4();
            }
            case INT: {
                return input.getInt();
            }
            case DOUBLE: {
                return input.getDouble();
            }
            default: {
                throw new IllegalArgumentException(type.toString());
            }
        }
    }
    
    @Override
    public Value encode(Number input) {
        Preconditions.checkNotNull(input, "Input");
        final Value value = factory.createValue();
        
        if (input instanceof Byte) {
            value.setI4(input.intValue());
        } else if (input instanceof Short) {
            value.setI4(input.intValue());
        } else if (input instanceof Integer) {
            value.setI4(input.intValue());
        } else {
            value.setDouble(input.doubleValue());
        }
        
        return value;
    }
    
}
