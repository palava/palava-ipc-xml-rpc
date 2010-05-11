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
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.collect.Maps;

/**
 * Decoder which decodes xml-rpc {@link Document}s into {@link Map}s/{@link List}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
@Sharable
@ThreadSafe
final class XmlRpcDecoder extends OneToOneDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(XmlRpcDecoder.class);

    @Override
    protected Object decode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (message instanceof Document) {
            final Document document = Document.class.cast(message);
            // TODO single struct vs multiple arguments
            
            
            

            return Maps.newHashMap();
        } else {
            return message;
        }
    }    
    
}
