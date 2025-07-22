package slisenko.samples.ratelimiter;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public interface IRateLimiter {

    boolean allowRequest(String userId);
}
