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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.core.Framework;
import de.cosmocode.palava.core.Palava;

/**
 * Tests xml-rpc.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class XmlRpcTest implements UnitProvider<XmlRpcClient> {

    private Framework framework;
    
    @Override
    public XmlRpcClient unit() {
        final XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        
        try {
            config.setServerURL(new URL("http://localhost:8081"));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        
        final XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        return client;
    }
    
    /**
     * Starts the framework prior each test.
     */
    @Before
    public void start() {
        framework = Palava.newFramework();
        framework.start();
    }
    
    /**
     * Tests xmlrpc with multiple params.
     * 
     * @throws XmlRpcException should not happen
     */
    @Test
    public void params() throws XmlRpcException {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        final Date now = calendar.getTime();
        final List<?> list = Arrays.asList((int) System.currentTimeMillis(), Maps.newHashMap(), now);
        final Map<?, ?> expected = ImmutableMap.of(
            "0", list.get(0),
            "1", Maps.newHashMap(),
            "2", now
        );
        final Object returnedList = unit().execute(Echo.class.getName(), list);
        Assert.assertEquals(expected, returnedList);
    }
    
    /**
     * Tests xmlrpc with a single map/struct param.
     * 
     * @throws XmlRpcException should not happen
     */
    @Test
    public void struct() throws XmlRpcException {
        final Map<?, ?> map = ImmutableMap.of(
//            "name", getClass().getName(),
            "truth", Boolean.valueOf(Math.random() > 0.5)
        );
        final Object returnedMap = unit().execute(Echo.class.getName(), Collections.singletonList(map));
        Assert.assertEquals(map, returnedMap);
    }
    
    /**
     * Stops the framework after each test.
     */
    @After
    public void stop() {
        framework.stop();
        framework = null;
    }
    
}
