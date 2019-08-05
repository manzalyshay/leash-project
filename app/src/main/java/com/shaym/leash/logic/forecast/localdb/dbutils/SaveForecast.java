package com.shaym.leash.logic.forecast.localdb.dbutils;


import android.os.AsyncTask;
import android.util.Log;

import com.shaym.leash.MainApplication;
import com.shaym.leash.logic.forecast.ForecastListener;

import java.util.ArrayList;

public class SaveForecast extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "SaveForecast";
    private ArrayList<ForecastObject> forecasts;

    public SaveForecast(ArrayList<ForecastObject> data){
        forecasts = data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Let's add some dummy data to the database.
        ForecastDB.getInstance(MainApplication.getInstace().getApplicationContext()).daoAccess().deleteAllForecasts();

        for (int i=0; i<forecasts.size(); i++) {
                ForecastDB.getInstance(MainApplication.getInstace().getApplicationContext()).daoAccess().saveForecast(forecasts.get(i));
                Log.d(TAG, "newForecastsAdded: True");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
    }
}