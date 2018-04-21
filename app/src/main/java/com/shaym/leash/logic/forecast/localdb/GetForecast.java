package com.shaym.leash.logic.forecast.localdb;


import android.os.AsyncTask;



public class GetForecast extends AsyncTask<Void, Void, Void> {
    private long mTimestamp;


    public GetForecast(long time_stamp){
        this.mTimestamp = time_stamp;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Let's add some dummy data to the database.

//        for (int i=0; i<forecasts.size(); i++) {
//            //Now access all the methods defined in DaoAccess with sampleDatabase object
//            if(ForecastDB.getInstance(MainApplication.getInstace().getApplicationContext()).daoAccess().getForecast(forecasts.get(i).getLocalTimeStamp()) == null) {
//                ForecastDB.getInstance(MainApplication.getInstace().getApplicationContext()).daoAccess().saveForecast(forecasts.get(i));
//            }
        return null;
    }


    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);

    }
}