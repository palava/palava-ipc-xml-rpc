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

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import de.cosmocode.palava.core.Registry;

/**
 * {@link NodeConverter} for arrays.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class ArrayConverter extends AbstractNodeConverter {

    @Inject
    public ArrayConverter(Registry registry) {
        super(registry);
    }

    @Override
    protected String supportedNodeName() {
        return XmlRpc.ARRAY;
    }

    @Override
    public Object convert(Node value, NodeConverter converter) {
        final List<Object> list = Lists.newArrayList();
        final Node array = value.getFirstChild();
        Preconditions.checkArgument(array.getChildNodes().getLength() == 1, "Expected only %s", XmlRpc.DATA);
        final Node data = array.getFirstChild();
        final NodeList values = data.getChildNodes();
        
        for (int i = 0; i < values.getLength(); i++) {
            final Node dataValue = values.item(i);
            final Object converted = converter.convert(dataValue, converter);
            list.add(converted);
        }
        
        return list;
    }

}
