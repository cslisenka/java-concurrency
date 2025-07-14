package slisenko.collections.map;

// TODO review other implementations of concurrent collections
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

// Not fully lock free!
// Reading - lock free
//         - hash(key) -> find bucket -> search by list or tree
// Writing - lock bucket, 16 buckets by default, extended by default when inserting more entries
//         - hash(key) -> find bucket -> lock bucket -> search by list or tree -> if not found, add new node


public class ConcurrentHashMapComputeIfAbsentMain {

    public static void main(String[] args) {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

        Function<String, String> valueLoaderFunction = new Function<String, String>() {
            @Override
            public String apply(String s) {
                log("Computing value for key %s", s);
                sleep(10_000);
                return s + "-value";
            }
        };

        Runnable client = () -> {
            log("waiting for value");
            String value = map.computeIfAbsent("key", valueLoaderFunction);
            log("value = %s", value);
        };

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(client));
        }

        // First thread is computing, other threads are waiting
        threads.forEach(Thread::start);
    }
}
