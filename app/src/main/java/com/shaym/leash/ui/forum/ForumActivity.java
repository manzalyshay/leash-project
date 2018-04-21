package com.shaym.leash.ui.forum;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.shaym.leash.MainApplication;
import com.shaym.leash.R;
import com.shaym.leash.ui.utils.BottomNavigationViewHelper;

/**
 * Created by shaym on 2/14/18.
 */

public class ForumActivity extends AppCompatActivity {
    private static final String TAG = "ForumActivity";
    private static final int ACTIVITY_NUM = 2;
    private BottomNavigationViewHelper mBottomNavHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setContentView(R.layout.activity_home);
        mBottomNavHelper = new BottomNavigationViewHelper(this, ACTIVITY_NUM);

    }
    
}
