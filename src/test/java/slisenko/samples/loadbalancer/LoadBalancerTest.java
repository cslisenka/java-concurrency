package slisenko.samples.loadbalancer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LoadBalancerTest {

    List<URI> hosts = List.of(
        URI.create("http://aaa.local:1234"),
        URI.create("http://bbb.local:1234"),
        URI.create("http://ccc.local:1234")
    );

    Map<URI, Integer> counters ;

    @BeforeEach
    void cleanupCounters() {
        counters = new HashMap<>();
        hosts.forEach(h -> counters.put(h, 0));
    }

    @Test
    public void testGetNextHost_RoundRobin() {
        ILoadBalancer lb = new RoundRobinLoadBalancer(hosts);

        // When
        for (int i = 0; i < 300_000; i++) {
            URI host = lb.getNextHost();
            assertNotNull(host);
            counters.compute(host, (key, counter) -> counter + 1);
        }

        // Assert hosts are equally balanced
        hosts.forEach(h -> assertEquals(100_000, counters.get(h)));
    }

    @Test
    public void testGetNextHost_random() {
        ILoadBalancer lb = new RandomLoadBalancer(hosts);

        // When
        for (int i = 0; i < 300_000; i++) {
            URI host = lb.getNextHost();
            assertNotNull(host);
            counters.compute(host, (key, counter) -> counter + 1);
        }

        // Assert hosts are equally balanced
        int range = 10_000;
        counters.forEach((uri, counter) -> assertTrue(Math.abs(100_000 - counter) <= range));
    }

    @Test
    public void testEmptyHosts() {
        // Given lb with no hosts
        ILoadBalancer rr = new RandomLoadBalancer(List.of());
        LoadBalancerException e = assertThrows(LoadBalancerException.class, rr::getNextHost);
        assertEquals("List of hosts is empty", e.getMessage());
    }
}
