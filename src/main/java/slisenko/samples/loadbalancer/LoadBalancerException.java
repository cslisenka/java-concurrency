package slisenko.samples.loadbalancer;

public class LoadBalancerException extends RuntimeException {

    public LoadBalancerException(String message) {
        super(message);
    }

    public LoadBalancerException(String message, Throwable cause) {
        super(message, cause);
    }
}
