package slisenko.samples.ratelimiter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;
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

        // Create new queue, or update existing queue atomically
        ConcurrentLinkedQueue<LocalDateTime> userRequests = stats.compute(userId, new BiFunction<String, ConcurrentLinkedQueue<LocalDateTime>, ConcurrentLinkedQueue<LocalDateTime>>() {
            @Override
            public ConcurrentLinkedQueue<LocalDateTime> apply(String s, ConcurrentLinkedQueue<LocalDateTime> localDateTimes) {
                if (localDateTimes == null) {
                    ConcurrentLinkedQueue<LocalDateTime> times = new ConcurrentLinkedQueue<>();
                    times.add(currentTime);
                    return times;
                } else {
                    localDateTimes.removeIf(localDateTime -> currentTime.isAfter(localDateTime.plus(duration)));
                    localDateTimes.add(currentTime);
                    return localDateTimes;
                }
            }
        });

        return userRequests.size() <= maxRequests;
    }

    // Use only for unit-tests to mock the time
    protected void setDateTimeSupplier(Supplier<LocalDateTime> timeSupplier) {
        this.timeSupplier = timeSupplier;
    }
}
