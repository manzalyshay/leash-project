package com.shaym.leash.models;

import com.shaym.leash.data.forecast.localdb.dbutils.ForecastObject;

public class ForecastAVGObject extends ForecastObject {
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


    public String getDay() {
        return day;
    }
}
