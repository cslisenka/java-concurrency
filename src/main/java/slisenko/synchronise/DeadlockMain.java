package slisenko.synchronise;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

public class DeadlockMain {

    // TODO can we do locks on Integers, Strings or Booleans? Possible no as objects may be polled - check it
    public static void main(String[] args) throws InterruptedException {
        Object lock1 = new Object();
        Object lock2 = new Object();

        Runnable worker1 = new Runnable() {
            @Override
            public void run() {
                synchronized (lock1) {
                    log("got lock 1");
                    sleep(1000);
                    synchronized (lock2) {
                        log("got lock 2");
                    }
                }
            }
        };

        Runnable worker2 = new Runnable() {
            @Override
            public void run() {
                synchronized (lock2) {
                    log("got lock 2");
                    sleep(1000);
                    synchronized (lock1) {
                        log("got lock 1");
                    }
                }
            }
        };

        log("Start");

        Thread t1 = new Thread(worker1);
        Thread t2 = new Thread(worker2);

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        log("Never see finish because of deadlock");
    }
}
