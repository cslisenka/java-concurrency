package slisenko.atomics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static slisenko.util.MyLogger.log;

public class AtomicsReferenceMain {

    static class Node<T> {
        final T value;
        final Node<T> next;

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    static class LockFreeStack<T> {
        private final AtomicReference<Node<T>> head = new AtomicReference<>();

        void push(T value) {
            Node<T> currentHead;
            Node<T> newHead;
            // This loop guarantees that if we push from multiple threads - nothing get lost
            do {
                currentHead = head.get();
                newHead = new Node<>(value, currentHead);
            } while (!head.compareAndSet(currentHead, newHead));

            // Not thread safe code - uncomment to see the difference
//            currentHead = head.get();
//            newHead = new Node<>(value, currentHead);
//            head.set(newHead);
        }

        T pop() {
            Node<T> currentHead;
            Node<T> nextHead;

            // Safely replace the pointer to head to the next item
            // If other thread did it - do int a loop until success
            do {
                currentHead = head.get();
                if (currentHead == null) {
                    return null;
                }
                nextHead = currentHead.next;
            } while (!head.compareAndSet(currentHead, nextHead));

            return currentHead.value;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // TODO multiple threads adding to the stack - nothing got lost

        LockFreeStack<Integer> stack = new LockFreeStack<>();
        int elementsPerThread = 10_000;

        Runnable stackPopulator = () -> {
            log("started");
            for (int i = 0; i < elementsPerThread; i++) {
                stack.push(i);
            }
            log("added %d items", elementsPerThread);
        };

        Runnable stackRemover = () -> {
            int removedCount = 0;

            Integer element;
            do {
                element = stack.pop();
                removedCount++;
            } while (element != null && removedCount < elementsPerThread);

            log("removed %d items", removedCount);
        };

        // Run many adders and run them in parallel
        List<Thread> adders = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Thread populator = new Thread(stackPopulator);
            populator.setName("POPULATOR-" + i);
            adders.add(populator);
            populator.start();
        }

        // Wait until the stack is populated fully
        for (Thread t : adders) {
            t.join();
        }

        // Start removing from the stack
        List<Thread> removers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Thread remover = new Thread(stackRemover);
            remover.setName("REMOVER-" + i);
            removers.add(remover);
            remover.start();
        }

        // Wait until the stack is removed fully
        for (Thread t : removers) {
            t.join();
        }
    }
}
