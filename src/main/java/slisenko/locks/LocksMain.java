package slisenko.locks;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

public class LocksMain {

    static class BlockingBuffer {

        private LinkedList<String> queue = new LinkedList<>();
        private final int maxSize;

        private final ReentrantLock lock = new ReentrantLock();
        private final Condition canWrite = lock.newCondition();
        private final Condition canRead = lock.newCondition();

        public BlockingBuffer(int maxSize) {
            this.maxSize = maxSize;
        }

        // Blocks if buffer is full
        public void put(String val) {
            lock.lock();

            try {
                while (queue.size() == maxSize) {
                    canWrite.await(); // Block writer thread
                }
                queue.push(val);
                log("%s", queue);
                canRead.signalAll(); // Unblock reader threads if blocked
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        // Returns false if buffer is full instead of blocking
        public boolean putNoWait(String val) {
            boolean result = lock.tryLock();
            if (!result) {
                return false;
            }

            try {
                if (queue.size() == maxSize) {
                    return false; // Block writer thread
                }
                queue.push(val);
                log("%s", queue);
                canRead.signalAll(); // Unblock reader threads if blocked
            } finally {
                lock.unlock();
            }

            return true;
        }

        // Blocks if buffer is empty
        public String take() {
            lock.lock();

            try {
                while (queue.isEmpty()) {
                    canRead.await();
                }

                String val = queue.removeLast();
                canWrite.signalAll();
                return val;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }

        // Returns null and doesn't block if buffer is empty
        public String takeNoWait() {
            boolean result = lock.tryLock();
            if (!result) {
                return null;
            }

            try {
                if (queue.isEmpty()) {
                    return null;
                }

                String val = queue.removeLast();
                canWrite.signalAll();
                return val;
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        BlockingBuffer buffer = new BlockingBuffer(10);

        Runnable blockingProducer = () -> {
            int i = 0;
            while(true) {
                String val = String.format(Thread.currentThread().getName() + " value " + i++);
                log("sending...");
                buffer.put(val);
                log("sent");

                sleep(1000);
            }
        };

        Runnable nonBlockingProducer = () -> {
            int i = 0;
            while(true) {
                String val = String.format(Thread.currentThread().getName() + " value " + i++);
                log("sending...");
                if (!buffer.putNoWait(val)) {
                    log("can not send, sleeping");
                    sleep(200);
                } else {
                    log("sent");
                }

                sleep(1000);
            }
        };

        Runnable blockingConsumer = () -> {
            int i = 0;
            while(true) {
                log("reading...");
                String val = buffer.take();
                log("read %s", val);

                sleep(1000);
            }
        };

        Runnable nonBlockingConsumer = () -> {
            int i = 0;
            while(true) {
                log("reading...");
                String val = buffer.takeNoWait();
                if (val != null) {
                    log("read %s", val);
                } else {
                    log("can not read, sleeping");
                    sleep(200);
                }

//                sleep(1000);
            }
        };

        Thread bp = new Thread(blockingProducer, "BL-PRODUCER");
        Thread nbp = new Thread(nonBlockingProducer, "N-BL-PRODUCER");
        Thread bc = new Thread(blockingConsumer, "BL-CONSUMER");
        Thread nbc = new Thread(nonBlockingConsumer, "N-BL-CONSUMER");



//        bp.start();
        nbp.start();

//        bc.start();
        nbc.start();
    }
}
