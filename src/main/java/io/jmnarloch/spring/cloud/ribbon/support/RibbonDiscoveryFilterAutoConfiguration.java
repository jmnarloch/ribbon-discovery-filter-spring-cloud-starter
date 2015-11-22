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
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import io.jmnarloch.spring.cloud.ribbon.api.DiscoveryEnabledServerListMatcher;
import io.jmnarloch.spring.cloud.ribbon.matcher.MetadataServerListMatcher;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Ribbon discovery auto configuration.
 *
 * @author Jakub Narloch
 */
@Configuration
@ConditionalOnClass(DiscoveryEnabledNIWSServerList.class)
@AutoConfigureBefore(RibbonAutoConfiguration.class)
public class RibbonDiscoveryFilterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(DiscoveryEnabledServerListMatcher.class)
    public ServerListFilter<Server> ribbonDiscoveryServerListFilter(DiscoveryEnabledServerListMatcher matcher) {
        return new RibbonDiscoveryServerListFilter(matcher);
    }

    @Configuration
    @ConditionalOnProperty(value = "ribbon.filter.metadata.enabled", matchIfMissing = true)
    public static class MetadataRibbonFilterConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public MetadataServerListMatcher metadataServerListMatcher() {
            return new MetadataServerListMatcher();
        }
    }
}
