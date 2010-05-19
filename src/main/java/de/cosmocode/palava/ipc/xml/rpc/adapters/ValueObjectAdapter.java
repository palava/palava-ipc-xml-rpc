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
import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.ipc.xml.rpc.generated.Value;

/**
 * A {@link Value} to {@link Object} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class ValueObjectAdapter implements Adapter<Value, Object> {

    static final TypeLiteral<Adapter<Value, Object>> LITERAL =
        new TypeLiteral<Adapter<Value, Object>>() { };

    private final ImmutableMap<Class<? extends Value>, Adapter<? super Value, ? extends Object>> decoders;
    private final ImmutableMap<Class<? extends Object>, Adapter<? extends Value, ? super Object>> encoders;
        
    // TODO depend on all adapters
    public ValueObjectAdapter() {
        this.decoders = ImmutableMap.of();
        this.encoders = ImmutableMap.of();
        // TODO register I4IntAdapter for byte, short, etc.
        // TODO register DoubleAdapter for double, float, bigdecimal, etc.
    }
    
    @Override
    public Object decode(Value input) {
        final Adapter<? super Value, ? extends Object> adapter = decoders.get(input.getClass());
        Preconditions.checkState(adapter != null, "No adapter configured for %s", input.getClass());
        return adapter.decode(input);
    }
    
    @Override
    public Value encode(Object input) {
        final Adapter<? extends Value, ? super Object> adapter = encoders.get(input.getClass());
        Preconditions.checkState(adapter != null, "No adapter configured for %s", input.getClass());
        return adapter.encode(input);
    }

}
