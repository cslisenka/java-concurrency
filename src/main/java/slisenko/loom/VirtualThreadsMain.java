package slisenko.loom;

import java.util.ArrayList;
import java.util.List;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

// Virtual thread (green thread) - managed by JVM, could be millions of them
// We can block inside virtual threads, while physical thread just take the next virtual thread
public class VirtualThreadsMain {

    public static void main(String[] args) throws InterruptedException {
        Runnable heavyTask = () -> {
            log("start");
            sleep(5_000);
            log("finish");
        };

        log("Threads %d", Thread.getAllStackTraces().size());

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            threads.add(
                    Thread.ofVirtual()
                        .name("VIRTUAL-" + i)
                        .start(heavyTask)
            );

            // Comparing if we run physical threads
//            Thread t = new Thread(heavyTask);
//            threads.add(t);
//            t.start();
        }

        log("Threads %d", Thread.getAllStackTraces().size());

        for (Thread t : threads) {
            t.join();
        }
    }
}
