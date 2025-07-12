package slisenko.thread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

public class PoolShutdownNowMain {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newSingleThreadExecutor();

        Runnable task1 = () -> {
            log("Task 1 start");
            sleep(10_000);
            log("Task 1 finish");
        };

        Runnable task2 = () -> {
            log("Task 2 start");
            sleep(10_000);
            log("Task 2 finish");
        };

        pool.submit(task1);
        pool.submit(task2);

        List<Runnable> tasks = pool.shutdownNow();
        log("Shutdown now, tasks=%d", tasks.size()); // Returns not completed tasks and interrupts tasks in progress
    }
}
