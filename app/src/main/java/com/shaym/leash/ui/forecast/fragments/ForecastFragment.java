package com.shaym.leash.ui.forecast.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.shaym.leash.MainApplication;
import com.shaym.leash.R;
import com.shaym.leash.logic.forecast.localdb.dbhandlers.ForecastDB;
import com.shaym.leash.logic.forecast.ForecastObject;
import com.shaym.leash.ui.forecast.MyMarkerView;
import com.shaym.leash.ui.forecast.PickerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import travel.ithaka.android.horizontalpickerlib.PickerLayoutManager;

public class ForecastFragment extends Fragment {
    private CombinedChart mTempChart;
    private CombinedChart mWindChart;
    private LineChart mWavesChart;
    private ArrayList<String> waveXVal = new ArrayList<>();
    private ArrayList<String> tempXVal = new ArrayList<>();
    private ArrayList<String> windXVal = new ArrayList<>();
    private XAxis mWindXaxis;
    private XAxis mTempXaxis;
    private ForecastDB forecastDB;
    private RecyclerView mDayPicker;
    public ForecastFragment instance;
    private List<ForecastObject> mData;
    private PickerAdapter adapter;

    public ForecastFragment (){
        instance = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forecast, container, false);

        // Register a receiver to get this fragment notified every time new Forecast objects were downloaded
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("forecast"));

        new GetForecasts(instance).execute();

        chartsSetup(v);
        mDayPicker = (RecyclerView) v.findViewById(R.id.forecastdaypick);

        return v;
    }

    private void setDayPicker() {

        final List<String> days = getDays();
        PickerLayoutManager pickerLayoutManager = new PickerLayoutManager(instance.getContext(), PickerLayoutManager.HORIZONTAL, false);

        adapter = new PickerAdapter(instance.getContext(), days, mDayPicker);
        SnapHelper snapHelper = new LinearSnapHelper();
        mDayPicker.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(mDayPicker);
        mDayPicker.setLayoutManager(pickerLayoutManager);
        mDayPicker.setAdapter(adapter);

        pickerLayoutManager.setOnScrollStopListener(new PickerLayoutManager.onScrollStopListener() {
            @Override
            public void selectedView(View view) {
                ArrayList<ForecastObject> list = getForecastsByDay (((TextView) view).getText().toString().toUpperCase());
                setWavesData(list);
                setTempData(list);
                setWindData(list);

                }
            });
        }


    private void chartsSetup(View v) {
        mWavesChart = (LineChart) v.findViewById(R.id.wave_chart);
        mWavesChart.setTouchEnabled(true);

        // enable scaling and dragging
        mWavesChart.setDragEnabled(true);
        mWavesChart.setScaleEnabled(true);
        mWavesChart.setPinchZoom(true);
        // to use for it
        MyMarkerView mv = new MyMarkerView(v.getContext(), R.layout.custom_marker_view);
        mv.setChartView(mWavesChart); // For bounds control
        mWavesChart.setMarker(mv); // Set the marker to the chart);

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
        ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(-30f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);
        XAxis xAxis = mWavesChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return waveXVal.get((int) value); // xVal is a string array
            }

        });
        YAxis leftAxis = mWavesChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(2f);
        leftAxis.setAxisMinimum(0f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mWavesChart.getAxisRight().setEnabled(false);

        mWavesChart.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mWavesChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);

        mWindChart = (CombinedChart) v.findViewById(R.id.wind_chart);
        mWindChart.getDescription().setEnabled(false);
        mWindChart.setBackgroundColor(Color.WHITE);
        mWindChart.setDrawGridBackground(false);
        mWindChart.setDrawBarShadow(false);
        mWindChart.setHighlightFullBarEnabled(false);

        mWindChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        Legend lwind = mWindChart.getLegend();
        lwind.setWordWrapEnabled(true);
        lwind.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        lwind.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        lwind.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        lwind.setDrawInside(false);

        YAxis rightAxis = mWindChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis1 = mWindChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        mWindXaxis = mWindChart.getXAxis();
        mWindXaxis.setPosition(XAxis.XAxisPosition.TOP);
        mWindXaxis.setAxisMinimum(0f);
        mWindXaxis.setGranularity(1f);
        mWindXaxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return windXVal.get((int) value); // xVal is a string array
            }

        });

        mTempChart = (CombinedChart) v.findViewById(R.id.temp_chart);
        mTempChart.getDescription().setEnabled(false);
        mTempChart.setBackgroundColor(Color.WHITE);
        mTempChart.setDrawGridBackground(false);
        mTempChart.setDrawBarShadow(false);
        mTempChart.setHighlightFullBarEnabled(false);

        mTempChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        Legend ltemp = mTempChart.getLegend();
        ltemp.setWordWrapEnabled(true);
        ltemp.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        ltemp.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        ltemp.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        ltemp.setDrawInside(false);

        YAxis rightAxis1 = mTempChart.getAxisRight();
        rightAxis1.setDrawGridLines(false);
        rightAxis1.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis2 = mTempChart.getAxisLeft();
        leftAxis2.setDrawGridLines(false);
        leftAxis2.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        mTempXaxis = mTempChart.getXAxis();
        mTempXaxis.setPosition(XAxis.XAxisPosition.TOP);
        mTempXaxis.setAxisMinimum(0f);
        mTempXaxis.setGranularity(1f);
        mTempXaxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return tempXVal.get((int) value); // xVal is a string array
            }

        });

    }

    private void setWavesData(ArrayList<ForecastObject> Forecasts) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < Forecasts.size(); i++) {
            waveXVal.add(formatHour(Forecasts.get(i).getLocalTimeStamp()));
            values.add(new Entry(i, Forecasts.get(i).getAbsMaxBreakingHeight(), getResources().getDrawable(R.drawable.star)));
        }

        LineDataSet set1;

        if (mWavesChart.getData() != null &&
                mWavesChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mWavesChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mWavesChart.getData().notifyDataChanged();
            mWavesChart.notifyDataSetChanged();
            mWavesChart.invalidate();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

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

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mWavesChart.setData(data);
            mWavesChart.getData().notifyDataChanged();
            mWavesChart.notifyDataSetChanged();
            mWavesChart.invalidate();

        }
    }

    private void setWindData(ArrayList<ForecastObject> Forecasts){
        CombinedData data = new CombinedData();

        data.setData(generateWindLineData(Forecasts));
        data.setData(generateWindBarData(Forecasts));

        mWindXaxis.setAxisMaximum(data.getXMax() + 0.25f);

        mWindChart.setData(data);
        mWindChart.invalidate();

    }

    private void setTempData(ArrayList<ForecastObject> Forecasts){
        CombinedData data = new CombinedData();

        data.setData(generateTempLineData(Forecasts));
        data.setData(generateTempBarData(Forecasts));

        mTempXaxis.setAxisMaximum(data.getXMax() + 0.25f);

        mTempChart.setData(data);
        mTempChart.invalidate();

    }

    private LineData generateWindLineData(ArrayList<ForecastObject> Forecasts) {
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

         for (int i = 0; i < Forecasts.size(); i++) {
             windXVal.add(formatHour(Forecasts.get(i).getLocalTimeStamp()));
             entries.add(new Entry(i, Forecasts.get(i).getWindDirection()));
         }

         LineDataSet set = new LineDataSet(entries, "Wind Direction - °Degrees");
         set.setColor(Color.rgb(240, 238, 70));
         set.setLineWidth(2.5f);
         set.setCircleColor(Color.rgb(240, 238, 70));
         set.setCircleRadius(5f);
         set.setFillColor(Color.rgb(240, 238, 70));
         set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
         set.setDrawValues(true);
         set.setValueTextSize(10f);
         set.setValueTextColor(Color.rgb(240, 238, 70));

         set.setAxisDependency(YAxis.AxisDependency.LEFT);
         d.addDataSet(set);

        return d;
    }

    private BarData generateWindBarData(ArrayList<ForecastObject> Forecasts) {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();

        for (int i = 0; i < Forecasts.size(); i++) {
            entries1.add(new BarEntry(i, Forecasts.get(i).getWindSpeed()));
            }

        BarDataSet set1 = new BarDataSet(entries1, "Wind Speed - KM/h");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);


        return d;
    }

    private LineData generateTempLineData(ArrayList<ForecastObject> Forecasts) {
        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < Forecasts.size(); i++){
            tempXVal.add(formatHour(Forecasts.get(i).getLocalTimeStamp()));
            entries.add(new Entry(i , Forecasts.get(i).getTempChill()));

        }

        LineDataSet set = new LineDataSet(entries, "Feels Like - °Degrees");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private BarData generateTempBarData(ArrayList<ForecastObject> Forecasts) {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();

        for (int i = 0; i < Forecasts.size(); i++) {
            entries1.add(new BarEntry(i, Forecasts.get(i).getTemp()));
        }

        BarDataSet set1 = new BarDataSet(entries1, "Temperature - °C");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);


        return d;
    }


    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            ArrayList<ForecastObject> Forecasts = ((ArrayList<ForecastObject>) intent.getSerializableExtra("result"));
            Log.d("receiver", "Got message: ");

            new GetForecasts(instance).execute();

        }
    };

    public void updateData(List<ForecastObject> result){
        if (result != null) {
            mData = result;
            setDayPicker();

        }
    }


    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this.getContext()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private String formatTimeStamp(long timestamp){
        Date date = new java.util.Date(timestamp*1000L);
// the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM HH:mm");
// give a timezone reference for formatting (see comment at the bottom)
        String formattedDate = sdf.format(date);
        return formattedDate;

    }

    private String formatHour(long timestamp){
        Date date = new java.util.Date(timestamp*1000L);
// the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm");
// give a timezone reference for formatting (see comment at the bottom)
        String formattedDate = sdf.format(date);
        return formattedDate;

    }


    private String formatDay(long timestamp){
        Date date = new java.util.Date(timestamp*1000L);
// the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM");
// give a timezone reference for formatting (see comment at the bottom)
        String formattedDate = sdf.format(date);
        return formattedDate;

    }

    public List<String> getDays() {
        List<String> days = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            String day = formatDay(mData.get(i).getLocalTimeStamp());
            if (!(days.contains(day))) {
                days.add(day);
            }
        }
        return days;
    }

    private ArrayList<ForecastObject> getForecastsByDay(String day){
         ArrayList<ForecastObject> list = new ArrayList<>();
         for (int i=0; i<mData.size(); i++){
             if (formatDay(mData.get(i).getLocalTimeStamp()).equals(day))
                 list.add(mData.get(i));
         }
         return list;
    }

    private static class GetForecasts extends AsyncTask<Void, Void, List<ForecastObject>> {

        private Fragment parent;

        // only retain a weak reference to the activity
        GetForecasts(Fragment activity) {
            parent = activity;
        }

        @Override
        protected List<ForecastObject> doInBackground(Void... params) {

            // do some long running task...

            return ForecastDB.getInstance(MainApplication.getInstace().getApplicationContext()).daoAccess().getForecasts();
        }

        @Override
        protected void onPostExecute(List<ForecastObject> result) {

            ((ForecastFragment)parent).updateData(result);
                    return;
        }
    }
}

