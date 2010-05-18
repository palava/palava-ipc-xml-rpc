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

import java.io.OutputStream;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.ipc.netty.ChannelBuffering;

/**
 * Encoder which encodes {@link JAXBElement}s into {@link ChannelBuffer}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
@Sharable
@ThreadSafe
final class JaxbEncoder extends OneToOneEncoder {

    private final Marshaller marshaller;
    
    @Inject
    public JaxbEncoder(@XmlRpc Marshaller marshaller) {
        this.marshaller = Preconditions.checkNotNull(marshaller, "Marshaller");
    }
    
    @Override
    protected Object encode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        final OutputStream stream = ChannelBuffering.asOutputStream(buffer);
        marshaller.marshal(message, stream);
        return buffer;
    }

}
