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

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import com.google.common.base.Charsets;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.ipc.protocol.Protocol;
import de.cosmocode.palava.ipc.xml.Xml;
import de.cosmocode.palava.ipc.xml.XmlFrameDecoder;

/**
 * Binds xml-rpc channel decoders/encoders/handlers.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class XmlRpcModule implements Module {

    @Override
    public void configure(Binder binder) {
        // frame decoders are stateful
        binder.bind(XmlFrameDecoder.class).in(Scopes.NO_SCOPE);
        binder.bind(StringDecoder.class).toInstance(new StringDecoder(Charsets.UTF_8));
        binder.bind(StringEncoder.class).toInstance(new StringEncoder(Charsets.UTF_8));
        binder.bind(XmlRpcDecoder.class).in(Singleton.class);
        binder.bind(XmlRpcEncoder.class).in(Singleton.class);
        binder.bind(XmlRpcHandler.class).in(Singleton.class);
    }

    /**
     * Provides the channel pipeline for netty.
     * 
     * @since 1.0 
     * @param frameDecoder the frame decoder
     * @param stringDecoder the string decoder
     * @param stringEncoder the string encoder
     * @param rpcDecoder the rpc decoder
     * @param rpcEncoder the rpc encoder
     * @param handler the xml-rpc handler
     * @return a new {@link ChannelPipeline}
     */
    @Provides
    ChannelPipeline provideChannelPipeline(XmlFrameDecoder frameDecoder, StringDecoder stringDecoder,
        StringEncoder stringEncoder, XmlRpcDecoder rpcDecoder, XmlRpcEncoder rpcEncoder, XmlRpcHandler handler) {
        return Channels.pipeline(
            frameDecoder,
            stringDecoder, stringEncoder,
            rpcDecoder, rpcEncoder,
            handler
        );
    }
    
    /**
     * Provides a channel pipeline factory.
     * 
     * @since 1.0
     * @param provider the backing provider
     * @return a new {@link ChannelPipelineFactory}
     */
    @Provides
    @Singleton
    ChannelPipelineFactory provideChannelPipelineFactory(final Provider<ChannelPipeline> provider) {
        return new ChannelPipelineFactory() {
            
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                return provider.get();
            }
            
        };
    }
    
    /**
     * Provides all xml protocols.
     * 
     * @since 1.0 
     * @param registry the current registry
     * @return iterable of xml protocols
     */
    @Provides
    @Singleton
    @Xml
    Iterable<Protocol> provideProtocols(Registry registry) {
        return registry.find(Protocol.class, Xml.OR_ANY);
    }
    
}
