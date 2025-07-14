package slisenko.samples;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

// TODO execute 3 requests in parallel, in case one of them fails - cancel others if possible, return aggregated result
public class ReactiveAggregator {

    private ExecutorService executor = Executors.newFixedThreadPool(8);

    public static class Result<A, B, C> {
        public final A a;
        final B b;
        final C c;

        public Result(A a, B b, C c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    private static class CallableWrapper<T> implements Callable<T> {

        private final Supplier<T> supplier;

        private CallableWrapper(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T call() throws Exception {
            return supplier.get();
        }
    }

    public <A, B, C> Result<A, B, C> execute(Supplier<A> sa, Supplier<B> sb, Supplier<C> sc) throws Exception {
//        Future<A> futureA = executor.submit(new CallableWrapper<>(sa));
//        Future<B> futureB = executor.submit(new CallableWrapper<>(sa));
//        Future<C> futureC = executor.submit(new CallableWrapper<>(sa));
//
//        A resultA = null;
//        B resultB = null;
//        C resultC = null;
//
//        // TODO use completable future - so we can react on the first error and cancell others, this implementation is not efficient, because we can only execute in order
//
//        try {
//            resultA = futureA.get();
//        } catch (Exception e) {
//            futureB.cancel(true);
//            futureC.cancel(true);
//            throw e;
//        }
//
//        try {
//            resultB = futureB.get();
//        } catch (Exception e) {
//            futureC.cancel(true);
//            throw e;
//        }
//
//        try {
//            resultC = futureC.get();
//        } catch (Exception e) {
//            futureB.cancel(true);
//            futureC.cancel(true);
//            throw e;
//        }
        return null;
    }
}
