package slisenko.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

public class ParallelStreamMain {

    public static void main(String[] args) throws InterruptedException {
        List<Integer> numbers = IntStream.range(1, 1000).boxed().toList();

        // Using all common pool + main as there are no empty threads
        numbers
            .stream()
            .parallel()
            .forEach(n -> {
                log("n=%d", n);
                sleep(1000);
            });
    }
}
