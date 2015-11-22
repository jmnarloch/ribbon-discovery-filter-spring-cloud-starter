/**
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.spring.cloud.ribbon.matcher;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import io.jmnarloch.spring.cloud.ribbon.support.RibbonFilterContextHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.jmnarloch.spring.cloud.ribbon.DiscoveryUtils.createInstanceInfo;
import static io.jmnarloch.spring.cloud.ribbon.DiscoveryUtils.servers;
import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link MetadataServerListMatcherTest} class.
 *
 * @author Jakub Narloch
 */
public class MetadataServerListMatcherTest {

    /**
     * The instance of the tested class.
     */
    private MetadataServerListMatcher instance;

    /**
     * Instance info.
     */
    private InstanceInfo instanceInfo1;

    /**
     * Instance info.
     */
    private InstanceInfo instanceInfo2;

    /**
     * Instance info.
     */
    private InstanceInfo instanceInfo3;

    /**
     * Sets up the test environment.
     *
     * @throws Exception if any error occurs
     */
    @Before
    public void setUp() throws Exception {

        instance = new MetadataServerListMatcher();

        final Map<String, String> versioned = Collections.singletonMap("version", "1.0");
        final Map<String, String> versionedAndDisabled = new HashMap<>(versioned);
        versionedAndDisabled.put("enabled", "false");

        instanceInfo1 = createInstanceInfo("app1", Collections.<String, String>emptyMap());
        instanceInfo2 = createInstanceInfo("app2", versioned);
        instanceInfo3 = createInstanceInfo("app3", versionedAndDisabled);
    }

    /**
     * Tears down the test environment.
     *
     * @throws Exception if any error occurs
     */
    @After
    public void tearDown() throws Exception {

        RibbonFilterContextHolder.clearCurrentContext();
    }

    @Test
    public void shouldMatchAllServersWhenNoAttributeIsSpecified() {

        // given
        final List<DiscoveryEnabledServer> list = servers(instanceInfo1, instanceInfo2, instanceInfo3);

        // when
        final List<DiscoveryEnabledServer> matched = instance.matchServers(list);

        // then
        assertEquals(3, matched.size());
    }

    @Test
    public void shouldMatchOnlyServicesBySpecificCriteria() {

        // given
        RibbonFilterContextHolder.getCurrentContext().add("version", "1.0");
        final List<DiscoveryEnabledServer> list = servers(instanceInfo1, instanceInfo2, instanceInfo3);

        // when
        final List<DiscoveryEnabledServer> matched = instance.matchServers(list);

        // then
        assertEquals(2, matched.size());
    }

    @Test
    public void shouldMatchExactlyServicesBySpecificCriteria() {

        // given
        RibbonFilterContextHolder.getCurrentContext()
                .add("version", "1.0")
                .add("enabled", "false");
        final List<DiscoveryEnabledServer> list = servers(instanceInfo1, instanceInfo2, instanceInfo3);

        // when
        final List<DiscoveryEnabledServer> matched = instance.matchServers(list);

        // then
        assertEquals(1, matched.size());
        assertEquals("APP3", matched.get(0).getInstanceInfo().getAppName());
    }
}