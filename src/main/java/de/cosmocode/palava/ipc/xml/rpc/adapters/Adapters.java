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

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * Utility class for {@link Adapter}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class Adapters {

    private Adapters() {
        
    }
    
    /**
     * Adapts the specified adapter to the {@link Function} interface using {@link Adapter#decode(Object)}.
     * 
     * @since 1.0
     * @param <F> source type
     * @param <T> target type
     * @param adapter the backing adapter
     * @return a function using the decode method of the given adapter
     * @throws NullPointerException if adapter is null
     */
    public static <F, T> Function<F, T> asDecodeFunction(final Adapter<? super F, ? extends T> adapter) {
        Preconditions.checkNotNull(adapter, "Adapter");
        return new Function<F, T>() {

            @Override
            public T apply(F from) {
                return adapter.decode(from);
            };
            
        };
    }
    
    /**
     * Adapts the specified adapter to the {@link Function} interface using {@link Adapter#encode(Object)}.
     * 
     * @since 1.0
     * @param <F> target type
     * @param <T> source type
     * @param adapter the backing adapter
     * @return a function using the encode method of the given adapter
     * @throws NullPointerException if adapter is null
     */
    public static <F, T> Function<T, F> asEncodeFunction(final Adapter<? extends F, ? super T> adapter) {
        Preconditions.checkNotNull(adapter, "Adapter");
        return new Function<T, F>() {
            
            @Override
            public F apply(T from) {
                return adapter.encode(from);
            };
            
        };
    }
    
}
