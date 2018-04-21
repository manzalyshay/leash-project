package com.shaym.leash.logic.forecast;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@SuppressWarnings("serial")
@Entity (tableName = "Forecasts")
public class ForecastObject implements Serializable {

    public static final String TAG = "ForecastObject";
    public String mLocation;
    @PrimaryKey
    @ColumnInfo(name = "timestamp")
    public long mLocalTimeStamp;
    public float mAbsMinBreakingHeight;
    public float mAbsMaxBreakingHeight;
    public int mWindSpeed;
    public int mWindDirection;
    public int mTempChill;
    public int mTemp;

    public ForecastObject(String mLocation, long mLocalTimeStamp, float absMinBreakingHeight,float absMaxBreakingHeight, int mWindSpeed, int mWindDirection, int mTempChill, int mTemp) {
        this.mLocation = mLocation;
        this.mLocalTimeStamp = mLocalTimeStamp;
        this.mAbsMinBreakingHeight = absMinBreakingHeight;
        this.mAbsMaxBreakingHeight = absMaxBreakingHeight;
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
        return mAbsMinBreakingHeight;
    }

    public float getAbsMaxBreakingHeight() {
        return mAbsMaxBreakingHeight;
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
                ", mAbsMinBreakingHeight=" + mAbsMinBreakingHeight +
                ", mAbsMaxBreakingHeight=" + mAbsMaxBreakingHeight +
                ", mWindSpeed=" + mWindSpeed +
                ", mWindDirection=" + mWindDirection +
                ", mTempChill=" + mTempChill +
                ", mTemp=" + mTemp +
                '}';
    }
}
