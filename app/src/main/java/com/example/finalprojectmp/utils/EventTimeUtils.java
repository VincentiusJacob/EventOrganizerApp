package com.example.finalprojectmp.utils;

import androidx.annotation.Nullable;

import com.example.finalprojectmp.models.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class EventTimeUtils {

    private static final String DB_DATETIME_PATTERN = "yyyy-MM-dd HH:mm";
    private static final String DB_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DISPLAY_DATE_PATTERN = "EEE, MMM d";
    private static final String DISPLAY_TIME_PATTERN = "h:mm a";

    private EventTimeUtils() {
    }

    public static long getEventTimestamp(@Nullable Event event) {
        if (event == null) {
            return 0L;
        }
        return parseTimestamp(event.getDate(), event.getTime());
    }

    public static long parseTimestamp(@Nullable String date, @Nullable String time) {
        if (date == null || time == null) {
            return 0L;
        }
        try {
            SimpleDateFormat parser = new SimpleDateFormat(DB_DATETIME_PATTERN, Locale.getDefault());
            Date parsed = parser.parse(date + " " + time);
            return parsed != null ? parsed.getTime() : 0L;
        } catch (ParseException e) {
            return 0L;
        }
    }

    public static EventStatus resolveStatus(@Nullable Event event) {
        long eventTime = getEventTimestamp(event);
        if (eventTime == 0L) {
            return EventStatus.UPCOMING;
        }

        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);

        Calendar todayEnd = (Calendar) todayStart.clone();
        todayEnd.add(Calendar.DAY_OF_MONTH, 1);

        if (eventTime < todayStart.getTimeInMillis()) {
            return EventStatus.PAST;
        }
        if (eventTime < todayEnd.getTimeInMillis()) {
            return EventStatus.CURRENT;
        }
        return EventStatus.UPCOMING;
    }

    public static String formatDisplayDate(@Nullable Event event) {
        return formatDisplayDate(event != null ? event.getDate() : null);
    }

    public static String formatDisplayDate(@Nullable String rawDate) {
        if (rawDate == null) {
            return "";
        }
        try {
            SimpleDateFormat parser = new SimpleDateFormat(DB_DATE_PATTERN, Locale.getDefault());
            Date parsed = parser.parse(rawDate);
            if (parsed == null) {
                return rawDate;
            }
            return new SimpleDateFormat(DISPLAY_DATE_PATTERN, Locale.getDefault()).format(parsed);
        } catch (ParseException e) {
            return rawDate;
        }
    }

    public static String formatDisplayTime(@Nullable String rawTime) {
        if (rawTime == null) {
            return "";
        }
        try {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date parsed = parser.parse(rawTime);
            if (parsed == null) {
                return rawTime;
            }
            return new SimpleDateFormat(DISPLAY_TIME_PATTERN, Locale.getDefault()).format(parsed);
        } catch (ParseException e) {
            return rawTime;
        }
    }

    public static String formatFullDateTime(Event event) {
        String date = formatDisplayDate(event);
        String time = formatDisplayTime(event != null ? event.getTime() : null);
        if (date.isEmpty()) {
            return time;
        }
        if (time.isEmpty()) {
            return date;
        }
        return date + " • " + time;
    }
}
