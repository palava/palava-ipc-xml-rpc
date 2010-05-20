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

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.ipc.xml.rpc.XmlRpc;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;
import de.cosmocode.palava.ipc.xml.rpc.generated.Value;

/**
 * A {@link Object} to {@link Object} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class ObjectAdapter implements Adapter<Value, Object> {
    
    private static final Logger LOG = LoggerFactory.getLogger(ObjectAdapter.class);

    static final TypeLiteral<Adapter<Value, Object>> LITERAL =
        new TypeLiteral<Adapter<Value, Object>>() { };

    private final Value nullValue;
    
    private final Map<Class<?>, Adapter<Value, ?>> adapters;
    
    @Inject
    public ObjectAdapter(
        @XmlRpc ObjectFactory factory,
        Adapter<Value, List<Object>> listAdapter,
        Adapter<Value, InputStream> streamAdapter,
        Adapter<Value, Boolean> booleanAdapter,
        Adapter<Value, Date> dateAdapter,
        Adapter<Value, Double> doubleAdapter,
        Adapter<Value, Integer> integerAdapter,
        Adapter<Value, String> stringAdapter,
        Adapter<Value, Map<String, Object>> mapAdapter) {
        
        Preconditions.checkNotNull(factory, "Factory");
        this.nullValue = factory.createValue();
        this.nullValue.setString("null");
        
        final Builder<Class<?>, Adapter<Value, ?>> builder = ImmutableMap.builder();
        
        builder.put(List.class, listAdapter);
        builder.put(InputStream.class, streamAdapter);
        builder.put(Boolean.class, booleanAdapter);
        builder.put(Date.class, dateAdapter);
        builder.put(Double.class, doubleAdapter);
        builder.put(Integer.class, integerAdapter);
        builder.put(String.class, stringAdapter);
        builder.put(Map.class, mapAdapter);
        
        this.adapters = builder.build();
        
        // TODO register composed adapters
        
        // TODO register IntegerAdapter for byte, short, etc.
        // TODO register DoubleAdapter for double, float, bigdecimal, etc.
    }
    
    private Adapter<Value, ?> getDecoder(Value value) {
        if (value.getString() != null) {
            return adapters.get(String.class);
        } else if (value.getArray() != null) {
            return adapters.get(List.class);
        } else if (value.getStruct() != null) {
            return adapters.get(Map.class);
        } else if (value.getI4() != null) {
            return adapters.get(Integer.class);
        } else if (value.getInt() != null) {
            return adapters.get(Integer.class);
        } else if (value.getDouble() != null) {
            return adapters.get(Double.class);
        } else if (value.isBoolean() != null) {
            return adapters.get(Boolean.class);
        } else if (value.getDateTimeIso8601() != null) {
            return adapters.get(Date.class);
        } else if (value.getBase64() != null) {
            return adapters.get(InputStream.class);
        } else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    private Adapter<Value, Object> getEncoder(Class<?> type) {
        for (Entry<Class<?>, Adapter<Value, ?>> entry : adapters.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                return (Adapter<Value, Object>) entry.getValue();
            }
        }
        return null;
    }
    
    @Override
    public Object decode(Value input) {
        Preconditions.checkNotNull(input, "Input");
        final Adapter<Value, ?> adapter = getDecoder(input);
        Preconditions.checkState(adapter != null, "No adapter configured for %s", input.getClass());
        return adapter.decode(input);
    }
    
    @Override
    public Value encode(Object input) {
        if (input == null) return nullValue;
        final Adapter<Value, ? super Object> adapter = getEncoder(input.getClass());
        if (adapter == null) {
            Preconditions.checkState(!(input instanceof java.lang.String), "No adapter configured for Strings");
            LOG.debug("No adapter configured for {}, using toString", input.getClass());
            // might be an endless loop
            return encode(input.toString());
        }
        Preconditions.checkState(adapter != null, "No adapter configured for %s", input.getClass());
        return adapter.encode(input);
    }

}