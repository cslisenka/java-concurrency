package slisenko.thread;

import static slisenko.util.MyLogger.log;

public class ExceptionHandlingMain {

    public static void main(String[] args) {
        Runnable workerWithException = () -> {
            log("Start");
            throw new RuntimeException("Exception in thread");
        };

        Thread threadWithHandler = new Thread(workerWithException);
        threadWithHandler.setName("WITH-HANDLER");
        threadWithHandler.setUncaughtExceptionHandler(
            (t, e) -> log("Uncaught exception handler in thread %s, exception $s", t.getName(), e.getClass().getName())
        );

        Thread threadWithoutHandler = new Thread(workerWithException);
        threadWithoutHandler.setName("NO-HANDLER");

        threadWithHandler.start();
        threadWithoutHandler.start();
    }
}
