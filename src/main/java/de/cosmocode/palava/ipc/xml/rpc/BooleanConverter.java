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
 * {@link NodeConverter} for {@link Boolean}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class BooleanConverter extends AbstractNodeConverter {

    @Inject
    public BooleanConverter(Registry registry) {
        super(registry);
    }

    @Override
    protected String supportedNodeName() {
        return XmlRpc.BOOLEAN;
    }
    
    @Override
    public Object convert(Node node, NodeConverter converter) {
        final String value = node.getChildNodes().item(0).getTextContent();
        if (XmlRpc.TRUE.equals(value)) {
            return Boolean.TRUE;
        } else if (XmlRpc.FALSE.equals(value)) {
            return Boolean.FALSE;
        } else {
            throw new IllegalArgumentException(String.format("Excepted %s to contain %s or %s but was %s",
                XmlRpc.BOOLEAN, XmlRpc.TRUE, XmlRpc.FALSE, value));
        }
    }

}
