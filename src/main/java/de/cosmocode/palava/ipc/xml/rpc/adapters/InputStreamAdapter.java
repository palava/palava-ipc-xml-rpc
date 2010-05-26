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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBElement;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.ipc.xml.rpc.XmlRpc;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;
import de.cosmocode.palava.ipc.xml.rpc.generated.Value;

/**
 * An {@link Value} to {@link InputStream} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class InputStreamAdapter implements Adapter<Value, InputStream> {

    static final TypeLiteral<Adapter<Value, InputStream>> LITERAL =
        new TypeLiteral<Adapter<Value, InputStream>>() { };

    private final ObjectFactory factory;
    
    @Inject
    public InputStreamAdapter(@XmlRpc ObjectFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }
    
    @Override
    public InputStream decode(Value input) {
        Preconditions.checkNotNull(input, "Input");
        @SuppressWarnings("unchecked")
        final JAXBElement<byte[]> element = JAXBElement.class.cast(input.getContent().get(0));
        return new ByteArrayInputStream(element.getValue());
    }
    
    @Override
    public Value encode(InputStream input) {
        Preconditions.checkNotNull(input, "Input");
        final Value value = factory.createValue();
        final byte[] bytes;
        
        try {
            bytes = ByteStreams.toByteArray(input);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        
        value.getContent().add(factory.createValueBase64(bytes));
        return value;
    }

}
