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

import org.junit.Assert;

import com.google.inject.Singleton;

import de.cosmocode.palava.ipc.IpcCall;
import de.cosmocode.palava.ipc.IpcCommand;
import de.cosmocode.palava.ipc.IpcCommandExecutionException;


/**
 * Internal {@link IpcCommand} implementation which checks that the same session is used.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
@Singleton
final class SessionIdCheckCommand implements IpcCommand {
    
    private String lastSessionId;
    
    @Override
    public void execute(IpcCall call, Map<String, Object> result) throws IpcCommandExecutionException {
        if (lastSessionId == null) {
            lastSessionId = call.getConnection().getSession().getSessionId();
        } else {
            Assert.assertEquals(lastSessionId, call.getConnection().getSession().getSessionId());
        }
    }
    
}
