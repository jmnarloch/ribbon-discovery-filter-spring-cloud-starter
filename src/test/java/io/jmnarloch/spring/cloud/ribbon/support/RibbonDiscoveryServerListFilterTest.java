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
package io.jmnarloch.spring.cloud.ribbon.support;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import io.jmnarloch.spring.cloud.ribbon.api.DiscoveryEnabledServerListMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static io.jmnarloch.spring.cloud.ribbon.DiscoveryUtils.createInstanceInfo;
import static io.jmnarloch.spring.cloud.ribbon.DiscoveryUtils.servers;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests the {@link RibbonDiscoveryServerListFilter} class.
 *
 * @author Jakub Narloch
 */
public class RibbonDiscoveryServerListFilterTest {

    /**
     * The instance of the tested class.
     */
    private RibbonDiscoveryServerListFilter instance;

    /**
     * The matcher instance.
     */
    private DiscoveryEnabledServerListMatcher matcher;

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

        matcher = mock(DiscoveryEnabledServerListMatcher.class);
        instance = new RibbonDiscoveryServerListFilter(matcher);

        instanceInfo1 = createInstanceInfo("app1", Collections.<String, String>emptyMap());
        instanceInfo2 = createInstanceInfo("app2", Collections.<String, String>emptyMap());
        instanceInfo3 = createInstanceInfo("app3", Collections.<String, String>emptyMap());
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
    public void shouldMatchServers() {

        // given
        final List<Server> list = servers(instanceInfo1, instanceInfo2, instanceInfo3);

        // when
        instance.getFilteredListOfServers(list);

        // then
        verify(matcher).matchServers(Mockito.anyListOf(DiscoveryEnabledServer.class));
    }
}