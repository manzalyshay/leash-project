package com.shaym.leash;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.cloudinary.android.MediaManager;
import com.facebook.FacebookSdk;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

/**
 * Created by shaym on 2/16/18.
 */

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
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
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        MediaManager.init(this);

    }

    public static MainApplication getInstance(){
        return mInstance;
    }



}
