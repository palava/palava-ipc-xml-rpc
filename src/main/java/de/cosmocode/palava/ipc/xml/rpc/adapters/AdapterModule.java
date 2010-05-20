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

package de.cosmocode.palava.ipc.xml.rpc.adapters;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Binds all {@link Adapter} implementations.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class AdapterModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BooleanAdapter.LITERAL).to(BooleanAdapter.class).in(Singleton.class);
        bind(DateAdapter.LITERAL).to(DateAdapter.class).in(Singleton.class);
        bind(DoubleAdapter.LITERAL).to(DoubleAdapter.class).in(Singleton.class);
        bind(EntryAdapter.LITERAL).to(EntryAdapter.class).in(Singleton.class);
        bind(InputStreamAdapter.LITERAL).to(InputStreamAdapter.class).in(Singleton.class);
        bind(IntegerAdapter.LITERAL).to(IntegerAdapter.class).in(Singleton.class);
        bind(ListAdapter.LITERAL).to(ListAdapter.class).in(Singleton.class);
        bind(MapAdapter.LITERAL).to(MapAdapter.class).in(Singleton.class);
        bind(NumberAdapter.LITERAL).to(NumberAdapter.class).in(Singleton.class);
        bind(ObjectAdapter.LITERAL).to(ObjectAdapter.class).in(Singleton.class);
        bind(StringAdapter.LITERAL).to(StringAdapter.class).in(Singleton.class);
        bind(ThrowableAdapter.LITERAL).to(ThrowableAdapter.class).in(Singleton.class);
    }
    
}
