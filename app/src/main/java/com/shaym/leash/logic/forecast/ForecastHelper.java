package com.shaym.leash.logic;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LeashHelper {

    public static String formatHour(long timestamp){
        Date date = new java.util.Date(timestamp*1000L);
// the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
// give a timezone reference for formatting (see comment at the bottom)
        String formattedDate = sdf.format(date);
        return formattedDate;

    }

    public static String formatDay(long timestamp){
        Date date = new java.util.Date(timestamp*1000L);
// the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM");
// give a timezone reference for formatting (see comment at the bottom)
        String formattedDate = sdf.format(date);
        return formattedDate;

    }
}
