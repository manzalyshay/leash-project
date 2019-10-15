package com.shaym.leash.data.forecast;

import com.shaym.leash.data.forecast.localdb.dbutils.ForecastObject;

import java.util.List;

public interface ForecastListener {

    void onForecastsLoaded(List<List<ForecastObject>> forecastByDays, List<String> days);

}
