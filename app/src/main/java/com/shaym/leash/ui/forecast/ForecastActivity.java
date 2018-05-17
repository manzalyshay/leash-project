package com.shaym.leash.ui.forecast;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shaym.leash.R;
import com.shaym.leash.logic.forecast.DownloadForecast;
import com.shaym.leash.ui.home.SectionPagerAdapter;
import com.shaym.leash.ui.utils.BottomNavigationViewHelper;

import java.util.Arrays;
import java.util.List;

import travel.ithaka.android.horizontalpickerlib.PickerLayoutManager;

/**
 * Created by shaym on 2/14/18.
 */

public class ForecastActivity extends AppCompatActivity {
    private static final String TAG = "ForecastActivity";
    public static final String TELAVIV_TAG = "TEL-AVIV";
    public static final String CALIFORNIA_TAG = "CALIFORNIA";
    private static final int ACTIVITY_NUM = 1;
    private BottomNavigationViewHelper mBottomNavHelper;
    private RecyclerView mCountryPick;
    private PickerAdapter adapter;
    private List<String> mForecastLocations;
    private List<String> mForecastUrls;
    public ForecastActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        mBottomNavHelper = new BottomNavigationViewHelper(this, ACTIVITY_NUM);
        mCountryPick = (RecyclerView) findViewById(R.id.forecastpick);
        mForecastLocations = Arrays.asList(getResources().getStringArray(R.array.Forecast_Locations));
        mForecastUrls = Arrays.asList(getResources().getStringArray(R.array.Forecast_urls));
        setUpPicker();
        instance = this;
        new DownloadForecast(instance).execute(0, 4, null);

    }

    private void setUpPicker() {
        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        sectionPagerAdapter.AddFragment(new ForecastFragment());
        ViewPager vp = (ViewPager) findViewById(R.id.container);

        vp.setAdapter(sectionPagerAdapter);
        PickerLayoutManager pickerLayoutManager = new PickerLayoutManager(this, PickerLayoutManager.HORIZONTAL, false);
        pickerLayoutManager.setChangeAlpha(true);
        pickerLayoutManager.setScaleDownBy(0.99f);
        pickerLayoutManager.setScaleDownDistance(0.8f);

        adapter = new PickerAdapter(this, mForecastLocations, mCountryPick);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mCountryPick);
        mCountryPick.setLayoutManager(pickerLayoutManager);
        mCountryPick.setAdapter(adapter);

        pickerLayoutManager.setOnScrollStopListener(new PickerLayoutManager.onScrollStopListener() {
            @Override
            public void selectedView(View view) {
                switch (((TextView) view).getText().toString().toUpperCase()){
                    case (TELAVIV_TAG):
                        new DownloadForecast(instance).execute(0, 4, null);
                        break;
                    case (CALIFORNIA_TAG):
                        new DownloadForecast(instance).execute(1, 4, null);
                        break;

                }
            }
        });
    }






}


