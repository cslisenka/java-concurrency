package slisenko.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyLogger {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    public static void log(String format, Object... args) {
        String message = String.format(format, args);
        String dateTime = LocalDateTime.now().format(formatter);
        String threadName = Thread.currentThread().isVirtual() ? "(V) " + Thread.currentThread().getName() : Thread.currentThread().getName();
        System.out.format("%-15s %4d | %-10s | %s\n", threadName, Thread.currentThread().threadId(), dateTime, message);
    }
}
