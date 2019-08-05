package com.shaym.leash.logic.cameras;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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
