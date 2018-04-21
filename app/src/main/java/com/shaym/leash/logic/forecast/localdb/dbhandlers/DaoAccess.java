package com.shaym.leash.logic.forecast.localdb.dbhandlers;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.shaym.leash.logic.forecast.ForecastObject;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    void saveForecasts(List<ForecastObject> forecastObject);

    @Insert
    void saveForecast(ForecastObject forecastObject);

    @Query("SELECT * FROM Forecasts")
    List<ForecastObject> getForecasts();

    @Query("SELECT * FROM Forecasts WHERE timestamp =:time_stamp")
    ForecastObject getForecast(long time_stamp);

    @Update
    void updateRecord(ForecastObject forecastObject);

    @Delete
    void deleteForecast(ForecastObject forecastObject);

    @Query("DELETE FROM Forecasts")
    void deleteAllForecasts();
}