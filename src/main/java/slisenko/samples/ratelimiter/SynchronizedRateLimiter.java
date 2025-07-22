package slisenko.samples.ratelimiter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SynchronizedRateLimiter implements IRateLimiter {

    static class TimeWindow {
        private final List<LocalDateTime> requests = new ArrayList<>();

        public List<LocalDateTime> getRequests() {
            return requests;
        }
    }

    private final Duration duration;
    private final int maxRequests;
    private final ConcurrentHashMap<String, TimeWindow> windows = new ConcurrentHashMap<>();

    public SynchronizedRateLimiter(int maxRequests, Duration duration) {
        this.duration = duration;
        this.maxRequests = maxRequests;
    }

    @Override
    public boolean allowRequest(String userId) {
        LocalDateTime currentTime = getCurrentTime();
        windows.putIfAbsent(userId, new TimeWindow());
        TimeWindow window = windows.get(userId);

        synchronized (window) {
            // Filter requests
            window.getRequests().removeIf(requestTime -> requestTime.plus(duration).isBefore(currentTime));

            // If we are allowed to do request
            if (window.getRequests().size() < maxRequests) {
                // Add new request
                window.getRequests().add(currentTime);
                return true;
            }
        }

        return false;
    }

    // A separate method to mock in uint-test and override the behavior
    protected LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
}
