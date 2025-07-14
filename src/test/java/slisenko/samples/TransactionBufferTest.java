package slisenko.samples;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionBufferTest {

    TransactionalBuffer<String> buffer = new TransactionalBuffer<>();

    @Test
    void testSingleTransaction() {
        buffer.begin();
        buffer.put("A");
        buffer.put("B");
        buffer.put("C");

        assertNull(buffer.poll());

        buffer.commit();

        assertEquals(List.of("A", "B", "C"), buffer.poll());
        assertNull(buffer.poll());
    }

    @Test
    void testTwoTransactions_SingleThread() {
        buffer.begin();

        assertThrows(TransactionException.class, () -> {
            buffer.begin();
        });
    }

    @Test
    void testMultipleThreads() throws InterruptedException {
        Runnable runner = () -> {
            for (int i = 0; i < 1_000_000; i++) {
                buffer.begin();
                buffer.put("A");
                buffer.put("B");
                buffer.put("C");
                buffer.commit();
            }
        };

        Thread t1 = new Thread(runner);
        Thread t2 = new Thread(runner);
        Thread t3 = new Thread(runner);

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        // Check recorded data
        int count = 0;

        List<String> records = null;
        while ((records = buffer.poll()) != null) {
            count++;
            assertEquals(List.of("A", "B", "C"), records);
        }

        assertEquals(3_000_000, count);

    }
}
