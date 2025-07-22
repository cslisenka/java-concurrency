package slisenko.samples.ratelimiter;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class SynchronizedRateLimiterTest {

    @Test
    public void testSingleThread_SingleUser() {
        // Given rate limiter and single user
        IRateLimiter rl = new SynchronizedRateLimiter(2, Duration.ofMinutes(1));
        String userId = "1";

        // When 3 requests
        boolean r1 = rl.allowRequest(userId);
        boolean r2 = rl.allowRequest(userId);
        boolean r3 = rl.allowRequest(userId);

        // Then
        assertTrue(r1);
        assertTrue(r2);
        assertFalse(r3);
    }

    @Test
    public void testSingleThread_MultipleUsers() {
        // Given rate limiter and two users
        IRateLimiter rl = new SynchronizedRateLimiter(2, Duration.ofMinutes(1));
        String user1 = "1";
        String user2 = "2";

        // When 3 requests for the first user, and then second user
        boolean r11 = rl.allowRequest(user1);
        boolean r12 = rl.allowRequest(user1);
        boolean r13 = rl.allowRequest(user1);

        boolean r21 = rl.allowRequest(user2);
        boolean r22 = rl.allowRequest(user2);
        boolean r23 = rl.allowRequest(user2);

        assertTrue(r11);
        assertTrue(r12);
        assertFalse(r13);

        assertTrue(r21);
        assertTrue(r22);
        assertFalse(r23);
    }

    @Test
    void testMultipleThreads_SingleUser() throws InterruptedException {
        // Run this experiment many times
        for (int i = 0; i < 10; i++) {
            // Given rate limiter and single user
            IRateLimiter rl = new SynchronizedRateLimiter(1, Duration.ofMinutes(1));
            String user1 = "1";

            AtomicInteger allowedRequests = new AtomicInteger();
            Runnable worker = () -> allowedRequests.getAndUpdate(operand -> rl.allowRequest(user1) ? operand + 1 : operand);

            List<Thread> threads = new ArrayList<>();
            for (int th = 0; th < 10; th++) {
                threads.add(new Thread(worker));
            }

            threads.forEach(Thread::start);
            for (Thread t : threads) {
                t.join();
            }

            // Expect that only 1 request was allowed, while there were a lot of threads
            assertEquals(1, allowedRequests.get());
        }
    }

    @Test
    public void testTimeWindowExpiration() {
        // Given rate limiter and single user
        SynchronizedRateLimiter rl = spy(new SynchronizedRateLimiter(2, Duration.ofMinutes(1)));
        String userID = "1";

        // Make 2 requests within the 1 minute time interval
        LocalDateTime start = LocalDateTime.of(2025, 07, 22, 0, 0, 0);

        // Two requests in the beginning of time interval
        doReturn(start).when(rl).getCurrentTime();
        assertTrue(rl.allowRequest(userID));

        doReturn(start.plusSeconds(10)).when(rl).getCurrentTime();
        assertTrue(rl.allowRequest(userID));

        // 1 requests at the end of the time interval
        doReturn(start.plusSeconds(59)).when(rl).getCurrentTime();
        assertFalse(rl.allowRequest(userID));

        // One more request after first is expired
        doReturn(start.plusSeconds(65)).when(rl).getCurrentTime();
        assertTrue(rl.allowRequest(userID));
    }
}
