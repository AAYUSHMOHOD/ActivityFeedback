package com.example.ad_pbl_activityfeedback_q5.utils;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DateTimeConverter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String toString(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        // Convert to IST before returning string
        LocalDateTime istDateTime = TimeUtils.convertToIST(dateTime);
        return istDateTime.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDateTime toLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) return null;
        // Note: When parsing from string, we assume it's already in IST
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formatForDisplay(LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown date";
        return TimeUtils.formatLocalDateTimeToIST(dateTime);
    }

    // Add a method to get current IST timestamp
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDateTime now() {
        return TimeUtils.getCurrentLocalDateTimeIST();
    }
}