package com.shaym.leash.logic.forecast;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


import com.shaym.leash.MainApplication;
import com.shaym.leash.R;
import com.shaym.leash.logic.forecast.http.HttpHandler;
import com.shaym.leash.logic.forecast.localdb.SaveForecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.shaym.leash.ui.forecast.ForecastActivity.CALIFORNIA_TAG;
import static com.shaym.leash.ui.forecast.ForecastActivity.TELAVIV_TAG;

/**
 * Created by shaym on 3/26/18.
 */


public class DownloadForecast extends AsyncTask<Integer, Void, ArrayList<ForecastObject>> {
    private long mLocalTimeStamp;
    private float mAbsMinBreakingHeight;
    private float mAbsMaxBreakingHeight;
    private int mWindSpeed;
    private int mWindDirection;
    private int mTempChill;
    private int mTemp;



    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected ArrayList<ForecastObject> doInBackground(Integer... ints) {
        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        List<String> urls = Arrays.asList(MainApplication.getInstace().getApplicationContext().getResources().getStringArray(R.array.Forecast_urls));
        int loc = ints[0];
        String url;
        String location;
        if (loc == 0){
            location = TELAVIV_TAG;
            url = urls.get(0);
        }
        else {
            location = CALIFORNIA_TAG;
            url = urls.get(1);
        }

        String jsonStr = sh.makeServiceCall(url);

        Log.e(TAG, "Response from url: " + jsonStr);

        ArrayList<ForecastObject> forecasts = new ArrayList<ForecastObject>();

        if (jsonStr != null) {
            try {
                JSONArray jsonarr = new JSONArray(jsonStr);

                for (int i=0; i<jsonarr.length(); i++) {

                    JSONObject jsonobj = jsonarr.getJSONObject(i);
//                    Log.e(TAG, "parsing obj: " + jsonobj);

                    mLocalTimeStamp = jsonobj.getLong("localTimestamp");
//                    Log.e(TAG, "localtimestamp: " + mLocalTimeStamp);

                    JSONObject swell = jsonobj.getJSONObject("swell");
//                    Log.e(TAG, "swellobject: " + swell);
                    mAbsMinBreakingHeight = (float)swell.getDouble("absMinBreakingHeight");
//                    Log.e(TAG, "absMinBreakingHeight: " + mAbsMinBreakingHeight);
                    mAbsMaxBreakingHeight = (float)swell.getDouble("absMaxBreakingHeight");
//                    Log.e(TAG, "absMaxBreakingHeight: " + mAbsMaxBreakingHeight);


                    JSONObject wind = jsonobj.getJSONObject("wind");
//                    Log.e(TAG, "windobject: " + wind);
                    mWindSpeed = wind.getInt("speed");
//                    Log.e(TAG, "WindSpeed: " + mWindSpeed);
                    mWindDirection = wind.getInt("direction");
//                    Log.e(TAG, "WindDirection: " + mWindDirection);
                    mTempChill = wind.getInt("chill");
//                    Log.e(TAG, "TempChill: " + mTempChill);

                    JSONObject condition = jsonobj.getJSONObject("condition");
//                    Log.e(TAG, "condition: " + condition);
                    mTemp = condition.getInt("temperature");
//                    Log.e(TAG, "Temp: " + mTemp);

                    forecasts.add(new ForecastObject(location, mLocalTimeStamp, mAbsMinBreakingHeight, mAbsMaxBreakingHeight, mWindSpeed, mWindDirection, mTempChill, mTemp));
                }

            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());

            }

        } else {
            Log.e(TAG, "Couldn't get json from server.");

        }

        return forecasts;
    }

    @Override
    protected void onPostExecute(ArrayList<ForecastObject> result) {
        super.onPostExecute(result);
        if (result.size() > 0) {
            new SaveForecast(result).execute();

            Log.d("sender", "Broadcasting Forecast Result");
            Intent intent = new Intent("forecast");
            // You can also include some extra data.
            intent.putExtra("result", (Serializable) result);
            LocalBroadcastManager.getInstance(MainApplication.getInstace().getApplicationContext()).sendBroadcast(intent);
        }
        else {
            Toast.makeText(MainApplication.getInstace().getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG);
        }
    }
}


