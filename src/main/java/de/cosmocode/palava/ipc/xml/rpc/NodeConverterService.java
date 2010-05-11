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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * A service which manages {@link NodeConverter}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class NodeConverterService implements NodeConverter {
    
    private static final Logger LOG = LoggerFactory.getLogger(NodeConverterService.class);

    private final Iterable<NodeConverter> converters;
    
    @Inject
    public NodeConverterService(Iterable<NodeConverter> converters) {
        this.converters = Preconditions.checkNotNull(converters, "Converters");
    }
    
    /**
     * Internal predicate implementation.
     *
     * @since 1.0
     * @author Willi Schoenborn
     */
    private static final class Supports implements Predicate<NodeConverter> {
        
        private final Node node;
        
        public Supports(Node node) {
            this.node = node;
        }

        @Override
        public boolean apply(NodeConverter input) {
            return input.supports(node);
        }
        
    }
    
    @Override
    public boolean supports(Node node) {
        return Iterables.any(converters, new Supports(node));
    }
    
    @Override
    public Object convert(Node node, NodeConverter self) {
        LOG.trace("Searching converter for node {} in {}", node, converters);
        // TODO check for supported first! or catch exception and rethrow
        return Iterables.find(converters, new Supports(node)).convert(node, this);
    }

}
