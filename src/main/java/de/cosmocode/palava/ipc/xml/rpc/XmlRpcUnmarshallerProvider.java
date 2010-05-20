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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * {@link Provider} implementation for {@link Unmarshaller}s.
 *
 * @since 
 * @author Willi Schoenborn
 */
final class XmlRpcUnmarshallerProvider implements Provider<Unmarshaller> {

    private final JAXBContext context;
    
    private final Schema schema;
    
    private boolean validate;

    @Inject
    public XmlRpcUnmarshallerProvider(@XmlRpc JAXBContext context, @XmlRpc Schema schema) {
        this.context = Preconditions.checkNotNull(context, "Context");
        this.schema = Preconditions.checkNotNull(schema, "Schema");
    }

    @Inject(optional = true)
    public void setValidate(@Named(XmlRpc.VALIDATE) boolean validate) {
        this.validate = validate;
    }
    
    @Override
    public Unmarshaller get() {
        final Unmarshaller unmarshaller;
        
        try {
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new IllegalArgumentException(e);
        }
        
        if (validate) {
            unmarshaller.setSchema(schema);
        }
        
        return unmarshaller;
    }
    
}
