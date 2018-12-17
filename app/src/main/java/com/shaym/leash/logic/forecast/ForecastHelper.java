package com.shaym.leash.logic.forecast;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ForecastHelper {
    private static ForecastHelper instance;

    private ForecastHelper(){
        instance = this;
    }

    public String formatHour(long timestamp){
        Date date = new java.util.Date(timestamp*1000L);
// the format of your date
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
// give a timezone reference for formatting (see comment at the bottom)
        return sdf.format(date);

    }

    private Date formatTimeStamp(long timestamp){
        return new java.util.Date(timestamp*1000L);
    }

    public String formatDay(Date date){
// the format of your date
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
// give a timezone reference for formatting (see comment at the bottom)
        return sdf.format(date);
    }









        public ArrayList<ForecastObject> getForecastsByDay(String day, List<ForecastObject> mData){
        ArrayList<ForecastObject> list = new ArrayList<>();
        for (int i=0; i<mData.size(); i++){
            String timestamp2date = formatDay(formatTimeStamp(mData.get(i).getLocalTimeStamp()));
            if (timestamp2date.equals(day))
                list.add(mData.get(i));
        }
        return list;
    }

    public static ForecastHelper getInstance(){
        if (instance == null){
            instance = new ForecastHelper();
            return  instance;
        }
        else {
            return instance;
        }

    }
}
