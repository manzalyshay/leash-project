package com.shaym.leash.logic.forecast.localdb.dbhandlers;

import android.support.annotation.NonNull;

import com.shaym.leash.logic.forecast.ForecastObject;

import java.util.List;

public interface ForecastDataSource {

    interface GetForecastsCallback {

        void onForecastsLoaded(List<ForecastObject> forecasts);

        void onDataNotAvailable();
    }

    interface GetForecastCallback {

        void onForecastsLoaded(ForecastObject forecast);

        void onDataNotAvailable();
    }

    void getForecasts(@NonNull GetForecastsCallback callback);

    void getForecast(@NonNull long timestamp, @NonNull GetForecastCallback callback);

    void saveForecast(@NonNull ForecastObject forecast);

    void clearOldForecasts(@NonNull final List <ForecastObject> forecasts);

    void deleteAllForecasts();

    void deleteForecast(@NonNull final ForecastObject forecast);
}
