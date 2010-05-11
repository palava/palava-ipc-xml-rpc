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

import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.Registry.Key;
import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;
import de.cosmocode.palava.ipc.protocol.DetachedConnection;
import de.cosmocode.palava.ipc.protocol.Protocol;
import de.cosmocode.palava.ipc.protocol.ProtocolException;

/**
 * Xml-Rpc implementation of the Protocol interface.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class XmlRpcProtocol implements Protocol, Initializable, Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(XmlRpcProtocol.class);
    
    private final Registry registry;

    private final DocumentBuilder builder;
    
    @Inject
    public XmlRpcProtocol(Registry registry, DocumentBuilder builder) {
        this.registry = Preconditions.checkNotNull(registry, "Registry");
        this.builder = Preconditions.checkNotNull(builder, "Builder");
    }

    @Override
    public void initialize() throws LifecycleException {
        registry.register(Key.get(Protocol.class, XmlRpc.class), this);
    }
    
    @Override
    public boolean supports(Object request) {
        return true || request instanceof Map<?, ?> || request instanceof List<?>;
    }

    @Override
    public Object process(Object request, DetachedConnection connection) throws ProtocolException {
        
        
        
        // TODO Auto-generated method stub
        return request;
    }

    @Override
    public Object onError(Throwable t, Object request) {
        // TODO error
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void dispose() throws LifecycleException {
        registry.remove(this);
    }

}
