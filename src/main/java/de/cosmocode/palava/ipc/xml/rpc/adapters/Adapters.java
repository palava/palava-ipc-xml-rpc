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
    public static <F, T> Function<F, T> asDecoder(final Adapter<? super F, ? extends T> adapter) {
        Preconditions.checkNotNull(adapter, "Adapter");
        return new Function<F, T>() {

            @Override
            public T apply(F from) {
                return adapter.decode(from);
            };
            
            @Override
            public String toString() {
                return String.format("Adapters.asDecoder(%s)", adapter);
            }
            
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
    public static <F, T> Function<T, F> asEncoder(final Adapter<? extends F, ? super T> adapter) {
        Preconditions.checkNotNull(adapter, "Adapter");
        return new Function<T, F>() {
            
            @Override
            public F apply(T from) {
                return adapter.encode(from);
            };
            
            @Override
            public String toString() {
                return String.format("Adapters.asEncoder(%s)", adapter);
            }
            
        };
    }
    
    /**
     * Creates an inverse version of the specified {@link Adapter} which uses
     * {@link Adapter#decode(Object)} when asked to encode and vice versa.
     * 
     * @since 1.0
     * @param <F> target type
     * @param <T> source type
     * @param adapter the backing adapter
     * @return an adapter which is a flipped version of the given one
     * @throws NullPointerException if adapter is null
     */
    public static <F, T> Adapter<T, F> flip(final Adapter<F, T> adapter) {
        Preconditions.checkNotNull(adapter, "Adapter");
        return new Adapter<T, F>() {
            
            @Override
            public F decode(T input) {
                return adapter.encode(input);
            };
            
            @Override
            public T encode(F input) {
                return adapter.decode(input);
            };
            
            @Override
            public String toString() {
                return String.format("Adapters.flip(%s)", adapter);
            }
            
        };
    }
    
    /**
     * Composes two {@link Adapter}s into one.
     * 
     * @since 1.0
     * @param <F> source type
     * @param <T> intermediate type
     * @param <S> target type
     * @param left left adapter
     * @param right right adapter
     * @return an adapter chaining left and right
     * @throws NullPointerException if left or right is null
     */
    public static <F, T, S> Adapter<F, S> compose(final Adapter<F, T> left, final Adapter<T, S> right) {
        Preconditions.checkNotNull(left, "Left");
        Preconditions.checkNotNull(right, "Right");
        return new Adapter<F, S>() {
            
            @Override
            public S decode(F input) {
                return right.decode(left.decode(input));
            };

            @Override
            public F encode(S input) {
                return left.encode(right.encode(input));
            };

            @Override
            public String toString() {
                return String.format("Adapters.compose(%s, %s)", left, right);
            }
            
        };
    }
    
    /**
     * Composes an {@link Adapter} and a {@link Function} into a one-way encoder, an adapter which
     * supports {@link Adapter#encode(Object)} but not {@link Adapter#decode(Object)}.
     * 
     * @since 1.0
     * @param <F> the source type
     * @param <T> the intermediate type
     * @param <S> the target type
     * @param adapter the backing adapter
     * @param function the encoding function
     * @return a composed adapter using the specified adapter and function for encoding 
     */
    public static <F, T, S> Adapter<F, S> composeEncoder(final Adapter<F, T> adapter, 
        final Function<? super S, ? extends T> function) {
        Preconditions.checkNotNull(adapter, "Adapter");
        Preconditions.checkNotNull(function, "Function");
        return new Adapter<F, S>() {
            
            @Override
            public S decode(F input) {
                throw new UnsupportedOperationException();
            };
            
            @Override
            public F encode(S input) {
                return adapter.encode(function.apply(input));
            };
            
            @Override
            public String toString() {
                return String.format("Adapters.compose(%s, %s)", adapter, function);
            }
            
        };
    }
    
}
