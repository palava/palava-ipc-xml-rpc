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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

import de.cosmocode.palava.ipc.xml.rpc.adapters.AdapterModule;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;

/**
 * Binds xml-rpc channel decoders/encoders/handlers.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class XmlRpcNettyModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(HttpRequestDecoder.class).in(Scopes.NO_SCOPE);
        binder.bind(HttpContentCompressor.class).in(Scopes.NO_SCOPE);
        binder.bind(HttpResponseEncoder.class).in(Singleton.class);
        binder.bind(HttpContentDecoder.class).in(Singleton.class);
        binder.bind(HttpContentEncoder.class).in(Singleton.class);
        binder.bind(JaxbDecoder.class).in(Singleton.class);
        binder.bind(JaxbEncoder.class).in(Singleton.class);
        binder.bind(MethodCallDecoder.class).in(Singleton.class);
        binder.bind(MethodResponseEncoder.class).in(Singleton.class);
        binder.bind(XmlRpcHandler.class).in(Singleton.class);
        
        binder.bind(ObjectFactory.class).annotatedWith(XmlRpc.class).to(ObjectFactory.class).in(Singleton.class);
        
        binder.install(new AdapterModule());
    }

    /**
     * Provides a stateful http chunk aggregator.
     * 
     * @since 1.0
     * @return a new {@link HttpChunkAggregator}
     */
    @Provides
    HttpChunkAggregator provideHttpChunkAggregator() {
        return new HttpChunkAggregator(1048576);
    }
    
    /**
     * Provides a channel pipeline.
     * 
     * @since 1.0
     * @param httpRequestDecoder the http request decoder
     * @param chunkAggregator the http chunk aggregator
     * @param httpResponseEncoder the http response encoder
     * @param compressor the http compressor
     * @param decompressor the http decompressor
     * @param httpContentDecoder the http content decoder
     * @param httpContentEncoder the http content encoder
     * @param jaxbDecoder the xml-rpc decoder
     * @param jaxbEncoder the xml-rpc encoder
     * @param callDecoder the call decoder
     * @param responseEncoder the response encoder
     * @param handler the xml-rpc handler
     * @return a new {@link ChannelPipeline}
     */
    @Provides
    ChannelPipeline provideChannelPipeline(
        HttpRequestDecoder httpRequestDecoder, HttpChunkAggregator chunkAggregator,
        HttpResponseEncoder httpResponseEncoder, 
        HttpContentCompressor compressor, HttpContentDecompressor decompressor,
        HttpContentDecoder httpContentDecoder, HttpContentEncoder httpContentEncoder,
        JaxbDecoder jaxbDecoder, JaxbEncoder jaxbEncoder,
        MethodCallDecoder callDecoder,
        MethodResponseEncoder responseEncoder,
        XmlRpcHandler handler) {
        return Channels.pipeline(
            httpRequestDecoder, chunkAggregator,
            httpResponseEncoder, 
            compressor, decompressor,
            httpContentDecoder, httpContentEncoder,
            jaxbDecoder, jaxbEncoder,
            callDecoder, responseEncoder,
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
     * Provides an {@link JAXBContext} for this package.
     * 
     * @since 1.0
     * @return a jaxb context
     * @throws JAXBException if creation failed
     */
    @Provides
    @Singleton
    @XmlRpc
    JAXBContext provideJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
    }

    /**
     * Provides a {@link Marshaller} of the given context.
     * 
     * @since 1.0
     * @param context the underlying context
     * @return a new marshaller
     * @throws JAXBException if creation failed
     */
    @Provides
    @XmlRpc
    Marshaller provideMarshaller(@XmlRpc JAXBContext context) throws JAXBException {
        return context.createMarshaller();
    }

    /**
     * Provides an {@link Unmarshaller} of the given context.
     * 
     * @since 1.0
     * @param context the underlying context
     * @return a new unmarshaller
     * @throws JAXBException if creation failed
     */
    @Provides
    @XmlRpc
    Unmarshaller provideUnmarshaller(@XmlRpc JAXBContext context) throws JAXBException {
        return context.createUnmarshaller();
    }
    
    /**
     * Provides a {@link DatatypeFactory}.
     * 
     * @since 1.0
     * @return a new {@link DatatypeFactory}
     * @throws DatatypeConfigurationException if creation failed
     */
    @Provides
    @Singleton
    @XmlRpc
    DatatypeFactory provideDatatypeFactory() throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance();
    }
    
}
