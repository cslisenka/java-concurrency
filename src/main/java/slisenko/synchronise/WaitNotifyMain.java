package slisenko.synchronise;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

public class WaitNotifyMain {

    public static String message = null;

    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        int count = 100;

        Runnable waitingFirstWorker = () -> {
            for (int i = 0; i < count; i++) {

                synchronized (lock) { // Acquire lock
                    // Wait
                    try {
                        lock.wait(10_000); // Release lock, thread -> WAITING
                        if (message != null) {
                            log("Received message [%s]", message);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Send
                    message = String.format("Message from %s", Thread.currentThread().getName());
                    lock.notifyAll(); // Wake up WAITING threads, hold lock until the end of synchronised
                }
            }
        };

        Thread t1 = new Thread(waitingFirstWorker);
        Thread t2 = new Thread(waitingFirstWorker);

        t1.start();
        t2.start();

        sleep(1000); // Wait until both threads are moved to WAITING

        synchronized (lock) {
            log("Kick off ping-pong");
            lock.notify(); // Wake up 1 thread to send a message
        }

        t1.join();
        t2.join();
    }
}
