package slisenko.collections.set;

import slisenko.util.MyLogger;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static slisenko.util.MyLogger.log;

// Set based on the hash map
public class SetFromMapMain {

    public static void main(String[] args) {
        Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

        for (int i = 0; i < 100; i++) {
            set.add("Element-" + i);
        }

        for (String val : set) {
            log(val);
            set.add("NewVal-" + val);
            // Sometimes new elements appear so we can iterate infinitely
        }

        log(">>>> Print after");
        set.forEach(MyLogger::log);

    }
}
