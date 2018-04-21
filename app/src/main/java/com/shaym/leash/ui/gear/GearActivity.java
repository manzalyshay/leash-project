package com.shaym.leash.ui.gear;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.shaym.leash.R;
import com.shaym.leash.ui.utils.BottomNavigationViewHelper;

/**
 * Created by shaym on 2/14/18.
 */

public class GearActivity extends AppCompatActivity {
    private static final String TAG = "GearActivity";
    private BottomNavigationViewHelper mBottomNavHelper;
    private static final int ACTIVITY_NUM = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBottomNavHelper = new BottomNavigationViewHelper(this, ACTIVITY_NUM);


    }
    
}

