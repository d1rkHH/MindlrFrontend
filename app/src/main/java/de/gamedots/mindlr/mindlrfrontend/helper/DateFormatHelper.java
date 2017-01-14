package de.gamedots.mindlr.mindlrfrontend.helper;

import android.content.Context;
import android.content.res.Resources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.gamedots.mindlr.mindlrfrontend.R;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created by Dirk on 12.01.17.
 */

public class DateFormatHelper {

    /* Liked Today, Liked 30 seconds ago, Liked Monday, Liked 5 Jan */
    public static final int LIKE_FORMAT = 0;
    /* Written on 5 Jan, Written 30 seconds ago */
    public static final int WRITE_FORMAT = 1;

    public static String getFormattedDateString(Context context, long dateMillis, int format) {

        long daysFromEpochToProvidedDate = MILLISECONDS.toDays(dateMillis);
        long daysFromEpochToToday = MILLISECONDS.toDays(System.currentTimeMillis());

        long sdate = MILLISECONDS.toSeconds(dateMillis);
        long sNow = MILLISECONDS.toSeconds(System.currentTimeMillis());
        long secDifference = sNow - sdate;
        boolean underMinute = secDifference < 60;

        long mdate = MILLISECONDS.toMinutes(dateMillis);
        long mNow = MILLISECONDS.toMinutes(System.currentTimeMillis());
        long minDifference = mNow - mdate;
        boolean underHour = minDifference < 60;

        Resources res = context.getResources();
        String formatStart;
        switch (format) {
            case LIKE_FORMAT:
                formatStart = res.getString(R.string.liked);
                break;
            case WRITE_FORMAT:
                formatStart = res.getString(R.string.written);
                break;
            default:
                throw new IllegalArgumentException("Unkown format");
        }

        String out;
        if ((daysFromEpochToToday - daysFromEpochToProvidedDate) < 2) {
            if (daysFromEpochToProvidedDate == daysFromEpochToToday) {
                if (underMinute) {
                    String secFormatted = res.getQuantityString(R.plurals.seconds, (int) secDifference);
                    // Liked 30 second(s) ago
                    out = res.getString(R.string.time_ago, formatStart, secFormatted);
                } else if (underHour) {
                    String minFormatted = res.getQuantityString(R.plurals.minutes, (int) minDifference);
                    // Liked 1 minute ago
                    out = res.getString(R.string.time_ago, formatStart, minFormatted);
                } else {
                    // Liked Today/ Written Today
                    String today = res.getString(R.string.today);
                    out = res.getString(R.string.time_ago, formatStart, today);
                }
            } else {
                // Liked Yesterday
                String yesterday = res.getString(R.string.yesterday);
                out = res.getString(R.string.time_ago, formatStart, yesterday);
            }

        } else if (daysFromEpochToProvidedDate < daysFromEpochToToday + 7) {
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE",
                    res.getConfiguration().locale);
            // Liked Monday
            String day = dayFormat.format(dateMillis);
            out = res.getString(R.string.time_weekday, formatStart, day);
        } else {
            SimpleDateFormat month_date = new SimpleDateFormat("dd MMM",
                    res.getConfiguration().locale);
            String dayWithMonth = month_date.format(dateMillis);
            // Liked on 5 Dez
            out = res.getString(R.string.time_day_with_month, formatStart, dayWithMonth);
        }

        return out;
    }

    public static String getFullDateString(Context context, long dateMillis){
        SimpleDateFormat full_date_format = new SimpleDateFormat("dd MMM YYYY",
                context.getResources().getConfiguration().locale);
        return full_date_format.format(dateMillis);
    }

    public static long getLongMillisFromDateString(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (date != null) ?
                date.getTime()
                : System.currentTimeMillis();
    }
}
