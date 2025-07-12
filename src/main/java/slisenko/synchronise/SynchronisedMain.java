package slisenko.synchronise;

import java.util.ArrayList;
import java.util.List;

import static slisenko.util.MyLogger.log;

// TODO synchronised singleton
// TODO thread local + thread local with date/time and random
// TODO java memory model
// TODO how to correctly handle interrupted exception
public class SynchronisedMain {

    static class Data {
        private int nonSynchronisedCounter;
        private int synchronisedCounter;

        public void incrementNonSync() {
            nonSynchronisedCounter++;
        }

        public synchronized void incrementSync() {
            synchronisedCounter++;
        }

        public synchronized int getSync() {
            return synchronisedCounter;
        }

        public int getNonSync() {
            return nonSynchronisedCounter;
        }
    }

    public static void main(String[] args) {
        Data data = new Data();
        int iterations = 1_000_000;
        int threads = 10;

        Runnable nonSyncWorker = () -> {
            for (int i = 0; i < 1_000_000; i++) {
                data.incrementNonSync();
            }
        };

        Runnable syncWorker = () -> {
            for (int i = 0; i < 1_000_000; i++) {
                data.incrementSync();
            }
        };

        runAndWait(nonSyncWorker, threads);
        runAndWait(syncWorker, threads);

       log("Sync counter %d, non sync counter %d, iterations %d, threads %d\n", data.getSync(), data.getNonSync(), iterations, threads);

    }

    public static void runAndWait(Runnable runnable, int threadCount) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            threads.add(new Thread(runnable));
        }

        long startTimeMs = System.currentTimeMillis();

        threads.forEach(Thread::start);

        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        log("Completed %d threads in %d ms", threadCount, System.currentTimeMillis() - startTimeMs);

        /*
         * Completed 10 threads in 23 ms
         * Completed 10 threads in 413 ms
         * Sync counter 10000000, non sync counter 5251437, iterations 1000000, threads 10
         */
    }
}