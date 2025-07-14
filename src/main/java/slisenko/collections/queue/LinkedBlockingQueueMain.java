package slisenko.collections.queue;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

// Based on the linked list, can be bounded
public class LinkedBlockingQueueMain {

    public static void main(String[] args) {
        // TODO producer-consumer

        LinkedBlockingQueue<String> messages = new LinkedBlockingQueue<>(10);

        Runnable producer = () -> {
            int i = 0;
            while (true) {
                try {
                    String message = String.format("message %d", i++);
                    messages.put(message); // Blocks until there is a free space
                    log("Sent %s", message);
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable consumer = () -> {
            while (true) {
                try {
                    String message = messages.take();
                    log("Received %s", message);
                    sleep(1000); // Simulating delay on the producer side
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };


        Thread p = new Thread(producer, "PRODUCER");
        Thread c1 = new Thread(consumer, "CONSUMER-1");
        Thread c2 = new Thread(consumer, "CONSUMER-2");
        Thread c3 = new Thread(consumer, "CONSUMER-3");
        Thread c4 = new Thread(consumer, "CONSUMER-4");


        List.of(p, c1, c2, c3, c4).forEach(Thread::start);
    }
}
