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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import de.cosmocode.collections.utility.Utility;
import de.cosmocode.collections.utility.UtilitySet;
import de.cosmocode.palava.ipc.AbstractIpcArguments;
import de.cosmocode.palava.ipc.IpcArguments;

/**
 * Xml-rpc implementation of the {@link IpcArguments} interface which supports
 * named and positional parameters.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class XmlRpcArguments extends AbstractIpcArguments {

    private final Map<String, Object> arguments = Maps.newHashMap();
    
    public XmlRpcArguments(List<?> params) {
        Preconditions.checkNotNull(params, "Params");
    
        int index = 0;
        for (Object param : params) {
            arguments.put(Integer.toString(index++), param);
        }
    }
    
    public XmlRpcArguments(Map<?, ?> params) {
        Preconditions.checkNotNull(params, "Params");
        
        for (Entry<?, ?> entry : params.entrySet()) {
            arguments.put(entry.getKey().toString(), entry.getValue());
        }
    }

    @Override
    public Object get(Object key) {
        return super.get(key == null ? null : key.toString());
    }
    
    @Override
    public UtilitySet<Entry<String, Object>> entrySet() {
        return Utility.asUtilitySet(arguments.entrySet());
    }

    @Override
    public Object put(String key, Object value) {
        return arguments.put(key, value);
    }

}
