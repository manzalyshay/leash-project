package com.shaym.leash.data.forecast.localdb.dbutils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@SuppressWarnings("serial")
@Entity (tableName = "Forecasts")
public class ForecastObject   {

    public static final String TAG = "ForecastObject";
    public String mLocation;
    @PrimaryKey
    @ColumnInfo(name = "timestamp")
    public long mLocalTimeStamp;
    public float mMinBreakingHeight;
    public float mMaxBreakingHeight;
    public int mWindSpeed;
    public int mWindDirection;
    public int mTempChill;
    public int mTemp;

    public ForecastObject(String mLocation, long mLocalTimeStamp, float mMinBreakingHeight,float mMaxBreakingHeight, int mWindSpeed, int mWindDirection, int mTempChill, int mTemp) {
        this.mLocation = mLocation;
        this.mLocalTimeStamp = mLocalTimeStamp;
        this.mMinBreakingHeight = mMinBreakingHeight;
        this.mMaxBreakingHeight = mMaxBreakingHeight;
        this.mWindSpeed = mWindSpeed;
        this.mWindDirection = mWindDirection;
        this.mTempChill = mTempChill;
        this.mTemp = mTemp;
    }

    public String getLocation() {
        return mLocation;
    }

    public long getLocalTimeStamp() {
        return mLocalTimeStamp;
    }

    public float getAbsMinBreakingHeight() {
        return mMinBreakingHeight;
    }

    public float getAbsMaxBreakingHeight() {
        return mMaxBreakingHeight;
    }

    public int getWindSpeed() {
        return mWindSpeed;
    }

    public int getWindDirection() {
        return mWindDirection;
    }

    public int getTempChill() {
        return mTempChill;
    }

    public int getTemp() {
        return mTemp;
    }

    @Override
    public String toString() {
        return "ForecastObject{" +
                ", mLocation='" + mLocation + '\'' +
                ", mLocalTimeStamp=" + mLocalTimeStamp +
                ", mMinBreakingHeight=" + mMinBreakingHeight +
                ", mMaxBreakingHeight=" + mMaxBreakingHeight +
                ", mWindSpeed=" + mWindSpeed +
                ", mWindDirection=" + mWindDirection +
                ", mTempChill=" + mTempChill +
                ", mTemp=" + mTemp +
                '}';
    }
}
