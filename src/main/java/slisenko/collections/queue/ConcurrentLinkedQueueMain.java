package slisenko.collections.queue;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

// Lock free queue (non-blocking)
// Using linked list
public class ConcurrentLinkedQueueMain {

    public static void main(String[] args) {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        Runnable logger = () -> {
            while (true) {
                // Read buffer of events from the queue
                String message = queue.poll();
                if (message != null) {
                    log(message);
                } else {
                    sleep(100);
                }
            }
        };

        Runnable producer = () -> {
            int i = 0;
            while (true) {
                queue.add(Thread.currentThread().getName() + " message in log " + i++);
                sleep(50);
            }
        };

        Thread loggerThread = new Thread(logger, "LOGGER");
        Thread p1 = new Thread(producer, "PRODUCER-1");
        Thread p2 = new Thread(producer, "PRODUCER-2");
        Thread p3 = new Thread(producer, "PRODUCER-3");

        List.of(loggerThread, p1, p2, p3).forEach(Thread::start);
    }
}
