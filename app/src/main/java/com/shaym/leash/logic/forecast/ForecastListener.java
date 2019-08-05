package com.shaym.leash.logic.forecast;

import com.shaym.leash.logic.forecast.localdb.dbutils.ForecastObject;

import java.util.List;

public interface ForecastListener {

    void onForecastsLoaded(List<List<ForecastObject>> forecastByDays, List<String> days);

}
