package com.shaym.leash.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.shaym.leash.R;

public class UIHelper {
    public final static String CAMERAS_SELECTED = "CAMERAS_SELECTED";
    public final static String PROFILE_SELECTED = "PROFILE_SELECTED";
    public final static String AROUNDME_SELECTED = "AROUNDME_SELECTED";
    public final static String CHAT_SELECTED = "CHAT_SELECTED";

    public final static String FORECAST_SELECTED = "FORECAST_SELECTED";
    public final static String FORUM_SELECTED = "FORUM_SELECTED";
    public final static String GEAR_SELECTED = "GEAR_SELECTED";
    private static UIHelper instance = new UIHelper();

    private UIHelper() {
    }

    public static UIHelper getInstance(){
        return instance;
    }

    public void updateToolBar(String currentState, Activity context, AppBarLayout mAppBar, ImageView mHeaderView){

        switch (currentState) {
            case CAMERAS_SELECTED:
                mAppBar.setExpanded(true);
                context.findViewById(R.id.location_icon_toolbar).setVisibility(View.VISIBLE);
                context.findViewById(R.id.profile_layout_toolbar).setVisibility(View.VISIBLE);
                context.findViewById(R.id.back_icon_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.back_icon_toolbar).setClickable(false);

                context.findViewById(R.id.profile_pic_profilefragment).setVisibility(View.GONE);

                break;

            case FORECAST_SELECTED:
                mAppBar.setExpanded(false);
                context.findViewById(R.id.profile_pic_profilefragment).setVisibility(View.GONE);

                break;

            case FORUM_SELECTED:
                mAppBar.setExpanded(false);
                context.findViewById(R.id.profile_pic_profilefragment).setVisibility(View.GONE);

                break;

            case GEAR_SELECTED:
                mAppBar.setExpanded(false);
                context.findViewById(R.id.profile_pic_profilefragment).setVisibility(View.GONE);

                break;

            case PROFILE_SELECTED:
                mAppBar.setExpanded(true);
                context.findViewById(R.id.location_icon_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.profile_layout_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.back_icon_toolbar).setVisibility(View.VISIBLE);
                context.findViewById(R.id.back_icon_toolbar).setClickable(true);

                context.findViewById(R.id.profile_pic_profilefragment).setVisibility(View.VISIBLE);

                break;

            case AROUNDME_SELECTED:
                mAppBar.setExpanded(false);
                context.findViewById(R.id.location_icon_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.profile_layout_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.back_icon_toolbar).setVisibility(View.VISIBLE);
                context.findViewById(R.id.back_icon_toolbar).setClickable(true);
                context.findViewById(R.id.profile_pic_profilefragment).setVisibility(View.GONE);

                break;

            case CHAT_SELECTED:
                mAppBar.setExpanded(false);
                context.findViewById(R.id.location_icon_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.profile_layout_toolbar).setVisibility(View.GONE);
                context.findViewById(R.id.back_icon_toolbar).setVisibility(View.VISIBLE);
                context.findViewById(R.id.back_icon_toolbar).setClickable(true);
                context.findViewById(R.id.profile_pic_profilefragment).setVisibility(View.GONE);

                break;


        }
    }


    public void addTab(TabLayout tabLayout, String title) {
        tabLayout.addTab(tabLayout.newTab().setText(title));
    }






}
