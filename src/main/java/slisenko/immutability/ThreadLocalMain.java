package slisenko.immutability;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

public class ThreadLocalMain {

    public static void main(String[] args) throws InterruptedException {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        threadLocal.set("MAIN");

        Runnable task1 = () -> {
            log("set thread local");
            threadLocal.set("TASK1");

            sleep(1000);
            log("thread local %s", threadLocal.get());
        };

        Runnable task2 = () -> {
            log("set thread local");
            threadLocal.set("TASK2");

            sleep(500);
            log("thread local %s", threadLocal.get());
        };

        Thread t1 = new Thread(task1, "TASK-1");
        Thread t2 = new Thread(task2, "TASK-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();
        log("thread local %s", threadLocal.get());
    }
}
