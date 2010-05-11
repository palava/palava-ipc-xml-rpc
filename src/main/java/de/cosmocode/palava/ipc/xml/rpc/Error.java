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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Preconditions;

/**
 * Static utility class for error handling.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class Error {

    private Error() {
        
    }

    /**
     * Adds the specified throwable to the given method response.
     * 
     * @since 1.0
     * @param document the target document
     * @param t the occured throwable
     * @throws NullPointerException if document or t is null
     */
    public static void set(Document document, Throwable t) {
        Preconditions.checkNotNull(document, "Document");
        Preconditions.checkNotNull(t, "Throwable");
        
        final Element methodResponse = document.createElement(XmlRpc.METHOD_RESPONSE);
        document.appendChild(methodResponse);
        
        final Element fault = document.createElement(XmlRpc.FAULT);
        methodResponse.appendChild(fault);
        
        final Element value = document.createElement(XmlRpc.VALUE);
        fault.appendChild(value);
        
        final Element struct = document.createElement(XmlRpc.STRUCT);
        fault.appendChild(struct);
        
        final Element faultCodeMember = document.createElement(XmlRpc.MEMBER);
        struct.appendChild(faultCodeMember);
        
        final Element faultCodeName = document.createElement(XmlRpc.NAME);
        faultCodeName.setTextContent(XmlRpc.FAULT_CODE);
        faultCodeMember.appendChild(faultCodeName);
        
        final Element faultCodeValue = document.createElement(XmlRpc.VALUE);
        final Element faultCodeI4 = document.createElement(XmlRpc.I4);
        faultCodeI4.setTextContent(Integer.toString(t.hashCode()));
        faultCodeValue.appendChild(faultCodeI4);
        faultCodeMember.appendChild(faultCodeValue);
        
        final Element faultStringMember = document.createElement(XmlRpc.MEMBER);
        struct.appendChild(faultStringMember);
        
        final Element faultStringName = document.createElement(XmlRpc.NAME);
        faultStringName.setTextContent(XmlRpc.FAULT_STRING);
        faultStringMember.appendChild(faultStringName);
        
        final Element faultStringValue = document.createElement(XmlRpc.VALUE);
        final Element faultStringString = document.createElement(XmlRpc.STRING);
        faultStringString.setTextContent(t.getMessage() == null ? t.getClass().getName() : t.getMessage());
        faultStringValue.appendChild(faultStringString);
        faultStringMember.appendChild(faultStringValue);
    }
    
}
