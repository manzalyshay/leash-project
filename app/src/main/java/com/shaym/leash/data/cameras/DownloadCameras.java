package com.shaym.leash.data.cameras;

import android.os.AsyncTask;
import android.util.Log;

import com.shaym.leash.data.forecast.utils.HttpHandler;
import com.shaym.leash.models.CameraObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shaym on 3/26/18.
 */


public class DownloadCameras extends AsyncTask<Void, Void, ArrayList<CameraObject>> {
    private static final String TAG = "DownloadCameras";

    public DownloadCameras(){
        ;
    }


    @Override
    public void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    public ArrayList<CameraObject> doInBackground(Void... voids) {
        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        String url = "http://www.gsx2json.com/api?id=1u1viBEIC2ZEDD2pHvm6hcIAHkClpvw4rYUrQdBMFoY0&columns=false";


        String jsonStr = sh.makeServiceCall(url);

        Log.e(TAG, "Response from url: " + jsonStr);

        ArrayList<CameraObject> cameras = new ArrayList<>();

        if (jsonStr != null) {
            try {
                JSONObject jsonobj = new JSONObject(jsonStr);
                JSONArray jsonarr = jsonobj.getJSONArray("rows");

                for (int i=0; i<jsonarr.length(); i++) {
                    Log.d(TAG, "doInBackground:arr " + jsonarr.get(i));
                    JSONObject obj = jsonarr.getJSONObject(i);
                    if (obj.getString("isactive").equals("TRUE")) {
                        cameras.add(new CameraObject(Integer.toString(obj.getInt("id")), obj.getString("city"), obj.getString("location"), obj.getString("url"), obj.getString("method"), obj.getString("sponser"), obj.getString("thumburl"), obj.getString("sponsericonurl")));
                    }
                }

            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());

            }

        } else {
            Log.e(TAG, "Couldn't get json from server.");

        }

        return cameras;
    }

    @Override
    public void onPostExecute(ArrayList<CameraObject> result) {
        super.onPostExecute(result);
        if (result.size() > 0) {
            new SaveCamera(result).execute();
        }
        else {
            Log.d(TAG, "onPostExecute: " + "No Internet Connection");
        }
    }
}


