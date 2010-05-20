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

import com.google.common.base.Preconditions;

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
    
    INT(Integer.class),
    
    STRING(String.class),
    
    STRUCT(Map.class);
    
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
        // ordering from the most used to the least
        if (value.getString() != null) {
            return STRING;
        } else if (value.isBoolean() != null) {
            return BOOLEAN;
        } else if (value.getStruct() != null) {
            return STRUCT;
        } else if (value.getI4() != null) {
            return I4;
        } else if (value.getInt() != null) {
            return INT;
        } else if (value.getArray() != null) {
            return ARRAY;
        } else if (value.getDouble() != null) {
            return DOUBLE;
        } else if (value.getDateTimeIso8601() != null) {
            return DATETIME_ISO801;
        } else if (value.getBase64() != null) {
            return BASE64;
        } else {
            // FIXME this should be considered a string
            throw new IllegalArgumentException(String.format("%s is of unknown type", value));
        }
    }
    
}
