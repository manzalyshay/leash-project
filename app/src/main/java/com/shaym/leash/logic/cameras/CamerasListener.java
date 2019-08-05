package com.shaym.leash.logic.cameras;

import com.shaym.leash.logic.forecast.localdb.dbutils.ForecastObject;

import java.util.List;

public interface CamerasListener {

    void onCamerasLoaded(List<List<CameraObject>> camerasbycity);

}
