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

import java.net.URL;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * {@link Provider} implementation for the {@link XmlRpc xmlrpc} {@link Schema schema}.
 *
 * @since 1.0,
 * @author Willi Schoenborn
 */
final class XmlRpcSchemaProvider implements Provider<Schema> {

    private final SchemaFactory factory;
    
    private URL url = Resources.getResource("xmlrpc.xsd");

    @Inject
    public XmlRpcSchemaProvider(@XmlRpc SchemaFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }
    
    @Inject(optional = true)
    public void setUrl(@Named(XmlRpc.SCHEMA) URL url) {
        this.url = Preconditions.checkNotNull(url, "Url");
    }
    
    @Override
    public Schema get() {
        try {
            return factory.newSchema(url);
        } catch (SAXException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
}
