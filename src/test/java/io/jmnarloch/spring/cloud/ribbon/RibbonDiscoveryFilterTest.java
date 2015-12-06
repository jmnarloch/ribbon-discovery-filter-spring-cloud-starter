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
package io.jmnarloch.spring.cloud.ribbon;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import io.jmnarloch.spring.cloud.ribbon.support.RibbonFilterContextHolder;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Demonstrates the usage of this component.
 *
 * @author Jakub Narloch
 */
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@WebAppConfiguration
@IntegrationTest
@SpringApplicationConfiguration(classes = {RibbonDiscoveryFilterTest.Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class RibbonDiscoveryFilterTest {

    @LoadBalanced
    @Autowired
    RestOperations restOperations;

    @After
    public void tearDown() throws Exception {

        RibbonFilterContextHolder.clearCurrentContext();
    }

    @Test
    public void shouldRouteRequests() {

        // when
        ResponseEntity<String> response = restOperations.getForEntity("http://local/message", String.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldRouteRequestWithContextAttributes() {

        // given
        RibbonFilterContextHolder.getCurrentContext().add("version", "1.0");

        // when
        ResponseEntity<String> response = restOperations.getForEntity("http://local/message", String.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldRouteRequestWithMultipleContextAttributes() {

        // given
        RibbonFilterContextHolder.getCurrentContext().add("version", "1.0").add("variant", "A");

        // when
        ResponseEntity<String> response = restOperations.getForEntity("http://local/message", String.class);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailForNonMatchingServer() {

        // given
        RibbonFilterContextHolder.getCurrentContext().add("version", "1.1");

        // then
        restOperations.getForEntity("http://local/message", String.class);
    }

    @RestController
    @EnableEurekaClient
    @SpringBootApplication
    public static class Application {

        @RequestMapping(method = GET, value = "/message")
        public ResponseEntity<String> getMessage() {

            return ResponseEntity.ok().body("Success");
        }

        @Bean
        public ServerList<?> ribbonServerList() {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("version", "1.0");
            metadata.put("variant", "A");
            InstanceInfo instanceInfo = InstanceInfo.Builder.newBuilder()
                    .setAppName("local")
                    .setHostName("localhost")
                    .setPort(8761)
                    .setMetadata(metadata)
                    .build();
            return new StaticServerList<>(Arrays.asList(new DiscoveryEnabledServer(instanceInfo, false)));
        }
    }

    private static class StaticServerList<T extends Server> implements ServerList<T> {

        private final List<T> serverList;

        public StaticServerList(List<T> serverList) {
            this.serverList = serverList;
        }

        @Override
        public List<T> getInitialListOfServers() {
            return serverList;
        }

        @Override
        public List<T> getUpdatedListOfServers() {
            return serverList;
        }
    }
}
