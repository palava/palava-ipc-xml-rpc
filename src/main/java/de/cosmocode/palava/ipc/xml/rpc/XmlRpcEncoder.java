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
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import redstone.xmlrpc.XmlRpcSerializer;

/**
 * Encoder which encodes {@link Map}s/{@link List}s into their xml-rpc counterparts.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class XmlRpcEncoder extends OneToOneEncoder {

    private static final Logger LOG = LoggerFactory.getLogger(XmlRpcEncoder.class);

    private final XmlRpcSerializer serializer;
    
    @Inject
    public XmlRpcEncoder(XmlRpcSerializer serializer) {
        this.serializer = Preconditions.checkNotNull(serializer, "Serializer");
    }
    
    @Override
    protected Object encode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (message instanceof Map<?, ?> || message instanceof List<?>) {
            final StringWriter writer = new StringWriter();
            LOG.trace("Serializing {} as xml", message);
            serializer.serialize(message, writer);
            return writer.toString();
        } else {
            return null;
        }
    }

}