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

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import de.cosmocode.palava.ipc.IpcArguments;
import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcConnection;
import de.cosmocode.palava.ipc.protocol.DetachedCall;
import de.cosmocode.palava.scope.AbstractScopeContext;

/**
 * Xml-rpc implementation of the {@link IpcCall} interface.
 *
 * @since 
 * @author Willi Schoenborn
 */
final class XmlRpcCall extends AbstractScopeContext implements DetachedCall {

    private final IpcArguments arguments;
    
    private final String methodName;
    
    private Map<Object, Object> context;
    
    private IpcConnection connection;
    
    public XmlRpcCall(String methodName, IpcArguments arguments) {
        this.methodName = Preconditions.checkNotNull(methodName, "MethodName");
        this.arguments = Preconditions.checkNotNull(arguments, "Arguments");
    }
    
    public String getMethodName() {
        return methodName;
    }

    @Override
    protected Map<Object, Object> context() {
        if (context == null) {
            context = Maps.newHashMap();
        }
        return context;
    }

    @Override
    public IpcArguments getArguments() {
        return arguments;
    }

    @Override
    public void attachTo(IpcConnection c) {
        this.connection = Preconditions.checkNotNull(c, "Connection");
    }
    
    @Override
    public IpcConnection getConnection() {
        Preconditions.checkState(connection != null, "Not yet attached to a session");
        return connection;
    }

}
