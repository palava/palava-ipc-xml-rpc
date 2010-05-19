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
 * A {@link Value} to {@link String} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class StringAdapter implements Adapter<Value, String> {

    static final TypeLiteral<Adapter<Value, String>> LITERAL =
        new TypeLiteral<Adapter<Value, String>>() { };

    private final ObjectFactory factory;
    
    @Inject
    public StringAdapter(@XmlRpc ObjectFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }

    @Override
    public String decode(Value input) {
        Preconditions.checkNotNull(input, "Input");
        return input.getString();
    }
    
    @Override
    public Value encode(String input) {
        Preconditions.checkNotNull(input, "Input");
        final Value value = factory.createValue();
        value.setString(input);
        return value;
    }
    
}
