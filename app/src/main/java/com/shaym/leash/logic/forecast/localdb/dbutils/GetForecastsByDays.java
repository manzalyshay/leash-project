package com.shaym.leash.logic.forecast.localdb.dbutils;

import android.os.AsyncTask;
import android.util.Log;

import com.shaym.leash.logic.forecast.ForecastAVGObject;
import com.shaym.leash.logic.forecast.ForecastHelper;
import com.shaym.leash.logic.forecast.ForecastListener;

import java.util.ArrayList;
import java.util.List;

public class GetForecastsByDays extends AsyncTask<Void, Void, List<List<ForecastObject>>> {
    private static final String TAG = "GetForecastsByDays";
    private List<List<ForecastObject>> mForecastByDays;
    private List<String> mDays;
    private ForecastListener mListener;
    private List<ForecastObject> allForecasts;

    // only retain a weak reference to the activity
    public GetForecastsByDays(ForecastListener listener, List<ForecastObject> forecasts) {
        mListener = listener;
        allForecasts = forecasts;
    }

    @Override
    protected List<List<ForecastObject>> doInBackground(Void... params) {
        mForecastByDays = new ArrayList<>();
        if (allForecasts.size() >0) {

            String day = ForecastHelper.getInstance().formatDay(ForecastHelper.getInstance().formatTimeStamp(allForecasts.get(0).getLocalTimeStamp()));
            mForecastByDays.add(ForecastHelper.getInstance().getForecastsByDay(day, allForecasts));

            for (int i = 0; i < allForecasts.size(); i++) {
                String newday = ForecastHelper.getInstance().formatDay(ForecastHelper.getInstance().formatTimeStamp(allForecasts.get(i).getLocalTimeStamp()));
                if (!newday.equals(day)) {
                    mForecastByDays.add(ForecastHelper.getInstance().getForecastsByDay(newday, allForecasts));
                    day = newday;
                }
            }
            Log.d(TAG, "doInBackground: " + mForecastByDays.toString());

            mDays = new ArrayList<>();

            for (int i = 0; i < mForecastByDays.size(); i++) {
                ForecastAVGObject avgforcastobj = (ForecastAVGObject)mForecastByDays.get(i).get(mForecastByDays.get(i).size()-1);
                String daystring = avgforcastobj.getDay();
                mDays.add(daystring);
            }
            return mForecastByDays;
        }
        else
            return null;
        }





    @Override
    protected void onPostExecute(List<List<ForecastObject>> result) {
        if (result != null) {
            Log.d(TAG, "onPostExecute: " + result.toString());
            mDays.remove(mDays.size()-1);
            mListener.onForecastsLoaded(result, mDays);
        }
    }
}