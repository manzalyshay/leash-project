package com.shaym.leash.data.forecast;

import android.annotation.SuppressLint;

import com.shaym.leash.data.forecast.localdb.dbutils.ForecastObject;
import com.shaym.leash.models.ForecastAVGObject;

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

    public Date formatTimeStamp(long timestamp){
        return new java.util.Date(timestamp*1000L);
    }

    public String formatDay(Date date){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM");
        return sdf.format(date);
    }



    public ArrayList<ForecastObject> getForecastsByDay(String day, List<ForecastObject> mData){
        ArrayList<ForecastObject> forecastByDay = new ArrayList<>();
        for (int i=0; i<mData.size(); i++){
            String timestamp2date = formatDay(formatTimeStamp(mData.get(i).getLocalTimeStamp()));
            if (timestamp2date.equals(day)) {
                forecastByDay.add(mData.get(i));
            }
        }

        float mAVGAbsMinBreakingHeight = 0f;
        float mAVGAbsMaxBreakingHeight = 0f;
        int mAVGWindSpeed = 0;
        int mAVGWindDirection = 0;
        int mAVGTempChill = 0;
        int mAVGTemp = 0;

        if (forecastByDay.size() > 0 ) {

            for (int i = 0; i < forecastByDay.size(); i++) {
                mAVGAbsMaxBreakingHeight += forecastByDay.get(i).getAbsMaxBreakingHeight();
                mAVGAbsMinBreakingHeight += forecastByDay.get(i).getAbsMinBreakingHeight();
                mAVGWindSpeed += forecastByDay.get(i).getWindSpeed();
                mAVGWindDirection += forecastByDay.get(i).getWindDirection();
                mAVGTempChill += forecastByDay.get(i).getTempChill();
                mAVGTemp += forecastByDay.get(i).getTemp();
            }

            mAVGAbsMaxBreakingHeight = mAVGAbsMaxBreakingHeight / forecastByDay.size();
            mAVGAbsMinBreakingHeight = mAVGAbsMinBreakingHeight / forecastByDay.size();
            mAVGWindSpeed = mAVGWindSpeed / forecastByDay.size();
            mAVGWindDirection = mAVGWindDirection / forecastByDay.size();
            mAVGTempChill = mAVGTempChill / forecastByDay.size();
            mAVGTemp = mAVGTemp / forecastByDay.size();


            forecastByDay.add(new ForecastAVGObject(mAVGAbsMinBreakingHeight, mAVGAbsMaxBreakingHeight, mAVGWindSpeed, mAVGWindDirection, mAVGTempChill, mAVGTemp, day));
        }
        return forecastByDay;
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
