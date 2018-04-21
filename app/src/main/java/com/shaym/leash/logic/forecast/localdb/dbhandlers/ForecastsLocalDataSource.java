package com.shaym.leash.logic.forecast.localdb.dbhandlers;

import android.support.annotation.NonNull;

import com.shaym.leash.logic.forecast.ForecastObject;
import com.shaym.leash.logic.forecast.utils.AppExecutors;

import java.util.List;

import static com.bumptech.glide.util.Preconditions.checkNotNull;

public class ForecastsLocalDataSource implements ForecastDataSource {
    final String NO_FORECAST_T0_DELETE= "No forecasts to delete";

    private static volatile ForecastsLocalDataSource INSTANCE;
    private DaoAccess mForecastsDao;
    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private ForecastsLocalDataSource(@NonNull AppExecutors appExecutors, @NonNull DaoAccess forecastsdao) {
        mAppExecutors = appExecutors;
        mForecastsDao = forecastsdao;
    }

    public static ForecastsLocalDataSource getInstance(@NonNull AppExecutors appExecutors, @NonNull DaoAccess forecastsdao) {
        if (INSTANCE == null) {
            synchronized (ForecastsLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ForecastsLocalDataSource(appExecutors, forecastsdao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getForecasts(@NonNull final GetForecastsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<ForecastObject> forecasts = mForecastsDao.getForecasts();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (forecasts.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onForecastsLoaded(forecasts);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getForecast(@NonNull final long timestamp, @NonNull final GetForecastCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final ForecastObject forecast = mForecastsDao.getForecast(timestamp);
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (forecast == null) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onForecastsLoaded(forecast);
                        }
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveForecast(@NonNull final ForecastObject forecast) {
        checkNotNull(forecast);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mForecastsDao.saveForecast(forecast);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
}

    @Override
    public void clearOldForecasts(@NonNull final List <ForecastObject> forecasts) {
        if (!forecasts.isEmpty()){
            Runnable saveRunnable = new Runnable() {
                @Override
                public void run() {
                    for (int i=0; i<forecasts.size(); i++){
                        deleteForecast(forecasts.get(i));
                    }
                }
            };
            mAppExecutors.diskIO().execute(saveRunnable);
        }


    }


    @Override
    public void deleteAllForecasts() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mForecastsDao.deleteAllForecasts();
            }
        };
        mAppExecutors.diskIO().execute(deleteRunnable);
    }



    @Override
    public void deleteForecast(@NonNull final ForecastObject forecast) {
        checkNotNull(forecast);
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mForecastsDao.deleteForecast(forecast);
            }
        };
        mAppExecutors.diskIO().execute(deleteRunnable);
    }
}
