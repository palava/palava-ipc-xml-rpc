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

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.ipc.xml.rpc.XmlRpc;
import de.cosmocode.palava.ipc.xml.rpc.generated.Member;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;
import de.cosmocode.palava.ipc.xml.rpc.generated.Struct;
import de.cosmocode.palava.ipc.xml.rpc.generated.Value;

/**
 * A {@link Struct} to {@link Object} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class MapAdapter implements Adapter<Value, Map<String, Object>> {

    static final TypeLiteral<Adapter<Value, Map<String, Object>>> LITERAL =
        new TypeLiteral<Adapter<Value, Map<String, Object>>>() { };

    private final ObjectFactory factory;
    private final Adapter<Member, Entry<String, Object>> entryAdapter;
    private final Function<Member, Entry<String, Object>> entryDecoder;
    
    @Inject
    public MapAdapter(@XmlRpc ObjectFactory factory, Adapter<Member, Entry<String, Object>> entryAdapter) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
        this.entryAdapter = Preconditions.checkNotNull(entryAdapter, "EntryAdapter");
        this.entryDecoder = Adapters.asDecoder(entryAdapter);
    }

    @Override
    public Map<String, Object> decode(Value input) {
        Preconditions.checkNotNull(input, "Input");
        final List<Member> members = input.getStruct().getMember();
        return new AbstractMap<String, Object>() {

            @Override
            public Set<Entry<String, Object>> entrySet() {
                return new AbstractSet<Entry<String, Object>>() {

                    @Override
                    public Iterator<Entry<String, Object>> iterator() {
                        return Iterators.unmodifiableIterator(Iterators.transform(members.iterator(), entryDecoder));
                    }

                    @Override
                    public int size() {
                        return members.size();
                    }
                    
                };
            }
            
        };
    }
    
    @Override
    public Value encode(Map<String, Object> input) {
        Preconditions.checkNotNull(input, "Input");
        final Value value = factory.createValue();
        final Struct struct = factory.createStruct();
        value.setStruct(struct);
        
        for (Entry<String, Object> entry : input.entrySet()) {
            final Member member = entryAdapter.encode(entry);
            struct.getMember().add(member);
        }
        
        return value;
    }
    
}
