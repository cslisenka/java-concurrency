package slisenko.volatil;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

// volatile - threads are reading the variable directly from the heap, not from the thread cache
// all changes are written to the heap memory
// visibility is guaranteed. atomicity is not guaranteed
// If we do not use volatile, one thread may change the variable, other thread may work with the local cache
public class VolatileMain {

    static boolean running = true;
    static volatile boolean volatileRunning = true;

    public static void main(String[] args) {
        Thread nonVolatileThread = new Thread(() -> {
            log("started");
            while (running) {
            }
            log("finished");
        });

        Thread volatileThread = new Thread(() -> {
            log("started");
            while (volatileRunning) {
            }
            log("finished");
        });

        nonVolatileThread.start();
        volatileThread.start();

        sleep(5_000);
        // Changing variables from main thread
        running = false;
        volatileRunning = false;

        // Thread 2 is not finished, because still reading variable from the local cache
    }
}
