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
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

import de.cosmocode.palava.ipc.xml.rpc.generated.Array;
import de.cosmocode.palava.ipc.xml.rpc.generated.Struct;
import de.cosmocode.palava.ipc.xml.rpc.generated.Value;

/**
 * Defines the different types a {@link Value} can represent.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
enum ValueType {

    ARRAY(List.class),
    
    BASE64(InputStream.class),
    
    BOOLEAN(Boolean.class),
    
    DATETIME_ISO801(Date.class),
    
    DOUBLE(Double.class),
    
    I4(Integer.class),
    
    STRING(String.class),
    
    STRUCT(Map.class);

    private static final QName DATETIME_ISO8601_NAME = QName.valueOf("dateTime.iso8601");
    
    private final Class<?> type;
    
    private ValueType(Class<?> type) {
        this.type = Preconditions.checkNotNull(type, "Type");
    }
    
    public Class<?> getType() {
        return type;
    }
    
    /**
     * Returns the type associated with a given value.
     * 
     * @since 1.0
     * @param value the value
     * @return the {@link ValueType} representing the type of the specified value
     * @throws NullPointerException if value is null
     */
    public static ValueType of(Value value) {
        Preconditions.checkNotNull(value, "Value");
        final Serializable first = Iterables.getOnlyElement(value.getContent());
        final JAXBElement<?> element;
        final Object content;
        
        if (first instanceof JAXBElement<?>) {
            element = JAXBElement.class.cast(first);
            content = element.getValue();
        } else {
            element = null;
            content = first;
        }
        
        // ordering from the most used to the least
        if (first instanceof String) {
            return STRING;
        } else if (DATETIME_ISO8601_NAME.equals(element.getName())) {
            return DATETIME_ISO801;
        } else {
            return of(value, content);
        }
    }
    
    private static ValueType of(Value value, Object content) {
        if (content instanceof String) {
            return  STRING;
        } else if (content instanceof Boolean) {
            return BOOLEAN;
        } else if (content instanceof Integer) {
            return I4;
        } else if (content instanceof Struct) {
            return STRUCT;
        } else if (content instanceof Array) {
            return ARRAY;
        } else if (content instanceof Double) {
            return DOUBLE;
        } else if (content instanceof byte[]) {
            return BASE64;
        } else {
            throw new IllegalArgumentException(String.format("%s is of unknown type", value));
        }
    }
    
}
