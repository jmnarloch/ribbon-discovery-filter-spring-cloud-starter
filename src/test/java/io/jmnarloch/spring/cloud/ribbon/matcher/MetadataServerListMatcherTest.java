package io.jmnarloch.spring.cloud.ribbon.matcher;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link MetadataServerListMatcherTest} class.
 *
 * @author Jakub Narloch
 */
public class MetadataServerListMatcherTest {

    private MetadataServerListMatcher metadataServerListMatcher;

    @Test
    public void shouldMatchServers() {

        // given
        InstanceInfo instance1 = InstanceInfo.Builder.newBuilder().setAppName("app1").setMetadata();

        List<DiscoveryEnabledServer> list = servers();

        // when
        List<DiscoveryEnabledServer> matched = metadataServerListMatcher.matchServers(list);

        // then
        assertEquals(0, matched.size());
    }
}