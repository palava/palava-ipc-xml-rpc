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

import java.io.InputStream;
import java.io.StringWriter;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;

import de.cosmocode.palava.ipc.netty.ChannelBuffering;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodCall;

/**
 * Decoder which decodes {@link ChannelBuffer}s into {@link MethodCall}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
@Sharable
@ThreadSafe
final class JaxbDecoder extends OneToOneDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(JaxbDecoder.class);
    
    private final Provider<Unmarshaller> unmarshaller;
    private final Provider<Marshaller> marshaller;
    
    @Inject
    public JaxbDecoder(@XmlRpc Provider<Unmarshaller> unmarshaller, @XmlRpc Provider<Marshaller> marshaller) {
        this.unmarshaller = Preconditions.checkNotNull(unmarshaller, "Unmarshaller");
        this.marshaller = Preconditions.checkNotNull(marshaller, "Marshaller");
    }

    @Override
    protected Object decode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
        if (message instanceof ChannelBuffer) {
            final ChannelBuffer buffer = ChannelBuffer.class.cast(message);
            final InputStream stream = ChannelBuffering.asInputStream(buffer);
            final Object unmarshalled = unmarshaller.get().unmarshal(stream);
            
            if (LOG.isTraceEnabled()) {
                final StringWriter writer = new StringWriter();
                final Marshaller m = marshaller.get();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                m.marshal(unmarshalled, writer);
                LOG.trace("Incoming xml: {}", writer);
            }
            
            LOG.trace("Unmarshalled {}", unmarshalled);
            return unmarshalled;
        } else {
            return message;
        }
    }
    
}
