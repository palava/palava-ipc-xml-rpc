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

/**
 * An adapter allows bidirectional conversion from one type to another.
 *
 * @since 1.0
 * @author Willi Schoenborn
 * @param <F> first type
 * @param <T> second type
 */
public interface Adapter<F, T> {
    
    /**
     * Decodes input of type F into type T. This method is usually applied lazily.
     * 
     * @since 1.0
     * @param input the input
     * @return an immutable instance of T
     * @throws NullPointerException if input is null
     */
    T decode(F input);
    
    /**
     * Encodes input of type T into type F.
     * 
     * @since 1.0
     * @param input the input
     * @return instante of F
     * @throws NullPointerException if input is null
     */
    F encode(T input);
    
}
