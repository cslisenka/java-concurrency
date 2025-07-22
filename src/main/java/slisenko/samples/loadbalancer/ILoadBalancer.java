package slisenko.samples.loadbalancer;

import java.net.URI;

public interface ILoadBalancer {

    URI getNextHost();
}
