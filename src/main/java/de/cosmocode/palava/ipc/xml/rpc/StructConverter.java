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

import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import de.cosmocode.palava.core.Registry;

/**
 * {@link NodeConverter} for structs.
 *
 * @since 
 * @author Willi Schoenborn
 */
public final class StructConverter extends AbstractNodeConverter {

    @Inject
    public StructConverter(Registry registry) {
        super(registry);
    }

    @Override
    protected String supportedNodeName() {
        return XmlRpc.STRUCT;
    }

    @Override
    public Object convert(Node value, NodeConverter converter) {
        final Map<String, Object> map = Maps.newLinkedHashMap();
        final Node struct = value.getFirstChild();
        final NodeList members = struct.getChildNodes();
        
        for (int i = 0; i < members.getLength(); i++) {
            final Node member = members.item(i);
            Preconditions.checkArgument(XmlRpc.MEMBER.equals(member.getNodeName()),
                 "Expected %s but was %s", XmlRpc.MEMBER, member.getNodeName());
            Preconditions.checkArgument(member.getChildNodes().getLength() == 2, 
                "Expected %s to contain only 2 elements", XmlRpc.MEMBER);
            
            final String name = nameOf(member);
            final Node memberValue = valueOf(member);
            final Object converted = converter.convert(memberValue, converter);
            
            map.put(name, converted);
        }
        
        return map;
    }
    
    private String nameOf(Node member) {
        final NodeList list = member.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            final Node node = list.item(i);
            if (XmlRpc.NAME.equals(node.getNodeName())) return node.getTextContent();
        }
        throw new IllegalArgumentException(String.format("Expected %s", XmlRpc.NAME));
    }
    
    private Node valueOf(Node member) {
        final NodeList list = member.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            final Node node = list.item(i);
            if (XmlRpc.VALUE.equals(node.getNodeName())) return node;
        }
        throw new IllegalArgumentException(String.format("Expected %s", XmlRpc.VALUE));
    }
    
}
