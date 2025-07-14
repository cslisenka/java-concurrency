package slisenko.samples;

import java.time.Duration;
import java.util.concurrent.*;

// TODO retry callable 3 times with duration, do not block any thread
public class RetriableExecutor {

    private ExecutorService executor = Executors.newFixedThreadPool(8);
    private ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(8);

    private class CallableRetryWrapper<T> implements Callable<T> {

        private final int retryCount;
        private final Callable<T> callable;

        private CallableRetryWrapper(int retryCount, Callable<T> callable) {
            this.retryCount = retryCount;
            this.callable = callable;
        }

        @Override
        public T call() throws Exception {
            try {
                return callable.call();
            } catch (Exception e) {

            }

            ScheduledFuture<T> future = retryExecutor.schedule();
            return future.get();
        }
    }

    public <T> T execute(Callable<T> task, int maxRetries, Duration delay) {



        return null;
    }
}
