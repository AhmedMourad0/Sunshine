package com.example.ahmed.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utility {

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString("location", "Arab Republic of Egypt, EG");
    }

    static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("units", "metric").equals("metric");
    }

    public static String formatTemperature(Context context, double temperature) {

        // Data stored in Celsius by default.  If user prefers to see in Fahrenheit, convert
        // the values here.
        if (!isMetric(context))
            temperature = (temperature * 1.8) + 32;

//        Locale locale;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            locale = context.getResources().getConfiguration().getLocales().get(0);
//        } else {
//            locale = context.getResources().getConfiguration().locale;
//        }

        //%.0f is the format string for a float, with 0 decimal places.
        //return String.format(locale, "%.0f", temp);
        return context.getString(R.string.format_temperature, temperature);
    }

    static String getFormattedWind(Context context, float windSpeed, float degrees) {

        int windFormat;

        if (Utility.isMetric(context)) {
            windFormat = R.string.format_wind_kmh;
        } else {
            windFormat = R.string.format_wind_mph;
            windSpeed = 0.621371192237334f * windSpeed;
        }

        // From wind direction in degrees, determine compass direction as a string (e.g NW)
        // You know what's fun, writing really long if/else statements with tons of possible
        // conditions.  Seriously, try it!
        String direction = "Unknown";

        if (degrees >= 337.5 || degrees < 22.5)
            direction = "N";
        else if (degrees >= 22.5 && degrees < 67.5)
            direction = "NE";
        else if (degrees >= 67.5 && degrees < 112.5)
            direction = "E";
        else if (degrees >= 112.5 && degrees < 157.5)
            direction = "SE";
        else if (degrees >= 157.5 && degrees < 202.5)
            direction = "S";
        else if (degrees >= 202.5 && degrees < 247.5)
            direction = "SW";
        else if (degrees >= 247.5 && degrees < 292.5)
            direction = "W";
        else if (degrees >= 292.5 && degrees < 337.5)
            direction = "NW";

        return String.format(context.getString(windFormat), windSpeed, direction);
    }

// --Commented out by Inspection START (2018-03-08 4:37 AM):
//    static String formatDate(long dateInMilliseconds) {
//        Date date = new Date(dateInMilliseconds);
//        return DateFormat.getDateInstance().format(date);
//    }
// --Commented out by Inspection STOP (2018-03-08 4:37 AM)

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    //public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * @param context      Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return a user-friendly representation of the date.
     */
    static String getFriendlyDayString(Context context, long dateInMillis, Boolean isTwoPane) {
        // The day string for forecast uses the following logic:
        // For today: "Today, June 8"
        // For tomorrow:  "Tomorrow"
        // For the next 5 days: "Wednesday" (just the day name)
        // For all days after that: "Mon Jun 8"

        Time time = new Time();
        time.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), time.gmtoff);

        // If the date we're building the String for is today's date, the format
        // is "Today, June 24"
        if (julianDay == currentJulianDay) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;

            if (!isTwoPane)
                return context.getString(formatId, today, getFormattedMonthDay(dateInMillis));
            else
                return today;

        } else if (julianDay < currentJulianDay + 7) {
            // If the input date is less than a week in the future, just return the day name.
            return getDayName(context, dateInMillis);
        } else {
            // Otherwise, use the form "Mon, Jun 3"
            return (new SimpleDateFormat("EEE, MMM dd", Locale.getDefault())).format(dateInMillis);
        }
    }

// --Commented out by Inspection START (2018-03-08 4:37 AM):
//    public static int getJulianDay(int year, int month, int day) {
//        int a = (14 - month) / 12;
//        int y = year + 4800 - a;
//        int m = month + 12 * a - 3;
//        return day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
//    }
// --Commented out by Inspection STOP (2018-03-08 4:37 AM)

// --Commented out by Inspection START (2018-03-08 4:37 AM):
//    /**
//     * Return date in specified format.
//     *
//     * @param milliSeconds Date in milliseconds
//     * @param dateFormat   Date format
//     * @return String representing date in specified format
//     */
//    public static String getDate(long milliSeconds, String dateFormat) {
//        // Create a DateFormatter object for displaying date in specified format.
//        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
//
//        // Create a calendar object that will convert the date and time value in milliseconds to date.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(milliSeconds);
//
//        return formatter.format(calendar.getTime());
//    }
// --Commented out by Inspection STOP (2018-03-08 4:37 AM)

// --Commented out by Inspection START (2018-03-08 4:37 AM):
//    public static int getDayFromMillis(long milliSeconds) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(milliSeconds);
//        return calendar.get(Calendar.DAY_OF_MONTH);
//    }
// --Commented out by Inspection STOP (2018-03-08 4:37 AM)

// --Commented out by Inspection START (2018-03-08 4:37 AM):
//    public static int getMonthFromMillis(long milliSeconds) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(milliSeconds);
//        return calendar.get(Calendar.MONTH);
//    }
// --Commented out by Inspection STOP (2018-03-08 4:37 AM)

// --Commented out by Inspection START (2018-03-08 4:37 AM):
//    public static int getYearFromMillis(long milliSeconds) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(milliSeconds);
//        return calendar.get(Calendar.YEAR);
//    }
// --Commented out by Inspection STOP (2018-03-08 4:37 AM)

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "wednesday".
     *
     * @param context      Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return the day name
     */
    static String getDayName(Context context, long dateInMillis) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if (julianDay == currentJulianDay + 1) {
            return context.getString(R.string.tomorrow);
        } else {
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return dayFormat.format(dateInMillis);
        }
    }

    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     * <p>
     * //@param context      Context to use for resource localization
     *
     * @param dateInMillis The db formatted date string, expected to be of the form specified
     *                     in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    static String getFormattedMonthDay(long dateInMillis) {
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        return monthDayFormat.format(dateInMillis);
    }

    /**
     * Helper method to provide the icon resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     *
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getIconResourceForWeatherCondition(int weatherId) {
        //TODO: http://openweathermap.org/weather-conditions
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232)
            return R.drawable.ic_storm;
        else if (weatherId >= 300 && weatherId <= 321)
            return R.drawable.ic_light_rain;
        else if (weatherId >= 500 && weatherId <= 504)
            return R.drawable.ic_rain;
        else if (weatherId == 511)
            return R.drawable.ic_snow;
        else if (weatherId >= 520 && weatherId <= 531)
            return R.drawable.ic_rain;
        else if (weatherId >= 600 && weatherId <= 622)
            return R.drawable.ic_snow;
        else if (weatherId >= 701 && weatherId <= 761)
            return R.drawable.ic_fog;
        else if (weatherId == 761 || weatherId == 781)
            return R.drawable.ic_storm;
        else if (weatherId == 800)
            return R.drawable.ic_clear;
        else if (weatherId == 801)
            return R.drawable.ic_light_clouds;
        else if (weatherId >= 802 && weatherId <= 804)
            return R.drawable.ic_cloudy;

        return -1;
    }

    /**
     * Helper method to provide the art resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     *
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getArtResourceForWeatherCondition(int weatherId) {
        //TODO: http://openweathermap.org/weather-conditions
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232)
            return R.drawable.art_storm;
        else if (weatherId >= 300 && weatherId <= 321)
            return R.drawable.art_light_rain;
        else if (weatherId >= 500 && weatherId <= 504)
            return R.drawable.art_rain;
        else if (weatherId == 511)
            return R.drawable.art_snow;
        else if (weatherId >= 520 && weatherId <= 531)
            return R.drawable.art_rain;
        else if (weatherId >= 600 && weatherId <= 622)
            return R.drawable.art_snow;
        else if (weatherId >= 701 && weatherId <= 761)
            return R.drawable.art_fog;
        else if (weatherId == 761 || weatherId == 781)
            return R.drawable.art_storm;
        else if (weatherId == 800)
            return R.drawable.art_clear;
        else if (weatherId == 801)
            return R.drawable.art_light_clouds;
        else if (weatherId >= 802 && weatherId <= 804)
            return R.drawable.art_clouds;

        return -1;
    }

//    static void prepareUtilities(Context context) {
//        oldLocation = getPreferredLocation(context);
//        oldIsMetric = isMetric(context);
//    }
//
//    static void checkForLocationAndUnitChanges(Context context, FragmentManager fragmentManager) {
//
//        String location = getPreferredLocation(context);
//        Boolean isMetric = isMetric(context);
//        // update the location in our second pane using the fragment manager
//
//        if ((location != null && !location.equals(oldLocation)) || (isMetric != oldIsMetric)) {
//
//            ForecastFragment mainActivityFragment = (ForecastFragment) fragmentManager.findFragmentById(R.id.fragment_forecast);
//            DetailsFragment detailsFragment = (DetailsFragment) fragmentManager.findFragmentByTag(DETAILS_FRAGMENT_TAG);
//
//            if (mainActivityFragment != null && detailsFragment != null) {
//
//                if (location != null && !location.equals(oldLocation)) {
//
//                    mainActivityFragment.onLocationChanged();
//
//                    detailsFragment.onLocationChanged(location);
//
//                    oldLocation = location;
//                }
//
//                if (isMetric != oldIsMetric) {
//
//                    mainActivityFragment.onUnitChanged();
//
//                    detailsFragment.onUnitChanged();
//
//                    oldIsMetric = isMetric;
//                }
//            }
//        }
//    }
}
