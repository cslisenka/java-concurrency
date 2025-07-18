package slisenko.samples.ratelimiter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;


public class RateLimiter {

    private final int maxRequests;
    private final Duration duration;
    private Supplier<LocalDateTime> timeSupplier = LocalDateTime::now;

    // TODO think of expiration -> could be a scheduled future which cleans up the user state in case it wasn't used during last N minutes
    // userId -> queue of timestamps
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<LocalDateTime>> stats = new ConcurrentHashMap<>();

    public RateLimiter(int maxRequests, Duration duration) {
        this.maxRequests = maxRequests;
        this.duration = duration;
    }

    /**
     * Allows request if there were no more than "maxRequests" already done during the "duration" sliding window
     * @param userId
     */
    public boolean allowRequest(String userId) {
        // Store statistics per user (could be map<userId -> statistics>)
        // The statistics must automatically expire to save memory

        LocalDateTime currentTime = timeSupplier.get();
        System.out.println("currentTime " + currentTime);

        // Create or get new queue of timestamps per user
        ConcurrentLinkedQueue<LocalDateTime> userRequests = stats.computeIfAbsent(userId, s -> new ConcurrentLinkedQueue<>());
        System.out.println("queue " + userRequests);
        // Remove all timestamps which do not fit into the window
        userRequests.removeIf(localDateTime -> {
            boolean isAfter = currentTime.isAfter(localDateTime.plus(duration));;
            System.out.println(currentTime + " is after? " + localDateTime.plus(duration) + " = " + isAfter);
            return isAfter;
        });

        userRequests.add(currentTime);

        System.out.println("queue after" + userRequests);

        return userRequests.size() <= maxRequests;
    }

    protected void setDateTimeSupplier(Supplier<LocalDateTime> timeSupplier) {
        this.timeSupplier = timeSupplier;
    }
}
