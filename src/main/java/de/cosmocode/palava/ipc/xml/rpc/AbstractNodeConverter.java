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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;

/**
 * Abstract base implementation of the {@link NodeConverter} interface.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
abstract class AbstractNodeConverter implements NodeConverter, Initializable, Disposable {

    private final Registry registry;

    @Inject
    public AbstractNodeConverter(Registry registry) {
        this.registry = Preconditions.checkNotNull(registry, "Registry");
    };
    
    @Override
    public final void initialize() throws LifecycleException {
        registry.register(NodeConverter.class, this);
    }
    
    @Override
    public boolean supports(Node value) {
        final NodeList list = value.getChildNodes();
        return list.getLength() == 1 && supportedNodeName().equals(list.item(0).getNodeName());
    }
    
    /**
     * Provides the suppported node name of this converter.
     * 
     * @since 1.0
     * @return the supported node name
     */
    protected abstract String supportedNodeName();
    
    @Override
    public final void dispose() throws LifecycleException {
        registry.remove(this);
    }
    
}
