package com.shaym.leash.logic.cameras;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.shaym.leash.logic.forecast.utils.CamerasDaoAccess;
import com.shaym.leash.logic.forecast.utils.ForecastDaoAccess;

@Database(entities = {CameraObject.class}, version = 1, exportSchema = false)
public abstract class CamerasDB extends RoomDatabase {

    public abstract CamerasDaoAccess daoAccess();
    private static CamerasDB INSTANCE;

    private static final Object sLock = new Object();

    public static CamerasDB getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        CamerasDB.class, "Cameras.db")
                        .build();
            }
            return INSTANCE;
        }
    }
    }

