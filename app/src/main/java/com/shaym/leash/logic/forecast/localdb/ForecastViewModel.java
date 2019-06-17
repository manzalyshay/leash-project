package com.shaym.leash.logic.forecast.localdb;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.shaym.leash.logic.forecast.ForecastObject;
import com.shaym.leash.logic.forecast.localdb.dbhandlers.ForecastDB;

import java.util.List;

public class ForecastViewModel extends AndroidViewModel {


    private LiveData<List<ForecastObject>> mAllForecasts;

    public ForecastViewModel(@NonNull Application application) {
        super(application);

        mAllForecasts = ForecastDB.getInstance(application).daoAccess().getForecasts();

    }

    public LiveData<List<ForecastObject>> getAllForecasts() {
        return mAllForecasts;
    }
}
