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

import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import io.jmnarloch.spring.cloud.ribbon.api.DiscoveryEnabledServerListMatcher;
import io.jmnarloch.spring.cloud.ribbon.api.RibbonFilterContext;
import io.jmnarloch.spring.cloud.ribbon.support.RibbonFilterContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link DiscoveryEnabledServerListMatcher} matcher that matches the values based on the metadata entries in
 * the discovery service.
 *
 * @author Jakub Narloch
 */
public class MetadataServerListMatcher implements DiscoveryEnabledServerListMatcher {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DiscoveryEnabledServer> matchServers(List<DiscoveryEnabledServer> servers) {

        final ServerMatcher serverMatcher = new ServerMatcher(RibbonFilterContextHolder.getCurrentContext(), servers);
        return serverMatcher.match();
    }

    /**
     * A base attribute based matcher.
     *
     * @author Jakub Narloch
     */
    private static class ServerMatcher {

        private final RibbonFilterContext filterContext;

        private final List<DiscoveryEnabledServer> servers;

        public ServerMatcher(RibbonFilterContext filterContext, List<DiscoveryEnabledServer> servers) {
            this.filterContext = filterContext;
            this.servers = servers;
        }

        public List<DiscoveryEnabledServer> match() {
            final List<DiscoveryEnabledServer> matching = new ArrayList<>();
            final Set<Map.Entry<String, String>> attributes = Collections.unmodifiableSet(filterContext.getAttributes().entrySet());
            for (DiscoveryEnabledServer server : servers) {
                final Map<String, String> metadata = server.getInstanceInfo().getMetadata();
                if (metadata.entrySet().containsAll(attributes)) {
                    matching.add(server);
                }
            }
            return matching;
        }
    }
}
