package com.mednote.cwru.util.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeStampHelper {

    public static String getTimeZone(TimeZone timeZone) {
        return getGenericTimestamp("ZZZZ", Calendar.getInstance().getTimeInMillis(), timeZone);
    }

    public static String getTimestamp(long eventDate) {
        return getTimestamp(eventDate, TimeZone.getTimeZone("UTC"));
    }

    public static String getTimestamp(long eventDate, TimeZone timeZone) {
        return getGenericTimestamp("yyyy-MM-dd HH:mm:ss ZZZZ", eventDate, timeZone);
    }

    public static String getTimestampUI(long eventDate, TimeZone timeZone) {
        return getGenericTimestamp("yyyy-MM-dd HH:mm:ss", eventDate, timeZone);
    }

    public static String getFileTimestamp(Calendar calendar) {
        return getGenericTimestamp("yyyy-MM-dd", calendar.getTimeInMillis(), calendar.getTimeZone());
    }

    public static String getFileTimestampExtended(long eventDate, TimeZone timeZone) {
        return getGenericTimestamp("yyyy-MM-dd--HH-mm-ss", eventDate, timeZone);
    }

    public static String getGenericTimestamp(String format, long eventDate, TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CANADA);
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(new Date(eventDate));
    }

    public static long daysBetween(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));
    }

    public static long daysBetween(long start , long end) {
        return TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));
    }
}
