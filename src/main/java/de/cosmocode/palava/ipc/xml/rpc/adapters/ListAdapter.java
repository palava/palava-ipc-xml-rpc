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

import java.util.AbstractList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.ipc.xml.rpc.XmlRpc;
import de.cosmocode.palava.ipc.xml.rpc.generated.Array;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;
import de.cosmocode.palava.ipc.xml.rpc.generated.Value;
import de.cosmocode.palava.ipc.xml.rpc.generated.Array.Data;

/**
 * An {@link Array} to {@link List} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class ListAdapter implements Adapter<Value, List<Object>> {

    static final TypeLiteral<Adapter<Value, List<Object>>> LITERAL =
        new TypeLiteral<Adapter<Value, List<Object>>>() { };
        
    private final ObjectFactory factory;
    private final Adapter<Value, Object> objectAdapter;
    
    @Inject
    public ListAdapter(@XmlRpc ObjectFactory factory, Adapter<Value, Object> objectAdapter) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
        this.objectAdapter = Preconditions.checkNotNull(objectAdapter, "ObjectAdapter");
    }

    @Override
    public List<Object> decode(Value input) {
        Preconditions.checkNotNull(input, "Input");
        final List<Value> values = input.getArray().getData().getValue();
        return new AbstractList<Object>() {

            @Override
            public Object get(int index) {
                return objectAdapter.decode(values.get(index));
            }

            @Override
            public int size() {
                return values.size();
            }
            
        };
    }
    
    @Override
    public Value encode(List<Object> input) {
        Preconditions.checkNotNull(input, "Input");
        final Value value = factory.createValue();
        final Array array = factory.createArray();
        value.setArray(array);
        final Data data = factory.createArrayData();
        array.setData(data);
        
        for (Object object : input) {
            data.getValue().add(objectAdapter.encode(object));
        }
        
        return value;
    }
    
}
