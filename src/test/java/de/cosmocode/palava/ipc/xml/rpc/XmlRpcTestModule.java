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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

import de.cosmocode.palava.concurrent.DefaultThreadProviderModule;
import de.cosmocode.palava.concurrent.ExecutorModule;
import de.cosmocode.palava.core.DefaultRegistryModule;
import de.cosmocode.palava.core.inject.TypeConverterModule;
import de.cosmocode.palava.core.lifecycle.LifecycleModule;
import de.cosmocode.palava.ipc.netty.Boss;
import de.cosmocode.palava.ipc.netty.NettyModule;
import de.cosmocode.palava.ipc.netty.Worker;
import de.cosmocode.palava.ipc.protocol.EchoProtocol;
import de.cosmocode.palava.ipc.protocol.Protocol;
import de.cosmocode.palava.ipc.xml.XmlNettyModule;
import de.cosmocode.palava.jmx.FakeMBeanServerModule;

/**
 * Test module.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class XmlRpcTestModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new TypeConverterModule());
        binder.install(new LifecycleModule());
        binder.install(new DefaultRegistryModule());
        binder.install(new FakeMBeanServerModule());
        binder.install(new ExecutorModule(Boss.class, Boss.NAME));
        binder.install(new ExecutorModule(Worker.class, Worker.NAME));
        binder.install(new DefaultThreadProviderModule());
        binder.install(new NettyModule());
        binder.install(new XmlNettyModule());
        binder.install(new XmlRpcNettyModule());
        Multibinder.newSetBinder(binder, Protocol.class).addBinding().to(EchoProtocol.class);
    }

    /**
     * Provides a document builder.
     * 
     * @since 1.0
     * @return a new {@link DocumentBuilder}
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created which 
     *         satisfies the configuration requested.
     */
    @Provides
    @Singleton
    DocumentBuilder provideDocumentBuilder() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }
    
    /**
     * Provides a transformer factory.
     * 
     * @since 1.0
     * @return a new {@link TransformerFactory}
     */
    @Provides
    @Singleton
    TransformerFactory provideTransformerFactory() {
        return TransformerFactory.newInstance();
    }
    
    /**
     * Provides a transformer.
     * 
     * @since 1.0
     * @param factory the factory used to create the instance
     * @return a new {@link Transformer}
     * @throws TransformerConfigurationException When it is not possible to create a Transformer instance.
     */
    @Provides
    Transformer provideTransformer(TransformerFactory factory) throws TransformerConfigurationException {
        return factory.newTransformer();
    }

}
