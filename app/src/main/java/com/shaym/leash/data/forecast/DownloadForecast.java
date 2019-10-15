package com.shaym.leash.data.forecast;

import android.os.AsyncTask;
import android.util.Log;

import com.shaym.leash.MainApplication;
import com.shaym.leash.R;
import com.shaym.leash.data.forecast.localdb.dbutils.ForecastObject;
import com.shaym.leash.data.forecast.utils.HttpHandler;
import com.shaym.leash.data.forecast.localdb.dbutils.SaveForecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.shaym.leash.ui.forecast.ForecastFragment.TELAVIV_TAG;

/**
 * Created by shaym on 3/26/18.
 */


public class DownloadForecast extends AsyncTask<Void, Void, ArrayList<ForecastObject>> {
    private static final String TAG = "DownloadForecast";
    private int locationNum;
    public DownloadForecast (int loc){
        locationNum = loc;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected ArrayList<ForecastObject> doInBackground(Void... voids) {
        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        List<String> urls = Arrays.asList(MainApplication.getInstance().getApplicationContext().getResources().getStringArray(R.array.Forecast_urls));
        String url;
        String location;
        if (locationNum == 0){
            location = TELAVIV_TAG;
            url = urls.get(0);
        }
        else {
            location = TELAVIV_TAG;
            url = urls.get(1);
        }

        String jsonStr = sh.makeServiceCall(url);

        Log.e(TAG, "Response from url: " + jsonStr);

        ArrayList<ForecastObject> forecasts;
        forecasts = new ArrayList<>();

        if (jsonStr != null) {
            try {
                JSONArray jsonarr = new JSONArray(jsonStr);

                for (int i=0; i<jsonarr.length(); i++) {

                    JSONObject jsonobj = jsonarr.getJSONObject(i);
//                    Log.e(TAG, "parsing obj: " + jsonobj);

                    long mLocalTimeStamp = jsonobj.getLong("localTimestamp");
//                    Log.e(TAG, "localtimestamp: " + mLocalTimeStamp);

                    JSONObject swell = jsonobj.getJSONObject("swell");
//                    Log.e(TAG, "swellobject: " + swell);
                    float mAbsMinBreakingHeight = (float) swell.getDouble("minBreakingHeight");
//                    Log.e(TAG, "absMinBreakingHeight: " + mMinBreakingHeight);
                    float mAbsMaxBreakingHeight = (float) swell.getDouble("maxBreakingHeight");
//                    Log.e(TAG, "absMaxBreakingHeight: " + mMaxBreakingHeight);


                    JSONObject wind = jsonobj.getJSONObject("wind");
//                    Log.e(TAG, "windobject: " + wind);
                    int mWindSpeed = wind.getInt("speed");
//                    Log.e(TAG, "WindSpeed: " + mWindSpeed);
                    int mWindDirection = wind.getInt("direction");
//                    Log.e(TAG, "WindDirection: " + mWindDirection);
                    int mTempChill = wind.getInt("chill");
//                    Log.e(TAG, "TempChill: " + mTempChill);

                    JSONObject condition = jsonobj.getJSONObject("condition");
//                    Log.e(TAG, "condition: " + condition);
                    int mTemp = condition.getInt("temperature");
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
        }
        else {
            Log.d(TAG, "onPostExecute: " + "No Internet Connection");
        }
    }
}


