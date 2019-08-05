package com.shaym.leash.logic.cameras;


import android.os.AsyncTask;
import android.util.Log;

import com.shaym.leash.MainApplication;
import com.shaym.leash.logic.forecast.localdb.dbutils.ForecastDB;
import com.shaym.leash.logic.forecast.localdb.dbutils.ForecastObject;

import java.util.ArrayList;

public class SaveCamera extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "SaveCamera";
    private ArrayList<CameraObject> cameraObjects;

    public SaveCamera(ArrayList<CameraObject> data){
        cameraObjects = data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Let's add some dummy data to the database.
        CamerasDB.getInstance(MainApplication.getInstace().getApplicationContext()).daoAccess().deleteAllCameras();

        for (int i=0; i<cameraObjects.size(); i++) {
                CamerasDB.getInstance(MainApplication.getInstace().getApplicationContext()).daoAccess().saveCamera(cameraObjects.get(i));
                Log.d(TAG, "newCameraAdded: True");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
    }
}