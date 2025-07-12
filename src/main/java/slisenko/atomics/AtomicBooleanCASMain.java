package slisenko.atomics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static slisenko.util.MyLogger.log;

public class AtomicBooleanCASMain {

    public static void main(String[] args) {
        AtomicBoolean initialised = new AtomicBoolean(false);

        Runnable initWorker = () -> {
            if (!initialised.getAndSet(true)) {
                log("initialise only in single thread");
            }
            log("do work");
        };

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            threads.add(new Thread(initWorker));
        }

        threads.forEach(Thread::start);
    }
}
