package com.shaym.leash.data.cameras;

import com.shaym.leash.models.CameraObject;

import java.util.List;

public interface CamerasListener {

    void onCamerasLoaded(List<List<CameraObject>> camerasbycity);

}
