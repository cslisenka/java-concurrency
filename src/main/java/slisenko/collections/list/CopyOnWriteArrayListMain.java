package slisenko.collections.list;

import slisenko.util.MyLogger;

import java.util.concurrent.CopyOnWriteArrayList;

import static slisenko.util.MyLogger.log;

// Read - no blocking, directly from the underlying array, iterator works with snapshot of data
// Write - copy array, modify and set as main array
public class CopyOnWriteArrayListMain {

    public static void main(String[] args) {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add("Value-" + i);
        }

        // Reading and inserting at the same time
        for (String value : list) {
            log(value);
            list.add("NewValue-" + value);
        }

        // Reading list after
        log("Reading next time");
        list.forEach(MyLogger::log);
    }
}
