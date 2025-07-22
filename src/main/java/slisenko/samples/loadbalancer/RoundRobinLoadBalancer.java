package slisenko.samples.loadbalancer;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements ILoadBalancer {

    private final List<URI> hosts;
    private final AtomicInteger nextHostIndex = new AtomicInteger();

    public RoundRobinLoadBalancer(List<URI> hosts) {
        // Reduce to the unique list
        Set<URI> unique = new HashSet<>(hosts);
        this.hosts = new ArrayList<>(unique);
    }

    @Override
    public URI getNextHost() {
        if (hosts.isEmpty()) {
            throw new LoadBalancerException("List of hosts is empty");
        }

        int index = nextHostIndex.updateAndGet(operand -> {
            // This code is thread-safe and lock-free
            return operand >= hosts.size() - 1 ? 0 : operand + 1;
        });

        return hosts.get(index);
    }
}
