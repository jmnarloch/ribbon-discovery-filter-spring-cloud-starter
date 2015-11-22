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

import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerListFilter;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import io.jmnarloch.spring.cloud.ribbon.api.DiscoveryEnabledServerListMatcher;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple delegating implementation of {@link ServerListFilter} that uses {@link DiscoveryEnabledServerListMatcher}
 * for determining the list of matching servers.
 *
 * @author Jakub Narloch
 */
public class RibbonDiscoveryServerListFilter implements ServerListFilter<Server> {

    /**
     * The matcher instance.
     */
    private final DiscoveryEnabledServerListMatcher matcher;

    /**
     * Creates new instance of {@link RibbonDiscoveryServerListFilter} with specific matcher.
     *
     * @param matcher the matcher
     */
    public RibbonDiscoveryServerListFilter(DiscoveryEnabledServerListMatcher matcher) {
        this.matcher = matcher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Server> getFilteredListOfServers(List<Server> servers) {
        final LinkedList<DiscoveryEnabledServer> discoveryServers = new LinkedList<>();
        for(Server server : servers) {
            if(server instanceof DiscoveryEnabledServer) {
                discoveryServers.add((DiscoveryEnabledServer) server);
            }
        }

        final List<? extends Server> matched = matcher.matchServers(discoveryServers);
        return (List<Server>) matched;
    }
}
