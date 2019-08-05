package com.shaym.leash.logic.forecast.utils;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.shaym.leash.logic.cameras.CameraObject;
import com.shaym.leash.logic.forecast.localdb.dbutils.ForecastObject;

import java.util.List;

@Dao
public interface CamerasDaoAccess {

    @Insert
    void saveCameras(List<CameraObject> cameraObjects);

    @Insert
    void saveCamera(CameraObject cameraObject);

    @Query("SELECT * FROM Cameras WHERE id =:id")
    CameraObject getCamera(String id);

    @Query("SELECT * FROM Cameras")
    LiveData<List<CameraObject>> getCameras();

    @Update
    void updateCameraRecord(CameraObject cameraObject);

    @Delete
    void deleteCamera(CameraObject cameraObject);

    @Query("DELETE FROM Cameras")
    void deleteAllCameras();
}