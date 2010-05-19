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
        bind(ValueObjectAdapter.LITERAL).to(ValueObjectAdapter.class).in(Singleton.class);
        bind(MemberEntryAdapter.LITERAL).to(MemberEntryAdapter.class).in(Singleton.class);
        bind(StructMapAdapter.LITERAL).to(StructMapAdapter.class).in(Singleton.class);
        bind(StructThrowableAdapter.LITERAL).to(StructThrowableAdapter.class).in(Singleton.class);
    }
    
}
