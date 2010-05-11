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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.common.base.Predicate;
import com.google.inject.BindingAnnotation;

/**
 * Binding annotation and utility class for xml rpc communication.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.METHOD,
    ElementType.PARAMETER
})
@BindingAnnotation
public @interface XmlRpc {

    Predicate<Object> OR_ANY = new Predicate<Object>() {
        
        @Override
        public boolean apply(Object input) {
            return input == null || input == XmlRpc.class;
        }
        
    };
    
    String METHOD_CALL = "methodCall";
    
    String METHOD_NAME = "methodName";
    
    String PARAMS = "params";
    
    String PARAM = "params";
    
    String VALUE = "value";
    
    String I4 = "i4";
    
    String INT = "int";
    
    String BOOLEAN = "boolean";
    
    String STRING = "string";
    
    String DOUBLE = "double";
    
    String DATE_TIME_ISO_8601 = "dateTime.iso8601";
    
    String BASE_64 = "base64";
    
    String STRUCT = "struct";
    
    String MEMBER = "member";
    
    String NAME = "name";
    
    String ARRAY = "array";

    String DATA = "data";
    
    String METHOD_RESPONSE = "methodResponse";
    
    String FAULT = "fault";
    
    String FAULT_CODE = "faultCode";
    
    String FAULT_STRING = "faultString";
    
}
