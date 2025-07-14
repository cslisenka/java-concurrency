package slisenko.samples;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LockFreeStatsCollectorTest {

    LockFreeStatsCollector collector = new LockFreeStatsCollector();

    @Test
    void testAddValue() {
        collector.add(10.0);
        collector.add(20.0);
        collector.add(30.0);

        assertEquals(3, collector.count());
        assertEquals(60.0, collector.sum());
        assertEquals(10.0, collector.min());
        assertEquals(30.0, collector.max());
        assertEquals(20.0, collector.avg());
    }

    static class Adder implements Runnable {

        private final double number;
        private final double count;
        private final LockFreeStatsCollector collector;

        Adder(double number, double count, LockFreeStatsCollector collector) {
            this.number = number;
            this.count = count;
            this.collector = collector;
        }

        @Override
        public void run() {
            for (int i = 0; i < count; i++) {
                collector.add(number);
            }
        }
    }

    @Test
    void testConcurrency() throws InterruptedException {
        Thread adder10 = new Thread(new Adder(10.0, 1_000_000, collector));
        Thread adder20 = new Thread(new Adder(20.0, 1_000_000, collector));
        Thread adder30 = new Thread(new Adder(30.0, 1_000_000, collector));

        adder10.start();
        adder20.start();
        adder30.start();

        adder10.join();
        adder20.join();
        adder30.join();

        assertEquals(3_000_000, collector.count());
        assertEquals(1_000_000 * (10.0 + 20.0 + 30.0), collector.sum());
        assertEquals(10.0, collector.min());
        assertEquals(30.0, collector.max());
        assertEquals(20.0, collector.avg());
    }
}
