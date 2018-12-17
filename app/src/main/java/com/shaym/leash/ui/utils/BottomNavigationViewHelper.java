package com.shaym.leash.ui.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.shaym.leash.R;
import com.shaym.leash.ui.forecast.ForecastActivity;
import com.shaym.leash.ui.forum.ForumActivity;
import com.shaym.leash.ui.gear.GearActivity;
import com.shaym.leash.ui.home.HomeActivity;

/**
 * Created by shaym on 2/14/18.
 */

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHel";
    private AHBottomNavigation mBottomMenu;

    public int getCurrentActivity() {
        return currentActivity;
    }

    private int currentActivity;
    private Activity callingActivity;
    private ActivityOptionsCompat mOptionsCompat;

    public BottomNavigationViewHelper(Activity activity, int actNum) {
        Log.d(TAG, "BottomNavigationViewHelper: Init menu ");
        callingActivity = activity;
        currentActivity = actNum;
        mBottomMenu = callingActivity.findViewById(R.id.bottomNavViewBar);
        mOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(callingActivity, mBottomMenu, "bottomtrans");
        mBottomMenu.setAccentColor(R.color.gradStart);

        setMenuItems();
        setNavigation();
    }

    private void setMenuItems(){
        // Menu Items
        AHBottomNavigationItem mCamerasItem = new AHBottomNavigationItem(R.string.home_menu_item, R.drawable.ic_cameras, R.color.white);
        AHBottomNavigationItem mForecastItem = new AHBottomNavigationItem(R.string.forecast_menu_item, R.drawable.ic_forecast, R.color.white);
        AHBottomNavigationItem mForumItem = new AHBottomNavigationItem(R.string.forum_menu_item, R.drawable.ic_forum, R.color.white);
        AHBottomNavigationItem mGearItem = new AHBottomNavigationItem(R.string.gear_menu_item, R.drawable.ic_equip, R.color.white);

        mBottomMenu.addItem(mCamerasItem);
        mBottomMenu.addItem(mForecastItem);
        mBottomMenu.addItem(mForumItem);
        mBottomMenu.addItem(mGearItem);

        mBottomMenu.setDefaultBackgroundColor(callingActivity.getResources().getColor(R.color.white));
        mBottomMenu.setCurrentItem(currentActivity);
    }

    private void setNavigation(){
        mBottomMenu.setOnTabSelectedListener((position, wasSelected) -> {
            switch (position){
                case 0:
                    Intent intent1 = new Intent(callingActivity, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    callingActivity.startActivity(intent1, mOptionsCompat.toBundle());

                    break;
                case 1:
                    Intent intent2 = new Intent(callingActivity.getApplicationContext(), ForecastActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    callingActivity.startActivity(intent2, mOptionsCompat.toBundle());


                    break;
                case 2:
                    Intent intent3 = new Intent(callingActivity, ForumActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    callingActivity.startActivity(intent3, mOptionsCompat.toBundle());


                    break;
                case 3:
                    Intent intent4 = new Intent(callingActivity, GearActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    callingActivity.startActivity(intent4, mOptionsCompat.toBundle());

                    break;
            }
            return true;
        });
    }

    public void setPage(int p){
        mBottomMenu.setCurrentItem(p);
    }
}
