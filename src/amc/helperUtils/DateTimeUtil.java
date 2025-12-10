package amc.helperUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** This class provides simple ISO date and time helpers. */
public final class DateTimeUtil {
    private DateTimeUtil(){}

    /** Returns current date and time in ISO format, like 2025-08-21T10:00. */
    public static String nowIso() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return now.format(fmt);
    }
}