package com.shaym.leash.ui.home;

/**
 * Created by shaym on 2/14/18.
 */

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.shaym.leash.R;
import com.shaym.leash.ui.home.cameras.CamerasFragment;
import com.shaym.leash.ui.utils.BottomNavigationViewHelper;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private BottomNavigationViewHelper mBottomNavHelper;
    private static final int ACTIVITY_NUM = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBottomNavHelper = new BottomNavigationViewHelper(this, ACTIVITY_NUM);
        setupViewPager();
    }

    private void setupViewPager(){
        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        sectionPagerAdapter.AddFragment(new CamerasFragment());
        sectionPagerAdapter.AddFragment(new VisualizeFragment());
        sectionPagerAdapter.AddFragment(new ProfileFramgent());
        sectionPagerAdapter.AddFragment(new AroundMeFragment());
        ViewPager vp = (ViewPager) findViewById(R.id.container);
        vp.setAdapter(sectionPagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_cameras);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_visualize);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_profile);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_aroundme);

    }




}
