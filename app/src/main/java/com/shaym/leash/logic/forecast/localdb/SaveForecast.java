package com.shaym.leash.logic.forecast.localdb;


import android.os.AsyncTask;
import android.util.Log;

import com.shaym.leash.MainApplication;
import com.shaym.leash.logic.forecast.ForecastListener;
import com.shaym.leash.logic.forecast.ForecastObject;
import com.shaym.leash.logic.forecast.localdb.dbhandlers.ForecastDB;

import java.util.ArrayList;

public class SaveForecast extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "SaveForecast";
    private ArrayList<ForecastObject> forecasts;
    private ForecastListener mListener;

    public SaveForecast(ArrayList<ForecastObject> data, ForecastListener listener){
        forecasts = data;
        mListener = listener;
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
            mListener.onForecastsUpdated();
    }
}