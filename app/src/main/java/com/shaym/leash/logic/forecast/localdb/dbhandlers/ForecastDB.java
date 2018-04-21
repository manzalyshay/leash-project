package com.shaym.leash.logic.forecast.localdb.dbhandlers;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.shaym.leash.logic.forecast.ForecastObject;

@Database(entities = {ForecastObject.class}, version = 1)
public abstract class ForecastDB extends RoomDatabase {

    public abstract DaoAccess daoAccess();
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

