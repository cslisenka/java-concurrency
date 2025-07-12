package slisenko.synchronise;

public class DeadlockMain {

    // TODO can we do locks on Integers, Strings or Booleans? Possible no as objects may be polled - check it
    public static void main(String[] args) throws InterruptedException {
        Object lock1 = new Object();
        Object lock2 = new Object();

        Runnable worker1 = new Runnable() {
            @Override
            public void run() {
                synchronized (lock1) {
                    System.out.format("%s got lock 1 \n", Thread.currentThread().getName());
                    sleep(1000);
                    synchronized (lock2) {
                        System.out.format("%s git lock 2 \n", Thread.currentThread().getName());
                    }
                }
            }
        };

        Runnable worker2 = new Runnable() {
            @Override
            public void run() {
                synchronized (lock2) {
                    System.out.format("%s got lock 2 \n", Thread.currentThread().getName());
                    sleep(1000);
                    synchronized (lock1) {
                        System.out.format("%s git lock 1 \n", Thread.currentThread().getName());
                    }
                }
            }
        };

        System.out.println("Start");

        Thread t1 = new Thread(worker1);
        Thread t2 = new Thread(worker2);

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Never see finish because of deadlock");
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
