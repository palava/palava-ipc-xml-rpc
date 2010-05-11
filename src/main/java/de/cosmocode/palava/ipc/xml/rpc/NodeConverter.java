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

package de.cosmocode.palava.ipc.xml.rpc;

import org.w3c.dom.Node;

/**
 * {@link Node} to {@link Object} converter to
 * provide xml-rpc compliant types.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
interface NodeConverter {
   
    /**
     * Checks whether this converter supports the specified value node.
     * 
     * @since 1.0
     * @param value the value node
     * @return true if this converter supports the specified node, false otherwise
     */
    boolean supports(Node value);

    /**
     * Converts the specified value into its java counterpart.
     * 
     * @since 1.0
     * @param value the value node
     * @param converter the *fallback* converter in case this
     *        converter supports recursive sub structures
     * @return the convertet object
     */
    Object convert(Node value, NodeConverter converter);
    
}
