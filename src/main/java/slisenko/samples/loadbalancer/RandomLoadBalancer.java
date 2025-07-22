package slisenko.samples.loadbalancer;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLoadBalancer implements ILoadBalancer {

    private final List<URI> hosts;

    public RandomLoadBalancer(List<URI> hosts) {
        // Reduce to the unique list
        Set<URI> unique = new HashSet<>(hosts);
        this.hosts = new ArrayList<>(unique);
    }

    @Override
    public URI getNextHost() {
        if (hosts.isEmpty()) {
            throw new LoadBalancerException("List of hosts is empty");
        }

        int index = ThreadLocalRandom.current().nextInt(hosts.size());
        return hosts.get(index);
    }
}
