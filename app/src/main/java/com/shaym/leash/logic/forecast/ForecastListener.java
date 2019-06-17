package com.shaym.leash.logic.forecast;

import java.util.List;

public interface ForecastListener {

    void onForecastsLoaded(List<List<ForecastObject>> forecastByDays, List<String> days);

    void onForecastsUpdated();
}
