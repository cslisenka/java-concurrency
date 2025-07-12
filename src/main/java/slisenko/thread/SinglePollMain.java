package slisenko.thread;

import java.util.concurrent.*;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

// TODO future with timeout
// TODO jvm shutdown hooks Runtime.getRuntime().addShutdownHook
// TODO completable future
// TODO what's new in new versions of java
public class SinglePollMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadFactory threadFactory = r -> {
            Thread thread = new Thread(r);
            thread.setName("ThreadNamePrefix");
            return thread;
        };
        ExecutorService pool = Executors.newSingleThreadExecutor(threadFactory);

        Runnable heavyRunnable = () -> {
            log("Start heavy runnable");
            sleep(5_000);
            log("Finish heavy runnable");
        };

        Callable<String> heavyCallable = () -> {
            log("Start heavy callable");
            sleep(5_000);
            log("Finish heavy callable");
            return "Result";
        };

        Future<String> futureCallable = pool.submit(heavyCallable);
        Future<?> futureRunnable = pool.submit(heavyRunnable);

        // Stop accepting new tasks, but complete existing
        // This call is not blocking
        // Threads will keep processing existing tasks in the queue
        pool.shutdown();

        // Try submitting new tasks after pool shutdown
        try {
            pool.submit(() -> log("New runnable"));
        } catch (Exception e) {
            log("Error submitting new task %s %s", e.getClass().getName(), e.getMessage());
        }

        String callableResult = futureCallable.get();
        log("Callable result $s", callableResult);

        log("Finish, waiting for callable to complete %b", futureRunnable.isDone());

        // Application not finished because there are working non-daemon threads
    }
}
