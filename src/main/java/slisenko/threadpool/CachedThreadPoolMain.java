package slisenko.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

public class CachedThreadPoolMain {

    public static void main(String[] args) {
        // Automatically creates new thread per task
        ExecutorService pool = Executors.newCachedThreadPool();

        for (int i = 0; i < 100; i++) {
            pool.submit(() -> {
                log("Task start");
                sleep(5_000);
                log("Task finish");
            });
        }

        pool.shutdown();
    }
}
