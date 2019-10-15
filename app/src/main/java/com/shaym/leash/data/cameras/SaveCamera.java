package com.shaym.leash.data.cameras;


import android.os.AsyncTask;
import android.util.Log;

import com.shaym.leash.MainApplication;
import com.shaym.leash.models.CameraObject;

import java.util.ArrayList;

public class SaveCamera extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "SaveCamera";
    private ArrayList<CameraObject> cameraObjects;

    SaveCamera(ArrayList<CameraObject> data){
        cameraObjects = data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Let's add some dummy data to the database.
        CamerasDB.getInstance(MainApplication.getInstance().getApplicationContext()).daoAccess().deleteAllCameras();

        for (int i=0; i<cameraObjects.size(); i++) {
                CamerasDB.getInstance(MainApplication.getInstance().getApplicationContext()).daoAccess().saveCamera(cameraObjects.get(i));
                Log.d(TAG, "newCameraAdded: True");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
    }
}