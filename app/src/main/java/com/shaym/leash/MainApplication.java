package com.shaym.leash;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.firebase.database.FirebaseDatabase;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.ui.utils.BottomNavigationViewHelper;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

/**
 * Created by shaym on 2/16/18.
 */

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    public static BottomNavigationViewHelper mBottomNavHelper;
    private static MainApplication mInstance;
    private Profile mCurrentProfile;

    public Profile getCurrentProfile() {
        return mCurrentProfile;
    }

    public void setCurrentProfile(Profile mCurrentProfile) {
        this.mCurrentProfile = mCurrentProfile;
    }


    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    @Override
    public void onCreate(){
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        mInstance = this;
        FacebookSdk.sdkInitialize(getApplicationContext());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }

    public static MainApplication getInstace(){
        return mInstance;
    }



}
