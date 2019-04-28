package com.shaym.leash.logic.forecast.localdb;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.shaym.leash.MainApplication;
import com.shaym.leash.logic.forecast.ForecastObject;
import com.shaym.leash.logic.forecast.localdb.dbhandlers.ForecastDB;

import java.io.Serializable;
import java.util.List;

import static com.shaym.leash.logic.utils.CONSTANT.FORECAST_BUNDLE;
import static com.shaym.leash.logic.utils.CONSTANT.FORECAST_RESULTS;
import static com.shaym.leash.logic.utils.CONSTANT.LOCAL_FORECAST_RESULTS;

public class GetForecasts extends AsyncTask<Void, Void, List<ForecastObject>> {
    private static final String TAG = "GetForecasts";

    // only retain a weak reference to the activity
    public GetForecasts() {
    }

    @Override
    protected List<ForecastObject> doInBackground(Void... params) {
        return ForecastDB.getInstance(MainApplication.getInstace().getApplicationContext()).daoAccess().getForecasts();
    }

    @Override
    protected void onPostExecute(List<ForecastObject> result) {
        Log.d(TAG, "onPostExecute: " + result.toString());
        Log.d("sender", "Broadcasting cameras change");
        Intent intent1 = new Intent(LOCAL_FORECAST_RESULTS);

        Bundle args = new Bundle();
        args.putSerializable(FORECAST_RESULTS, (Serializable) result);

        intent1.putExtra(FORECAST_BUNDLE ,args);
        LocalBroadcastManager.getInstance(MainApplication.getInstace().getApplicationContext()).sendBroadcast(intent1);
    }
}