/**
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.spring.cloud.ribbon;

import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class MetadataServerListMatcher implements DiscoveryEnabledServerListMatcher {

    @Override
    public List<DiscoveryEnabledServer> matchServers(List<DiscoveryEnabledServer> servers) {

        final ServerMatcher serverMatcher = new ServerMatcher(RibbonDiscoveryFilterContextHolder.getCurrentContext(), servers);
        return serverMatcher.match();
    }

    private static class ServerMatcher {

        private final RibbonDiscoveryFilterContext filterContext;

        private final List<DiscoveryEnabledServer> servers;

        public ServerMatcher(RibbonDiscoveryFilterContext filterContext, List<DiscoveryEnabledServer> servers) {
            this.filterContext = filterContext;
            this.servers = servers;
        }

        public List<DiscoveryEnabledServer> match() {
            final List<DiscoveryEnabledServer> matching = new ArrayList<>();
            for(DiscoveryEnabledServer server : servers) {
                final Map<String, String> metadata = server.getInstanceInfo().getMetadata();
                for(Map.Entry<String, String> entry : filterContext.getAttributes().entrySet()) {
                    String value = metadata.get(entry.getKey());
                    if(value != null && value.equals(entry.getValue())) {
                        matching.add(server);
                    }
                }
            }
            return matching;
        }
    }
}
