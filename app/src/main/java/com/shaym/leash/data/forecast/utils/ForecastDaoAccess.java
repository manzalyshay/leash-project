package com.shaym.leash.data.forecast.utils;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.shaym.leash.data.forecast.localdb.dbutils.ForecastObject;

import java.util.List;

@Dao
public interface ForecastDaoAccess {

    @Insert
    void saveForecasts(List<ForecastObject> forecastObject);

    @Insert
    void saveForecast(ForecastObject forecastObject);

    @Query("SELECT * FROM Forecasts")
    LiveData<List<ForecastObject>> getForecasts();

    @Query("SELECT * FROM Forecasts WHERE timestamp =:time_stamp")
    ForecastObject getForecast(long time_stamp);

    @Update
    void updateForecastRecord(ForecastObject forecastObject);

    @Delete
    void deleteForecast(ForecastObject forecastObject);

    @Query("DELETE FROM Forecasts")
    void deleteAllForecasts();

}