package com.shaym.leash.data.forecast.localdb.dbutils;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.shaym.leash.data.forecast.utils.ForecastDaoAccess;

@Database(entities = {ForecastObject.class}, version = 1, exportSchema = false)
public abstract class ForecastDB extends RoomDatabase {

    public abstract ForecastDaoAccess daoAccess();
    private static ForecastDB INSTANCE;

    private static final Object sLock = new Object();

    public static ForecastDB getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        ForecastDB.class, "Forecasts.db")
                        .build();
            }
            return INSTANCE;
        }
    }
    }

