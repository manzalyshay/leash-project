package com.shaym.leash.logic.forecast.localdb;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.shaym.leash.MainApplication;
import com.shaym.leash.logic.forecast.ForecastObject;
import com.shaym.leash.logic.forecast.localdb.dbhandlers.ForecastDB;
import com.shaym.leash.ui.forecast.ForecastActivity;

import java.util.List;

public class GetForecasts extends AsyncTask<Void, Void, List<ForecastObject>> {
    private static final String TAG = "GetForecasts";
    private AppCompatActivity parent;

    // only retain a weak reference to the activity
    public GetForecasts(AppCompatActivity forecastactivity) {
        parent = forecastactivity;
    }

    @Override
    protected List<ForecastObject> doInBackground(Void... params) {
        return ForecastDB.getInstance(MainApplication.getInstace().getApplicationContext()).daoAccess().getForecasts();
    }

    @Override
    protected void onPostExecute(List<ForecastObject> result) {
        Log.d(TAG, "onPostExecute: " + result.toString());
        ((ForecastActivity)parent).updateData(result);
        return;
    }
}