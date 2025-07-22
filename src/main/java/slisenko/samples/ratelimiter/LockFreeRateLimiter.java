package slisenko.samples.ratelimiter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;


public class LockFreeRateLimiter implements IRateLimiter {

    private final int maxRequests;
    private final Duration duration;

    static class TimeWindow {
        private final List<LocalDateTime> requests = new ArrayList<>();

        public long getRequestCount(Duration duration, LocalDateTime to) {
            return requests.stream().filter(new Predicate<LocalDateTime>() {
                @Override
                public boolean test(LocalDateTime requestTime) {
                    return requestTime.plus(duration).isAfter(to);
                }
            }).count();
        }

        /**
         * Create new immutable object
         * @param duration
         * @param to
         */
        public TimeWindow filterAndCopy(Duration duration, LocalDateTime to) {
            TimeWindow newWindow = new TimeWindow();
            for (LocalDateTime requestTime : this.requests) {
                if (requestTime.plus(duration).isAfter(to)) {
                    newWindow.requests.add(requestTime);
                }
            }
            newWindow.requests.add(to);
            return newWindow;
        }
    }

    // TODO think of expiration -> could be a scheduled future which cleans up the user state in case it wasn't used during last N minutes
    private final ConcurrentHashMap<String, AtomicReference<TimeWindow>> stats = new ConcurrentHashMap<>();

    public LockFreeRateLimiter(int maxRequests, Duration duration) {
        this.maxRequests = maxRequests;
        this.duration = duration;
    }

    /**
     * Allows request if there were no more than "maxRequests" already done during the "duration" sliding window
     * @param userId
     */
    @Override
    public boolean allowRequest(String userId) {
        stats.putIfAbsent(userId, new AtomicReference<>(new TimeWindow()));
        LocalDateTime currentTime = getCurrentTime();

        boolean retry = true;
        AtomicReference<TimeWindow> ref = stats.get(userId);

        while (retry) {
            // Get current window
            TimeWindow window = ref.get();

            // Check if we can make a new request
            if (window.getRequestCount(duration, currentTime) >= maxRequests) {
                return false;
            }

            // Creating new window, move here only relevant requests + new request
            TimeWindow newWindow = window.filterAndCopy(duration, currentTime);
            retry = !ref.compareAndSet(window, newWindow);
        }
        return true;
    }

    // A separate method to mock in uint-test and override the behavior
    protected LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
}
