package com.shaym.leash.logic.forecast;

import android.annotation.SuppressLint;

import com.shaym.leash.logic.forecast.localdb.dbutils.ForecastObject;

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
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        return sdf.format(date);
    }



        public ArrayList<ForecastObject> getForecastsByDay(String day, List<ForecastObject> mData){
        ArrayList<ForecastObject> list = new ArrayList<>();
        for (int i=0; i<mData.size(); i++){
            String timestamp2date = formatDay(formatTimeStamp(mData.get(i).getLocalTimeStamp()));
            if (timestamp2date.equals(day)) {
                list.add(mData.get(i));
            }
        }

            float mAVGAbsMinBreakingHeight = 0f;
            float mAVGAbsMaxBreakingHeight = 0f;
            int mAVGWindSpeed = 0;
            int mAVGWindDirection = 0;
            int mAVGTempChill = 0;
            int mAVGTemp = 0;

            for (int i=0; i<list.size(); i++) {
                mAVGAbsMaxBreakingHeight += list.get(i).getAbsMaxBreakingHeight();
                mAVGAbsMinBreakingHeight += list.get(i).getAbsMinBreakingHeight();
                mAVGWindSpeed += list.get(i).getWindSpeed();
                mAVGWindDirection += list.get(i).getWindDirection();
                mAVGTempChill += list.get(i).getTempChill();
                mAVGTemp += list.get(i).getTemp();
            }

            mAVGAbsMaxBreakingHeight = mAVGAbsMaxBreakingHeight/list.size();
            mAVGAbsMinBreakingHeight = mAVGAbsMinBreakingHeight/list.size();
            mAVGWindSpeed = mAVGWindSpeed/list.size();
            mAVGWindDirection = mAVGWindDirection/list.size();
            mAVGTempChill = mAVGTempChill/list.size();
            mAVGTemp = mAVGTemp/list.size();


            list.add(new ForecastAVGObject(mAVGAbsMinBreakingHeight, mAVGAbsMaxBreakingHeight, mAVGWindSpeed, mAVGWindDirection, mAVGTempChill, mAVGTemp, day));
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
