package slisenko.samples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TransactionalBuffer<T> {

    private ConcurrentLinkedQueue<List<T>> queue = new ConcurrentLinkedQueue<>();
    private ThreadLocal<ArrayList<T>> threadLocal = new ThreadLocal<>();

    public void begin() {
        if (threadLocal.get() != null) {
            throw new TransactionException("Transaction already started"); // TODO unit-test
        }

        threadLocal.set(new ArrayList<>());
    }

    public void put(T value) {
        if (threadLocal.get() == null) {
            throw new TransactionException("Transaction not started"); // TODO unit-test
        }

        threadLocal.get().add(value);
    }

    public void commit() {
        if (threadLocal.get() == null) {
            throw new TransactionException("Transaction not started"); // TODO unit-test
        }
        queue.add(threadLocal.get());
        threadLocal.remove();
    }

    // Allow to delete and read from the common queue
    public List<T> poll() {
        return queue.poll();
    }
}
