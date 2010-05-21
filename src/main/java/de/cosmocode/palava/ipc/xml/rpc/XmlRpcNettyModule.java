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

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

import de.cosmocode.palava.ipc.netty.ConnectionManager;
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
        binder.bind(HttpResponseEncoder.class).in(Singleton.class);
        binder.bind(HttpHandler.class).in(Singleton.class);
        binder.bind(JaxbDecoder.class).in(Singleton.class);
        binder.bind(JaxbEncoder.class).in(Singleton.class);
        binder.bind(MethodCallDecoder.class).in(Singleton.class);
        binder.bind(MethodResponseEncoder.class).in(Singleton.class);
        binder.bind(XmlRpcHandler.class).in(Singleton.class);
        
        binder.bind(Key.get(Schema.class, XmlRpc.class)).toProvider(XmlRpcSchemaProvider.class).in(Singleton.class);
        binder.bind(Marshaller.class).annotatedWith(XmlRpc.class).toProvider(XmlRpcMarshallerProvider.class);
        binder.bind(Unmarshaller.class).annotatedWith(XmlRpc.class).toProvider(XmlRpcUnmarshallerProvider.class);
        
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
     * @param manager the connection manager
     * @param httpRequestDecoder the http request decoder
     * @param chunkAggregator the http chunk aggregator
     * @param httpResponseEncoder the http response encoder
     * @param httpHandler the http handler
     * @param jaxbDecoder the xml-rpc decoder
     * @param jaxbEncoder the xml-rpc encoder
     * @param callDecoder the call decoder
     * @param responseEncoder the response encoder
     * @param handler the xml-rpc handler
     * @return a new {@link ChannelPipeline}
     */
    @Provides
    @XmlRpc
    ChannelPipeline provideChannelPipeline(
        ConnectionManager manager,
        HttpRequestDecoder httpRequestDecoder, HttpChunkAggregator chunkAggregator,
        HttpResponseEncoder httpResponseEncoder, 
        HttpHandler httpHandler,
        JaxbDecoder jaxbDecoder, JaxbEncoder jaxbEncoder,
        MethodCallDecoder callDecoder,
        MethodResponseEncoder responseEncoder,
        XmlRpcHandler handler) {
        return Channels.pipeline(
            manager,
            httpRequestDecoder, chunkAggregator,
            httpResponseEncoder, 
            httpHandler,
            jaxbDecoder, jaxbEncoder,
            callDecoder, responseEncoder,
            handler
        );
    }
    
    /**
     * Provides an object factory.
     * 
     * @since 1.0
     * @return a new {@link ObjectFactory}
     */
    @Provides
    @Singleton
    @XmlRpc
    ObjectFactory provideObjectFactory() {
        return new ObjectFactory();
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
     * Provides a {@link SchemaFactory}.
     * 
     * @since 1.0
     * @return a new schema factory
     */
    @Provides
    @Singleton
    @XmlRpc
    SchemaFactory provideSchemaFactory() {
        return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }
    
}
