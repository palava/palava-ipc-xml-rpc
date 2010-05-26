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
 * A {@link Double} to {@link Double} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class DoubleAdapter implements Adapter<Value, Double> {

    static final TypeLiteral<Adapter<Value, Double>> LITERAL =
        new TypeLiteral<Adapter<Value, Double>>() { };

    private final ObjectFactory factory;
    
    @Inject
    public DoubleAdapter(@XmlRpc ObjectFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }

    @Override
    public Double decode(Value input) {
        Preconditions.checkNotNull(input, "Input");
        return Double.class.cast(input.getContent().get(0));
    }
    
    @Override
    public Value encode(Double input) {
        Preconditions.checkNotNull(input, "Input");
        final Value value = factory.createValue();
        value.getContent().add(factory.createValueDouble(input));
        return value;
    }
    
}
