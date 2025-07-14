package slisenko.collections.map;

import java.util.concurrent.ConcurrentHashMap;

import static slisenko.util.MyLogger.log;

public class ConcurrentHashMapInsertAndIterateMain {

    public static void main(String[] args) {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

        for (int i = 0; i < 100; i++) {
            map.put("key-" + i, "value-" + i);
        }

        // Iterate over the map and sometimes put values - no ConcurrentModificationException
        for (String key : map.keySet()) {
            log(key);
            map.put("key-" + key, "value-" + key);
        }
    }
}
