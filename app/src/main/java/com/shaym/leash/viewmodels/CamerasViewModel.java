package com.shaym.leash.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.shaym.leash.data.cameras.CamerasDB;
import com.shaym.leash.models.CameraObject;

import java.util.List;

public class CamerasViewModel extends AndroidViewModel {

    private LiveData<List<CameraObject>> mAllCameras;

    public CamerasViewModel(@NonNull Application application) {
        super(application);
        mAllCameras = CamerasDB.getInstance(application).daoAccess().getCameras();
    }

    public LiveData<List<CameraObject>> getAllCameras() {
        return mAllCameras;
    }
}
