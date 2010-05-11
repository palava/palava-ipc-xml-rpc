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

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;
import javax.print.Doc;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import de.cosmocode.palava.ipc.IpcArguments;

/**
 * Decoder which decodes xml-rpc {@link Document}s into {@link XmlRpcCall}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
@Sharable
@ThreadSafe
final class XmlRpcDecoder extends OneToOneDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(XmlRpcDecoder.class);
    
    private static final TransformerFactory FACTORY = TransformerFactory.newInstance();

    private final NodeConverter converter;
    
    @Inject
    public XmlRpcDecoder(NodeConverter converter) {
        this.converter = Preconditions.checkNotNull(converter, "Converter");
    }

    @Override
    protected Object decode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (message instanceof Document) {
            final Document root = Document.class.cast(message);
            
            if (LOG.isTraceEnabled()) {
                prettyLog(root);
            }
            
            final Node methodCall = methodCall(root);
            final String methodName = methodName(methodCall);
            final NodeList params = params(methodCall).getChildNodes();
            
            final IpcArguments arguments;
            
            if (params.getLength() == 1) {
                // single param, could be a struct
                final Node param = params.item(0);
                final Node value = param.getFirstChild();
                Preconditions.checkNotNull(value, "Expected %s to be present", XmlRpc.VALUE);
                
                if (isStruct(value)) {
                    LOG.trace("Xml-rpc method call will use named parameters");
                    final Map<?, ?> map = Map.class.cast(converter.convert(value, converter));
                    
                    arguments = new XmlRpcArguments(map);
                } else {
                    final List<Object> singletonList = Lists.newArrayListWithCapacity(1);
                    singletonList.add(converter.convert(value, converter));
                    
                    arguments = new XmlRpcArguments(singletonList);
                }
            } else {
                LOG.trace("Xml-rpc method call will use positional parameters");
                final List<Object> list = Lists.newArrayList();
                
                for (int i = 0; i < params.getLength(); i++) {
                    final Node param = params.item(i);
                    final Node value = param.getFirstChild();
                    Preconditions.checkNotNull(value, "Expected %s to be present", XmlRpc.VALUE);
                    final Object object = converter.convert(value, converter);
                    list.add(object);
                }
                
                arguments = new XmlRpcArguments(list);
            }
            
            return new XmlRpcCall(methodName, arguments);
        } else {
            return message;
        }
    }
    
    private Node methodCall(Document root) {
        final NodeList list = root.getElementsByTagName(XmlRpc.METHOD_CALL);
        Preconditions.checkArgument(list.getLength() == 1, "Excepted one %s", XmlRpc.METHOD_CALL);
        return list.item(0);
    }
    
    private String methodName(Node methodCall) {
        final NodeList list = methodCall.getChildNodes();
        Preconditions.checkArgument(list.getLength() == 2, 
            "Expected %s to contain only two element", XmlRpc.METHOD_CALL);
        
        for (int i = 0; i < list.getLength(); i++) {
            final Node node = list.item(i);
            if (XmlRpc.METHOD_NAME.equals(node.getNodeName())) {
                return node.getTextContent();
            }
        }
     
        throw new IllegalArgumentException(String.format("Expected %s to be set", XmlRpc.METHOD_NAME));
    }
    
    private Node params(Node methodCall) {
        final NodeList list = methodCall.getChildNodes();

        for (int i = 0; i < list.getLength(); i++) {
            final Node node = list.item(i);
            if (XmlRpc.PARAMS.equals(node.getNodeName())) {
                return node;
            }
        }
        
        throw new IllegalArgumentException(String.format("Expected %s to be set", XmlRpc.PARAMS));
    }
    
    private boolean isStruct(Node node) {
        return XmlRpc.STRUCT.equals(node.getNodeName());
    }
    
    private void prettyLog(Document document) {
        final Transformer transformer;
        
        try {
            transformer = FACTORY.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new AssertionError(e);
        }
        
        final Source source = new DOMSource(document);
        final Writer xml = new StringWriter();
        
        try {
            transformer.transform(source, new StreamResult(xml));
        } catch (TransformerException e) {
            throw new AssertionError(e);
        }
        
        LOG.trace("Incoming xml-rpc {}", xml);
    }
    
}
