package slisenko.samples.ratelimiter;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RateLimiterTest {

    // Rate limiter with 2 requests per second
    RateLimiter rl = new RateLimiter(2, Duration.ofMinutes(1));

    @Test
    public void testSingleUser() {
        // Given rate limiter and single user
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
    public void testMultipleUsers() {
        // Given rate limiter and two users
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
    public void testTimeWindowExpiration() {
        // Given
        String userID = "1";

        // Requests made now
        System.out.println("request 1");
        boolean r1 = rl.allowRequest(userID);

        // Request made 2 minutes ago
        System.out.println("request 2");
        rl.setDateTimeSupplier(() -> LocalDateTime.now().minusMinutes(2));
        boolean r2 = rl.allowRequest(userID);
        rl.setDateTimeSupplier(LocalDateTime::now);

        System.out.println("request 3");
        // Request 3, should be true because second request was 2 minutes ago
        boolean r3 = rl.allowRequest(userID);

        // Then
        assertTrue(r1);
        assertTrue(r2);
        assertTrue(r3);
    }
}
