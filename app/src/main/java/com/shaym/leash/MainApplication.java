package com.shaym.leash;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.shaym.leash.ui.utils.BottomNavigationViewHelper;

/**
 * Created by shaym on 2/16/18.
 */

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    public static BottomNavigationViewHelper mBottomNavHelper;
    private static MainApplication mInstance;

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    @Override
    public void onCreate(){
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        mInstance = this;
    }

    public static MainApplication getInstace(){
        return mInstance;
    }



}
