package com.shaym.leash.ui.forecast;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.shaym.leash.R;
import com.shaym.leash.logic.forecast.ForecastHelper;
import com.shaym.leash.logic.forecast.ForecastObject;

import java.util.ArrayList;
import java.util.List;


class Utils {
    private static ArrayList<String> Xval = new ArrayList<>();
    private static LineChart mChart;

    @SuppressLint("SetTextI18n")
    static void setupItem(final View view, List<ForecastObject> forecastObjects, HorizontalPagerAdapter.ForecastSetting forecastSetting) {
        TextView txt = view.findViewById(R.id.txt_item);
        txt.setText("" + forecastSetting.mName);

        chartSetup(view, forecastSetting);
        setChartData(forecastObjects, view, forecastSetting);
    }


    private static void chartSetup(View v, HorizontalPagerAdapter.ForecastSetting forecastSetting) {
        mChart = v.findViewById(R.id.chart_item);
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        // to use for it
        MyMarkerView mv = new MyMarkerView(v.getContext(), R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart);
        mChart.getDescription().setText(forecastSetting.mDescriptionLabel);

        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        Typeface tf = Typeface.createFromAsset(v.getContext().getAssets(), "OpenSans-Regular.ttf");

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
        leftAxis.setAxisMaximum(forecastSetting.mYLength);
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


    private static void setChartData(List<ForecastObject> Forecasts, View v, HorizontalPagerAdapter.ForecastSetting forecastSetting) {

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < Forecasts.size(); i++) {
            Xval.add(ForecastHelper.getInstance().formatHour(Forecasts.get(i).getLocalTimeStamp()));


            if (forecastSetting.mName.equals(v.getContext().getString(R.string.waves_name_forecast)))
            {
                values.add(new Entry(i, Forecasts.get(i).getAbsMaxBreakingHeight(), v.getContext().getResources().getDrawable(R.drawable.star)));
            }
            else if (forecastSetting.mName.equals(v.getContext().getString(R.string.wind_name_forecast))) {
                    values.add(new Entry(i, Forecasts.get(i).getWindSpeed(), v.getContext().getResources().getDrawable(R.drawable.star)));
            }
            else {
                    values.add(new Entry(i, Forecasts.get(i).getTemp(), v.getContext().getResources().getDrawable(R.drawable.star)));
            }
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, forecastSetting.mName);

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
                Drawable drawable = ContextCompat.getDrawable(v.getContext(), forecastSetting.mGradientID);
                set1.setFillDrawable(drawable);
            }
            else {
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