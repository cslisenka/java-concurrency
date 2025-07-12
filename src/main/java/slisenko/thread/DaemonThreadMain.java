package slisenko.thread;

import static slisenko.util.MyLogger.log;
import static slisenko.util.ThreadUtil.sleep;

public class DaemonThreadMain {

    public static void main(String[] args) {
        Thread daemon = new Thread(() -> {
            log("Start");
            sleep(10_000);
            log("Finish");
        });
        daemon.setDaemon(true);
        daemon.setName("DAEMON");

        Thread regular = new Thread(() -> {
            log("Start");
            sleep(3_000);
            log("Finish");
        });
        regular.setName("REGULAR");

        daemon.start();
        regular.start();

        log("Main method finish");
        // Application waits until regular thread finish and not waiting for the daemon thread
    }
}
