package slisenko.thread;

import java.time.Duration;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

public class ThreadInterruptionMain {

    public static void main(String[] args) {
        // Create thread
        Thread t = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                Thread current = Thread.currentThread();
                log("do work isInterrupted=%b state=%s alive=%b", current.isInterrupted(), current.getState(), current.isAlive());
                try {
                    // If thread was interrupted - we get InterruptedException immediately
                    Thread.sleep(Duration.ofMillis(1000));
                } catch (InterruptedException e) {
                    log("Exception %s %s", e.getClass().getName(), e.getMessage());
                    // Catching the exception cleans interrupted flag back to true
                    current.interrupt();
                    log("isInterrupted=%b state=%s alive=%b", current.isInterrupted(), current.getState(), current.isAlive());

                    log("Trying sleep more");
                    try {
                        Thread.sleep(Duration.ofMillis(50_000));
                    } catch (InterruptedException ex) {
                        log("We got interrupted exception immediately");
                        // Now isInterrupted flag is back to false
                        log("isInterrupted=%b state=%s alive=%b", current.isInterrupted(), current.getState(), current.isAlive());
                    }
                    log("Finish sleep more");
                }
            }
        });

        t.start();
        sleep(2_000);

        t.interrupt();

        log("Thread %s isInterrupted=%b", t.getName(), t.isInterrupted());
    }
}
