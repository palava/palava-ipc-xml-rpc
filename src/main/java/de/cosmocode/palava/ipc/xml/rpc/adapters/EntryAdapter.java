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

import java.util.Map.Entry;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.ipc.xml.rpc.XmlRpc;
import de.cosmocode.palava.ipc.xml.rpc.generated.Member;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;
import de.cosmocode.palava.ipc.xml.rpc.generated.Value;

/**
 * A {@link Member} to {@link Entry} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class EntryAdapter implements Adapter<Member, Entry<String, Object>> {

    static final TypeLiteral<Adapter<Member, Entry<String, Object>>> LITERAL =
        new TypeLiteral<Adapter<Member, Entry<String, Object>>>() { };
    
    private final ObjectFactory factory;
    private final Adapter<Value, Object> objectAdapter;
    
    @Inject
    public EntryAdapter(@XmlRpc ObjectFactory factory, Adapter<Value, Object> objectAdapter) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
        this.objectAdapter = Preconditions.checkNotNull(objectAdapter, "ObjectAdapter");
    }
    
    @Override
    public Entry<String, Object> decode(final Member input) {
        Preconditions.checkNotNull(input, "Input");
        return new Entry<String, Object>() {
            
            @Override
            public String getKey() {
                return input.getName();
            }
            
            @Override
            public Object getValue() {
                return objectAdapter.decode(input.getValue());
            }
            
            @Override
            public Object setValue(Object value) {
                throw new UnsupportedOperationException();
            }
            
        };
    }
    
    @Override
    public Member encode(Entry<String, Object> input) {
        Preconditions.checkNotNull(input, "Input");
        final Member member = factory.createMember();
        member.setName(input.getKey());
        final Value value = objectAdapter.encode(input.getValue());
        member.setValue(value);
        return member;
    }
    
}
