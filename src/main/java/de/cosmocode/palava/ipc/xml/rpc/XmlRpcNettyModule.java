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

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.ipc.protocol.Protocol;
import de.cosmocode.palava.ipc.xml.Xml;

/**
 * Binds xml-rpc channel decoders/encoders/handlers.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class XmlRpcNettyModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(JaxbDecoder.class).in(Singleton.class);
        binder.bind(JaxbEncoder.class).in(Singleton.class);
        binder.bind(XmlRpcHandler.class).in(Singleton.class);
    }
    
    /**
     * Provides a channel pipeline.
     * 
     * @since 1.0
     * @param pipeline the backing xml pipeline
     * @param rpcDecoder the xml-rpc decoder
     * @param rpcEncoder the xml-rpc encoder
     * @param handler the xml-rpc handler
     * @return a new {@link ChannelPipeline}
     */
    @Provides
    ChannelPipeline provideChannelPipeline(@Xml ChannelPipeline pipeline, JaxbDecoder rpcDecoder, 
        JaxbEncoder rpcEncoder, XmlRpcHandler handler) {
        pipeline.addLast("rpc-decoder", rpcDecoder);
        pipeline.addLast("rpc-encoder", rpcEncoder);
        pipeline.addLast("rpc-handler", handler);
        return pipeline;
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
    
}
