package slisenko.collections.map;

import java.util.concurrent.ConcurrentHashMap;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

public class ConcurrentHashMapLockExampleMain {

    public static void main(String[] args) {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("key", "initial");

        Runnable computer = () -> {
            map.compute("key", (k, v) -> {
                log("started compute");
                sleep(10_000); // Simulating delay to lock on the bucket
                log("finished compute");
                return "changed";
            });
        };

        Thread t1 = new Thread(computer);
        Thread t2 = new Thread(computer);

        t1.start();
        t2.start(); // t2 is locked and can not compute until first thread finish the operation
    }
}
