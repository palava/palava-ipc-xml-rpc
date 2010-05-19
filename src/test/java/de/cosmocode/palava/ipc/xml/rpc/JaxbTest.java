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

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Resources;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.core.Framework;
import de.cosmocode.palava.core.Palava;
import de.cosmocode.palava.ipc.xml.rpc.adapters.Adapter;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodCall;
import de.cosmocode.palava.ipc.xml.rpc.generated.Param;
import de.cosmocode.palava.ipc.xml.rpc.generated.Value;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodResponse.Fault;
import de.cosmocode.palava.ipc.xml.rpc.generated.MethodResponse.Fault.Value.Struct;

/**
 * Tests jaxb un/marshalling.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class JaxbTest {

    /**
     * Tests marshalling.
     * 
     * @since 1.0
     */
    @Test
    public void marshal() {
        final Framework framework = Palava.newFramework();
        framework.start();
        
        try {
            final Adapter<Fault.Value, Throwable> adapter;
            adapter = framework.getInstance(Key.get(new TypeLiteral<Adapter<Fault.Value, Throwable>>() { }));
            final Throwable throwable = new Throwable();
            final Struct struct = adapter.encode(throwable).getStruct();
            Assert.assertEquals(2, struct.getMember().size());
            final Value value = struct.getMember().get(0).getValue();
            Assert.assertTrue(value.getI4() != null); 
            Assert.assertEquals(throwable.hashCode(), value.getI4().intValue()); 
        } finally {
            framework.stop();
        }
    }
    
    /**
     * Tests marshalling.
     * 
     * @since 1.0
     * @throws JAXBException should not happen.
     */
    @Test
    public void unmarshal() throws JAXBException {
        final Framework framework = Palava.newFramework();
        framework.start();
        
        try {
            final Unmarshaller unmarshaller = framework.getInstance(Key.get(Unmarshaller.class, XmlRpc.class));
            final URL url = Resources.getResource("methodCall.xml");
            final Object unmarshalled = unmarshaller.unmarshal(url);
            Assert.assertTrue(unmarshalled instanceof MethodCall);
            final MethodCall methodCall = MethodCall.class.cast(unmarshalled);
            Assert.assertEquals(Echo.class.getName(), methodCall.getMethodName());
            Assert.assertEquals(2, methodCall.getParams().getParam().size());
            final Param first = methodCall.getParams().getParam().get(0);
            Assert.assertTrue(first.getValue().getString() != null);
            final String name = first.getValue().getString();
            Assert.assertEquals(Echo.class.getName(), name);
            final Param second = methodCall.getParams().getParam().get(1);
            Assert.assertTrue(second.getValue().isBoolean() != null);
            final Boolean truth = second.getValue().isBoolean();
            Assert.assertTrue(truth);
        } finally {
            framework.stop();
        }
    }
    
}
