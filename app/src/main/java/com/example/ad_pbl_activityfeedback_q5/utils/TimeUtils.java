package com.example.ad_pbl_activityfeedback_q5.utils;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@RequiresApi(api = Build.VERSION_CODES.O)
public class TimeUtils {

    private static final TimeZone IST_ZONE = TimeZone.getTimeZone("Asia/Kolkata");
    private static final ZoneId IST_ZONE_ID = ZoneId.of("Asia/Kolkata");

    // Format current time as IST string
    public static String getCurrentTimeIST() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        sdf.setTimeZone(IST_ZONE);
        return sdf.format(new Date());
    }

    // Format Date object to IST string
    public static String formatDateToIST(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        sdf.setTimeZone(IST_ZONE);
        return sdf.format(date);
    }

    // Format timestamp to IST string
    public static String formatTimestampToIST(long timestamp) {
        return formatDateToIST(new Date(timestamp));
    }

    // Convert UTC LocalDateTime to IST LocalDateTime
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDateTime convertToIST(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(IST_ZONE_ID)
                .toLocalDateTime();
    }

    // Get current LocalDateTime in IST
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDateTime getCurrentLocalDateTimeIST() {
        return LocalDateTime.now(IST_ZONE_ID);
    }

    // Format LocalDateTime to IST string
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String formatLocalDateTimeToIST(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        LocalDateTime istDateTime = convertToIST(dateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return istDateTime.format(formatter);
    }
}