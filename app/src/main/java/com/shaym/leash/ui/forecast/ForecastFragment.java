package com.shaym.leash.ui.forecast;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.tabs.TabLayout;
import com.shaym.leash.R;
import com.shaym.leash.data.forecast.DownloadForecast;
import com.shaym.leash.models.ForecastAVGObject;
import com.shaym.leash.data.forecast.ForecastHelper;
import com.shaym.leash.data.forecast.ForecastListener;
import com.shaym.leash.data.forecast.localdb.dbutils.ForecastObject;
import com.shaym.leash.data.forecast.localdb.dbutils.ForecastViewModel;
import com.shaym.leash.data.forecast.localdb.dbutils.GetForecastsByDays;
import com.shaym.leash.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by shaym on 2/14/18.
 */

public class ForecastFragment extends Fragment implements  ForecastListener, TabLayout.OnTabSelectedListener {
    private static final String TAG = "ForecastFragment";
    public static final String TELAVIV_TAG = "TEL-AVIV";
    public static final String CALIFORNIA_TAG = "CALIFORNIA";

    private TextView mMinWaveHeight;
    private TextView mMaxWaveHeight;
    private TextView mWindSpeed;
    private ImageView mWindDirection;
    private TextView mCurrentTemp;
    private List<List<ForecastObject>> mForecastByDays;
    private TabLayout mDayPicker;
    private LineChart mChart;
    private static ArrayList<String> Xval = new ArrayList<>();
    private ForecastViewModel mForecastViewModel;

    public ForecastFragment (){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forecast, container, false);
        Log.d(TAG, "onCreateView: ");
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        new DownloadForecast(0).execute();
        initUI();
    }

    private void initUI() {
        mMinWaveHeight = Objects.requireNonNull(getView()).findViewById(R.id.min_wave_height);
        mMaxWaveHeight = getView().findViewById(R.id.max_wave_height);
        mWindSpeed = getView().findViewById(R.id.wind_speed);
        mWindDirection = getView().findViewById(R.id.wind_direction_icon);
        mCurrentTemp = getView().findViewById(R.id.temp_value);
        mDayPicker = getView().findViewById(R.id.days_menu);
        mDayPicker.addOnTabSelectedListener(this);

        initChart();
        initForecastViewModel();
    }

    private void initForecastViewModel() {
        mForecastViewModel = ViewModelProviders.of(this).get(ForecastViewModel.class);
        mForecastViewModel.getAllForecasts().observe(this, forecasts -> {
            // Update the cached copy of the words in the adapter.
            Log.d(TAG, "Forecast Observer Triggered");
            new GetForecastsByDays(ForecastFragment.this, forecasts).execute();

        });
    }

    private void initChart() {
        mChart = Objects.requireNonNull(getView()).findViewById(R.id.forecast_chart);
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        // to use for it
        mChart.getDescription().setText("");

        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");

        LimitLine ll1 = new LimitLine(150f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setLabel("TWO");
        ll1.setTypeface(tf);
        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll1.setLabel("ONE");
        ll2.setTypeface(tf);
        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setValueFormatter((value, axis) -> {
            return Xval.get((int) value); // xVal is a string array
        });
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(1.3f);
        leftAxis.setAxisMinimum(0f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        mChart.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
    }



    private void updateAVGUI(ForecastAVGObject avgObjectToday){
            if (avgObjectToday.mAVGAbsMinBreakingHeight < 1) {
                float normalized = avgObjectToday.mAVGAbsMinBreakingHeight*100;
                mMinWaveHeight.setText(String.valueOf((int) normalized));
            }
            else {
                mMinWaveHeight.setText(String.valueOf((int) avgObjectToday.mAVGAbsMinBreakingHeight));

            }

            if (avgObjectToday.mAVGAbsMaxBreakingHeight < 1){
                float normalized = avgObjectToday.mAVGAbsMaxBreakingHeight*100;

                mMaxWaveHeight.setText(String.valueOf((int) normalized));

            }
            else {
                mMaxWaveHeight.setText(String.valueOf((int) avgObjectToday.mAVGAbsMaxBreakingHeight ));

            }
            mWindSpeed.setText(String.valueOf(avgObjectToday.mAVGWindSpeed));
            mCurrentTemp.setText(String.valueOf(avgObjectToday.mAVGTemp));
            mWindDirection.setRotation(avgObjectToday.mAVGWindDirection);


        }



    @Override
    public void onForecastsLoaded(List<List<ForecastObject>> forecastByDays, List<String> days) {
        Log.d(TAG, "onForecastsLoaded: ");
        mForecastByDays = forecastByDays;
        mDayPicker.removeAllTabs();

        for (int i=0; i<days.size(); i++){
            UIHelper.getInstance().addTab(mDayPicker, days.get(i), mDayPicker.getTabCount() == 0);
        }

    }



    private void updateForecastChart(List<ForecastObject> chartforecasts, int currentForecast) {

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < chartforecasts.size(); i++) {
            Xval.add(ForecastHelper.getInstance().formatHour(chartforecasts.get(i).getLocalTimeStamp()));


            switch (currentForecast) {
                case 0:
                    values.add(new Entry(i, chartforecasts.get(i).getAbsMaxBreakingHeight(), getContext().getResources().getDrawable(R.drawable.star)));
                    break;

                case 1:
                    values.add(new Entry(i, chartforecasts.get(i).getWindSpeed(), getContext().getResources().getDrawable(R.drawable.star)));
                    break;

                case 2:
                    values.add(new Entry(i, chartforecasts.get(i).getTemp(), getContext().getResources().getDrawable(R.drawable.star)));
                    break;
            }


            LineDataSet set1;

            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                set1.setValues(values);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.invalidate();
            } else {
                // create a dataset and give it a type
                set1 = new LineDataSet(values, "");

                set1.setDrawIcons(false);

                // set the line to be drawn like this "- - - - - -"
                set1.enableDashedLine(10f, 5f, 0f);
                set1.enableDashedHighlightLine(10f, 5f, 0f);
                set1.setColor(Color.BLACK);
                set1.setCircleColor(Color.BLACK);
                set1.setLineWidth(1f);
                set1.setCircleRadius(3f);
                set1.setDrawCircleHole(false);
                set1.setValueTextSize(9f);
                set1.setDrawFilled(true);
                set1.setFormLineWidth(1f);
                set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                set1.setFormSize(15.f);


                if (com.github.mikephil.charting.utils.Utils.getSDKInt() >= 18) {
                    // fill drawable only supported on api level 18 and above
                    Drawable drawable = ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.color.bottom_nav_bg_color);
                    set1.setFillDrawable(drawable);
                } else {
                    set1.setFillColor(Color.BLACK);
                }

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1); // add the datasets

                // create a data object with the datasets
                LineData data = new LineData(dataSets);

                // set data
                mChart.setData(data);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.invalidate();

            }

    }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d(TAG, "onDaySelected: " + tab.getPosition());
        updateAVGUI((ForecastAVGObject)(mForecastByDays.get(tab.getPosition()).get(mForecastByDays.get(tab.getPosition()).size()-1)));

        List<ForecastObject> Chartforecasts = new ArrayList<>(mForecastByDays.get(tab.getPosition()));
        Chartforecasts.remove(Chartforecasts.size()-1);

        updateForecastChart(Chartforecasts, 0);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}


