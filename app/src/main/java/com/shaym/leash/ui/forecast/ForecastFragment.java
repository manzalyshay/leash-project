package com.shaym.leash.ui.forecast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.shaym.leash.R;
import com.shaym.leash.logic.forecast.DownloadForecast;
import com.shaym.leash.logic.forecast.ForecastHelper;
import com.shaym.leash.logic.forecast.ForecastObject;
import com.shaym.leash.logic.forecast.localdb.GetForecasts;
import com.shaym.leash.ui.forecast.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

import static com.shaym.leash.logic.utils.CONSTANT.DOWNLOAD_FORECAST_RESULTS;
import static com.shaym.leash.logic.utils.CONSTANT.FORECAST_BUNDLE;
import static com.shaym.leash.logic.utils.CONSTANT.FORECAST_RESULTS;
import static com.shaym.leash.logic.utils.CONSTANT.LOCAL_FORECAST_RESULTS;

/**
 * Created by shaym on 2/14/18.
 */

public class ForecastFragment extends Fragment implements DatePickerListener {
    private static final String TAG = "ForecastFragment";
    public static final String TELAVIV_TAG = "TEL-AVIV";
    public static final String CALIFORNIA_TAG = "CALIFORNIA";
    private static final int ACTIVITY_NUM = 1;
    public final static int ISRAEL_FRAGMENT_ITEM_ID = 0201;
    public final static int CALIFORNIA_FRAGMENT_ITEM_ID = 0202;
    private List<ForecastObject> mData;
    private List<ForecastObject> mDayData;
    private HorizontalPicker mPicker;
    private ProgressBar mProgressBar;
    private HorizontalInfiniteCycleViewPager mInfiniteCycleViewPager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forecast, container, false);
        return v;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();

        initUI();

        // Register a receiver to get this fragment notified every time new Forecast objects were downloaded

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mDownloadResultsReceiver,
                new IntentFilter(DOWNLOAD_FORECAST_RESULTS));

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mLocalResultsReceiver,
                new IntentFilter(LOCAL_FORECAST_RESULTS));

        new GetForecasts().execute();
        new DownloadForecast().execute(0, 4, null);
    }

    @Override
    public void onPause() {
        super.onPause();
    }








    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initUI() {

        mProgressBar = getView().findViewById(R.id.vpprogressbar);

        setupViewPager();
        initPicker();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initPicker() {
        mPicker = getView().findViewById(R.id.datePicker);
        mPicker.setBackgroundColor(Color.WHITE);


        // initialize it and attach a listener
        mPicker
                .setListener(ForecastFragment.this)
                .setTodayDateBackgroundColor(getActivity().getColor(R.color.newAccent))
                .setDateSelectedColor(getActivity().getColor(R.color.newAccent))
                .setMonthAndYearTextColor(getActivity().getColor(R.color.FragmentBackground))
                .setDayOfWeekTextColor(getActivity().getColor(R.color.FragmentBackground))
                .setUnselectedDayTextColor(getActivity().getColor(R.color.FragmentBackground))
                .showTodayButton(false)
                .init();
        mPicker.setDate(new DateTime());

    }



    private void setupViewPager() {
        if (mDayData != null && !mDayData.isEmpty()) {

            mInfiniteCycleViewPager = getView().findViewById(R.id.hicvp);
            mInfiniteCycleViewPager.setAdapter(new HorizontalPagerAdapter(getContext(), mDayData));

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
    private BroadcastReceiver mDownloadResultsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(TAG + " Web", "Got message: ");
            new GetForecasts().execute();

        }
    };

    private BroadcastReceiver mLocalResultsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(TAG + " Local", "Got message: ");
            Bundle args = intent.getBundleExtra(FORECAST_BUNDLE);
            List<ForecastObject> result = (List<ForecastObject>) args.getSerializable(FORECAST_RESULTS);

            updateData(result);
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
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mLocalResultsReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mDownloadResultsReceiver);

    }

    @Override
    public void onDateSelected(DateTime dateSelected) {
        if (mData != null && !mData.isEmpty()) {
            mDayData = ForecastHelper.getInstance().getForecastsByDay(ForecastHelper.getInstance().formatDay(dateSelected.toDate()), mData);
            if (!mDayData.isEmpty()) {
                setupViewPager();
            }
            else {
//                Toast.makeText(getContext(), R.string.forecast_notavailable_date, Toast.LENGTH_LONG).show();
                mPicker.setDate(new DateTime());
                mPicker.invalidate();
            }
        }
    }




}


