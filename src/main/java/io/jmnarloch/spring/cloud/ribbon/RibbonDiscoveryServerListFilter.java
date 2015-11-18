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

import com.netflix.loadbalancer.ServerListFilter;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import java.util.List;

/**
 * A simple delegating implementation of {@link ServerListFilter} that uses {@link DiscoveryEnabledServerListMatcher}
 * for determining the list of matching servers.
 *
 * @author Jakub Narloch
 */
public class RibbonDiscoveryServerListFilter implements ServerListFilter<DiscoveryEnabledServer> {

    private final DiscoveryEnabledServerListMatcher matcher;

    public RibbonDiscoveryServerListFilter(DiscoveryEnabledServerListMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public List<DiscoveryEnabledServer> getFilteredListOfServers(List<DiscoveryEnabledServer> servers) {
        return matcher.matchServers(servers);
    }
}
