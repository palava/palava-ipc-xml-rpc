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

import com.google.inject.Inject;

import de.cosmocode.palava.core.Registry;

/**
 * {@link NodeConverter} for {@link Integer}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class IntConverter extends AbstractNodeConverter {

    @Inject
    public IntConverter(Registry registry) {
        super(registry);
    }

    @Override
    protected String supportedNodeName() {
        return XmlRpc.I4;
    }
    
    @Override
    public Object convert(Node value, NodeConverter converter) {
        return Integer.parseInt(value.getFirstChild().getTextContent());
    }

}
