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

import java.text.ParseException;
import java.util.Date;

import org.w3c.dom.Node;

import com.google.inject.Inject;

import de.cosmocode.commons.DateFormats;
import de.cosmocode.palava.core.Registry;

/**
 * {@link NodeConverter} for {@link Date}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class DateTimeIso8601Converter extends AbstractNodeConverter {
    
    @Inject
    public DateTimeIso8601Converter(Registry registry) {
        super(registry);
    }
    
    @Override
    protected String supportedNodeName() {
        return XmlRpc.DATE_TIME_ISO_8601;
    }

    @Override
    public Object convert(Node value, NodeConverter converter) {
        try {
            return DateFormats.ISO_8061.parse(value.getFirstChild().getTextContent());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
