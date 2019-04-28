package com.shaym.leash.ui.forecast;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shaym.leash.R;
import com.shaym.leash.logic.forecast.ForecastObject;

import java.util.List;

import static com.shaym.leash.ui.forecast.Utils.setupItem;


/**
 * Created by GIGAMOLE on 7/27/16.
 */
public class HorizontalPagerAdapter extends PagerAdapter {

    private  List<ForecastObject> forecastObjects;
    private LayoutInflater mLayoutInflater;
    private final ForecastSetting[] forecastSettings;

    HorizontalPagerAdapter(final Context context, final List<ForecastObject> forecastObjects) {
        mLayoutInflater = LayoutInflater.from(context);
        this.forecastObjects = forecastObjects;
         forecastSettings = new ForecastSetting[]{
                new ForecastSetting(context.getString(R.string.waves_name_forecast), 2f, "Waves Week1", "Waves Height - CM", R.drawable.fade_chart),
                new ForecastSetting(context.getString(R.string.wind_name_forecast), 40f, "Wind Week1", "Wind Speed - KM/H", R.drawable.fade_chart),
                new ForecastSetting(context.getString(R.string.tempeartue_name_forecast), 50f, "Temperature Week1", "Temperature - °C", R.drawable.fade_chart)
        };
    }

    @Override
    public int getCount() {
        return forecastSettings.length;
    }

    @Override
    public int getItemPosition(@NonNull final Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final View view;

        view = mLayoutInflater.inflate(R.layout.viewpager_item, container, false);
        setupItem(view, forecastObjects, forecastSettings[position]);

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull final View view, @NonNull final Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull final ViewGroup container, final int position, @NonNull final Object object) {
        container.removeView((View) object);
    }

     class ForecastSetting{

        String mName;
        float mYLength;
        String mDataSetName;
        String mDescriptionLabel;
        int mGradientID;

        ForecastSetting(String name, float ylength, String dataSetname, String descLabel, int gradID){
            this.mName = name;
            this.mYLength = ylength;
            this.mDataSetName = dataSetname;
            this.mDescriptionLabel = descLabel;
            this.mGradientID = gradID;
        }
}

}
