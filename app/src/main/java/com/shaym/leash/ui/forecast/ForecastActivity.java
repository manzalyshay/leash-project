package com.shaym.leash.ui.forecast;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.shaym.leash.R;
import com.shaym.leash.logic.forecast.DownloadForecast;
import com.shaym.leash.logic.forecast.ForecastHelper;
import com.shaym.leash.logic.forecast.ForecastObject;
import com.shaym.leash.logic.forecast.localdb.GetForecasts;
import com.shaym.leash.ui.forecast.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.shaym.leash.ui.utils.BottomNavigationViewHelper;
import com.shaym.leash.ui.utils.NavHelper;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

/**
 * Created by shaym on 2/14/18.
 */

public class ForecastActivity extends AppCompatActivity implements DatePickerListener {
    private static final String TAG = "ForecastActivity";
    public static final String TELAVIV_TAG = "TEL-AVIV";
    public static final String CALIFORNIA_TAG = "CALIFORNIA";
    private static final int ACTIVITY_NUM = 1;
    public final static int ISRAEL_FRAGMENT_ITEM_ID = 0201;
    public final static int CALIFORNIA_FRAGMENT_ITEM_ID = 0202;
    public ForecastActivity instance;
    private List<ForecastObject> mData;
    private List<ForecastObject> mDayData;
    private HorizontalPicker mPicker;
    private ProgressBar mProgressBar;
    private DrawerLayout mDrawerLayout;
    HorizontalInfiniteCycleViewPager mInfiniteCycleViewPager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        mDrawerLayout = findViewById(R.id.drawer_layout_forecast);
        NavigationView mNavigationView = findViewById(R.id.nav_view_forecast);
        // Register a receiver to get this fragment notified every time new Forecast objects were downloaded

        LocalBroadcastManager.getInstance(ForecastActivity.this).registerReceiver(mMessageReceiver,
                new IntentFilter("forecast"));
//        List<String> mForecastLocations = Arrays.asList(getResources().getStringArray(R.array.Forecast_Locations));
//        List<String> mForecastUrls = Arrays.asList(getResources().getStringArray(R.array.Forecast_urls));
        mProgressBar = findViewById(R.id.vpprogressbar);
        instance = ForecastActivity.this;

        setupViewPager();
        Toolbar toolbar = findViewById(R.id.toolbar_forecast);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mPicker = findViewById(R.id.datePicker);

        // initialize it and attach a listener
        mPicker
                .setListener(ForecastActivity.this)
                .setTodayDateBackgroundColor(getColor(R.color.primaryTextColor))
                .init();
        mPicker.setDate(new DateTime());
        new NavHelper(mNavigationView, mInfiniteCycleViewPager, new BottomNavigationViewHelper(ForecastActivity.this, ACTIVITY_NUM), ACTIVITY_NUM);

        new initActivity().execute();

    }

    private void setupViewPager() {
        if (mDayData != null && !mDayData.isEmpty()) {

            mInfiniteCycleViewPager = findViewById(R.id.hicvp);
            mInfiniteCycleViewPager.setAdapter(new HorizontalPagerAdapter(this, mDayData));

            mInfiniteCycleViewPager.setOffscreenPageLimit(2);
            mInfiniteCycleViewPager.setScrollDuration(500);
            mInfiniteCycleViewPager.setMediumScaled(true);
            mInfiniteCycleViewPager.setMaxPageScale(0.8F);
            mInfiniteCycleViewPager.setMinPageScale(0.5F);
            mInfiniteCycleViewPager.setCenterPageScaleOffset(30.0F);
            mInfiniteCycleViewPager.setMinPageScaleOffset(5.0F);
            mProgressBar.setVisibility(View.INVISIBLE);
        }


    }

    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(TAG + "recv", "Got message: ");
            new GetForecasts(instance).execute();

        }
    };

    public void updateData(List<ForecastObject> result){
        if (!result.isEmpty()) {
            mData = result;
            mDayData = ForecastHelper.getInstance().getForecastsByDay(ForecastHelper.getInstance().formatDay(new Date()), mData);
            setupViewPager();
        }


    }

    @Override
    public void onDestroy() {
        //Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onDateSelected(DateTime dateSelected) {
        if (mData != null && !mData.isEmpty()) {
            mDayData = ForecastHelper.getInstance().getForecastsByDay(ForecastHelper.getInstance().formatDay(dateSelected.toDate()), mData);
            if (!mDayData.isEmpty()) {
                setupViewPager();
            }
            else {
                Toast.makeText(this, R.string.forecast_notavailable_date, Toast.LENGTH_LONG).show();
                mPicker.setDate(new DateTime());
                mPicker.invalidate();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class initActivity extends AsyncTask<Void, Void, Void> {



        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected Void doInBackground(Void... voids) {



            return  null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new GetForecasts(instance).execute();
            new DownloadForecast(instance).execute(0, 4, null);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}


