package slisenko.atomics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static slisenko.util.MyLogger.log;

public class AtomicIntegerCounterMain {

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();

        Runnable worker = () -> {
            for (int i = 0; i < 1_000_000; i++) {
                counter.incrementAndGet();
            }
        };

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(worker);
            t.start();
            threads.add(t);
        }

        for (Thread t : threads) {
            t.join();
        }

        log("counter=%d", counter.get());
    }
}
