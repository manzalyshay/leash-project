package com.shaym.leash.logic.cameras;

import android.os.AsyncTask;
import android.util.Log;

import com.shaym.leash.logic.forecast.ForecastListener;

import java.util.ArrayList;
import java.util.List;

import static com.shaym.leash.logic.utils.CONSTANT.ashdod_location;
import static com.shaym.leash.logic.utils.CONSTANT.caesarea_location;
import static com.shaym.leash.logic.utils.CONSTANT.herzelia_location;
import static com.shaym.leash.logic.utils.CONSTANT.telaviv_location;

public class GetCamerasByCity extends AsyncTask<Void, Void, List<List<CameraObject>>> {
    private static final String TAG = "GetForecastsByDays";
    private List<List<CameraObject>> mCamerasByCity;
    private CamerasListener mListener;
    private List<CameraObject> allCameras;

    // only retain a weak reference to the activity
    public GetCamerasByCity(CamerasListener listener, List<CameraObject> forecasts) {
        mListener = listener;
        allCameras = forecasts;
    }

    @Override
    protected List<List<CameraObject>> doInBackground(Void... params) {
        mCamerasByCity = new ArrayList<>();
        List<CameraObject> telavivCameras = new ArrayList<>();
        List<CameraObject> ashdodCameras = new ArrayList<>();
        List<CameraObject> caesareaCameras = new ArrayList<>();
        List<CameraObject> herzeliaCameras = new ArrayList<>();


            for (int i = 0; i < allCameras.size(); i++) {
                CameraObject cam = allCameras.get(i);

                switch (cam.getCity()){
                    case telaviv_location:
                        telavivCameras.add(cam);
                        break;

                    case ashdod_location:
                        ashdodCameras.add(cam);
                        break;

                    case caesarea_location:
                        caesareaCameras.add(cam);
                        break;

                    case herzelia_location:
                        herzeliaCameras.add(cam);
                        break;

                }
            }

            mCamerasByCity.add(telavivCameras);
            mCamerasByCity.add(ashdodCameras);
            mCamerasByCity.add(caesareaCameras);
            mCamerasByCity.add(herzeliaCameras);
            Log.d(TAG, "doInBackground: " + mCamerasByCity.toString());

            return mCamerasByCity;

    }





    @Override
    protected void onPostExecute(List<List<CameraObject>> result) {
        if (result != null) {
            Log.d(TAG, "onPostExecute: " + result.toString());
            mListener.onCamerasLoaded(result);
        }
    }
}