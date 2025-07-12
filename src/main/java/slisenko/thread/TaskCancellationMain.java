package slisenko.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

public class TaskCancellationMain {

    // TODO example for handling the thread interrupt
    public static void main(String[] args) {
        ExecutorService pool = Executors.newSingleThreadExecutor();

        Runnable task1 = () -> {
            log("Start task 1");
            sleep(15_000);
            log("Finish 1");
        };

        Runnable task2 = () -> {
            log("Start task 2");
            sleep(15_000);
            log("Finish 2");
        };

        Future<?> future1 = pool.submit(task1);
        Future<?> future2 = pool.submit(task2);

        log("Task 1 status %s", future1.state());
        log("Task 2 status %s", future2.state());

        // This task is running and will be completed unless we interrupt
        boolean cancellation1 = future1.cancel(true); // This task is only interrupted, so sleep method throws interrupted exception
        boolean cancellation2 = future2.cancel(true); // This task wasn't started, so cancelled
        log("Cancelling both tasks (%b, %b)", cancellation1, cancellation2);

        log("Task 1 status %s, cancelled=%b", future1.state(), future1.isCancelled());
        log("Task 2 status %s, cancelled=%b", future2.state(), future2.isCancelled());

        pool.shutdown();
    }
}
