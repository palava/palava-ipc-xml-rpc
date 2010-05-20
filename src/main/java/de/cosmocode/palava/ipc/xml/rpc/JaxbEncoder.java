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
import java.io.StringWriter;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.Marshaller;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;

import de.cosmocode.palava.ipc.netty.ChannelBuffering;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodResponse;

/**
 * Encoder which encodes {@link MethodResponse}s into {@link ChannelBuffer}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
@Sharable
@ThreadSafe
final class JaxbEncoder extends OneToOneEncoder {

    private static final Logger LOG = LoggerFactory.getLogger(JaxbEncoder.class);
    
    private final Provider<Marshaller> provider;
    
    @Inject
    public JaxbEncoder(@XmlRpc Provider<Marshaller> provider) {
        this.provider = Preconditions.checkNotNull(provider, "Provider");
    }
    
    @Override
    protected Object encode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (message instanceof MethodResponse) {
            final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
            final OutputStream stream = ChannelBuffering.asOutputStream(buffer);
            final Marshaller marshaller = provider.get();
            marshaller.marshal(message, stream);

            if (LOG.isTraceEnabled()) {
                final StringWriter writer = new StringWriter();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(message, writer);
                LOG.trace("Xml-Rpc response:\n{}", writer);
            }
            
            return buffer;
        } else {
            return message;
        }
    }

}
