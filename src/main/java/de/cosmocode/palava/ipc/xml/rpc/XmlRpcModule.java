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

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import de.cosmocode.palava.core.Registry;

/**
 * Binds xml-rpc protocols/converters.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class XmlRpcModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(NodeConverter.class).to(NodeConverterService.class).in(Singleton.class);
        binder.bind(ArrayConverter.class).asEagerSingleton();
        // TODO add base64 converter
        binder.bind(BooleanConverter.class).asEagerSingleton();
        binder.bind(DateTimeIso8601Converter.class).asEagerSingleton();
        binder.bind(DoubleConverter.class).asEagerSingleton();
        binder.bind(I4Converter.class).asEagerSingleton();
        binder.bind(IntConverter.class).asEagerSingleton();
        binder.bind(StringConverter.class).asEagerSingleton();
        binder.bind(StructConverter.class).asEagerSingleton();
    }
    
    /**
     * Provides all registered node converters.
     * 
     * @since 1.0
     * @param registry the current registry
     * @return all registered node converters
     */
    @Provides
    @Singleton
    Iterable<NodeConverter> provideConverters(Registry registry) {
        return registry.getListeners(NodeConverter.class);
    }

}
