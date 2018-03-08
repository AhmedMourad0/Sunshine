package com.example.ahmed.sunshine;

import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

class DateUtils {
    private DateUtils() {  }

    private static Date getDateFromJulian7(String julianDate)
            throws ParseException
    {
        return new SimpleDateFormat("yyyyD", Locale.getDefault()).parse(julianDate);
    }

    private static String getJulian7FromDate(Date date) {
        StringBuilder sb = new StringBuilder();
        Calendar cal  = Calendar.getInstance();
        cal.setTime(date);

        return sb.append(cal.get(Calendar.YEAR))
                .append(String.format(Locale.getDefault(), "%03d", cal.get(Calendar.DAY_OF_YEAR)))
                .toString();
    }

    public static void main(String[] args) throws Exception {
        String test = "1998221";
        Date d = DateUtils.getDateFromJulian7(test);
        System.out.println(d);
        System.out.println(DateUtils.getJulian7FromDate(d));

    /*
     * output :
     *    Sun Aug 09 00:00:00 EDT 1998
     *    1998221
     */
    }
}
