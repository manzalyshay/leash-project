package com.shaym.leash.logic.forecast;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

public class ForecastAVGObject extends ForecastObject  {
    public float mAVGAbsMinBreakingHeight;
    public float mAVGAbsMaxBreakingHeight;
    public int mAVGWindSpeed;
    public int mAVGWindDirection;
    public int mAVGTempChill;
    public int mAVGTemp;
    private String day;

    @Override
    public String toString() {
        return "ForecastAVGObject{" +
                "mAVGAbsMinBreakingHeight=" + mAVGAbsMinBreakingHeight +
                ", mAVGAbsMaxBreakingHeight=" + mAVGAbsMaxBreakingHeight +
                ", mAVGWindSpeed=" + mAVGWindSpeed +
                ", mAVGWindDirection=" + mAVGWindDirection +
                ", mAVGTempChill=" + mAVGTempChill +
                ", mAVGTemp=" + mAVGTemp +
                ", day='" + day + '\'' +
                '}';
    }

    public ForecastAVGObject(float absMinBreakingHeight, float absMaxBreakingHeight, int mWindSpeed, int mWindDirection, int mTempChill, int mTemp, String day) {
        super("AVG-OBJECT", 0, absMinBreakingHeight, absMaxBreakingHeight, mWindSpeed, mWindDirection, mTempChill, mTemp);
        this.day = day;
        mAVGAbsMinBreakingHeight = absMinBreakingHeight;
        mAVGAbsMaxBreakingHeight = absMaxBreakingHeight;
        mAVGWindSpeed = mWindSpeed;
        mAVGWindDirection = mWindDirection;
        mAVGTempChill = mTempChill;
        mAVGTemp = mTemp;
    }

    public float getmAVGAbsMinBreakingHeight() {
        return mAVGAbsMinBreakingHeight;
    }

    public float getmAVGAbsMaxBreakingHeight() {
        return mAVGAbsMaxBreakingHeight;
    }

    public int getmAVGWindSpeed() {
        return mAVGWindSpeed;
    }

    public int getmAVGWindDirection() {
        return mAVGWindDirection;
    }

    public int getmAVGTempChill() {
        return mAVGTempChill;
    }

    public int getmAVGTemp() {
        return mAVGTemp;
    }


    public String getDay() {
        return day;
    }
}
