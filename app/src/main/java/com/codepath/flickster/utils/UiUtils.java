package com.codepath.flickster.utils;

import android.content.Context;
import android.content.res.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Generic utility UI functions used accross the app.
 *
 * @author yvastavaus.
 */
public class UiUtils {
    /**
     * Returns the orientation of the device at this point.
     *
     * @param context context for getting resources
     *
     * @return true if portrait otherwise false landscape.
     */
    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static String getFormattedDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String newFormat = dateString;
        try {
            Date date = format.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            newFormat = new SimpleDateFormat("MMM d, yyyy").format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newFormat;
    }
}
