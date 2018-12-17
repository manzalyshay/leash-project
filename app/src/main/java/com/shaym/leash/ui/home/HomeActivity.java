package com.shaym.leash.ui.home;

/**
 * Created by shaym on 2/14/18.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.shaym.leash.R;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.home.aroundme.AroundMeFragment;
import com.shaym.leash.ui.home.cameras.CamerasFragment;
import com.shaym.leash.ui.home.chat.ChatFragment;
import com.shaym.leash.ui.home.fragments.VisualizeFragment;
import com.shaym.leash.ui.home.profile.ProfileFragment;
import com.shaym.leash.ui.utils.BottomNavigationViewHelper;
import com.shaym.leash.ui.utils.NavHelper;

import java.util.Objects;

import static android.R.id.home;
import static com.shaym.leash.logic.utils.FireBaseUsersHelper.BROADCAST_USER;
import static com.shaym.leash.ui.authentication.LoginActivity.PROFILE_PIC_KEY;
import static com.shaym.leash.ui.authentication.RegisterActivity.REGISTER_KEY;

public class HomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final String TAG = "HomeActivity";
    public static final String FROM_UID_KEY = "FROM_UID_KEY";

    private BottomNavigationViewHelper mBottomNavHelper;
    private static final int ACTIVITY_NUM = 0;
    private FusedLocationProviderClient mFusedLocationClient;
    private CamerasFragment mCamerasFragment;
    private AroundMeFragment mAroundMeFragment;
    private ProfileFragment mProfileFramgent;
    private VisualizeFragment mVisualizeFragment;
    public final static int CAMERAS_FRAGMENT_ITEM_ID = 0101;
    public final static int CAMERAS_ISRAEL_ITEM_ID = 02201;
    public final static int CAMERAS_CALIFORNIA_ITEM_ID = 02202;
    public final static int VISUALIZE_FRAGMENT_ITEM_ID = 0102;
    public final static int PROFILE_FRAGMENT_ITEM_ID = 0103;
    public final static int AROUNDME_FRAGMENT_ITEM_ID = 0104;
    public final static int HOME_ITEM_ID = 01;
    public final static int FORECAST_ITEM_ID = 02;
    public final static int FORUM_ITEM_ID = 03;
    public final static int GEAR_ITEM_ID = 04;
    public ViewPager vp;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private NavHelper mNavHelper;
    private String mProfilePicURL;
    private String mRegisterKey;

    private Profile mUser;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");

        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        mProfileFramgent.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        handleIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mUserReceiver,
                new IntentFilter(BROADCAST_USER));
        FireBaseUsersHelper.getInstance().loadCurrentUserProfile();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUserReceiver);
    }


    // Get extra data included in the Intent
    BroadcastReceiver mUserReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(TAG + "receiver", "Got message: ");
            Bundle args = intent.getBundleExtra("DATA");

            mUser = (Profile) args.getSerializable("USEROBJ");
            if (mUser != null) {
                updateUI();
            }
        }
    };

    private void updateUI() {
        if (mNavHelper == null) {
            mNavHelper = new NavHelper(mNavigationView, vp, mBottomNavHelper, ACTIVITY_NUM);
            mNavHelper.setCurrentUser(mUser);
        }
        mNavHelper.setCurrentUser(mUser);
    }



    private void handleIntent() {

        Bundle b = getIntent().getExtras();

        if(b != null) {

            String mFromUID = b.getString(FROM_UID_KEY);

            if (mFromUID !=null && !mFromUID.isEmpty()) {
                Log.d(TAG, "handleIntent: " + mFromUID);
                ChatFragment cf = ChatFragment.newInstance(mFromUID);
                cf.show(getSupportFragmentManager(), "fragment_chat");
            }

            mProfilePicURL = b.getString(PROFILE_PIC_KEY);
            mRegisterKey = b.getString(REGISTER_KEY);
        }


        if (mProfilePicURL == null){
            mProfilePicURL = "";
        }

        if ((mRegisterKey != null && mRegisterKey.equals("REGISTER")) || !mProfilePicURL.isEmpty()){
            FireBaseUsersHelper.getInstance().createUserInDB(mProfilePicURL);
        }


    }

    private void initUI() {
        mDrawerLayout = findViewById(R.id.drawer_layout_home);
        mNavigationView = findViewById(R.id.nav_view);

        mBottomNavHelper = new BottomNavigationViewHelper(HomeActivity.this, ACTIVITY_NUM);

        mAroundMeFragment = new AroundMeFragment();
        mProfileFramgent = new ProfileFragment();
//        mVisualizeFragment = new VisualizeFragment();
        mCamerasFragment = new CamerasFragment();

        setupViewPager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

    }




    private void setupViewPager() {
        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        sectionPagerAdapter.AddFragment(mCamerasFragment);
//        sectionPagerAdapter.AddFragment(mVisualizeFragment);
        sectionPagerAdapter.AddFragment(mProfileFramgent);
        sectionPagerAdapter.AddFragment(mAroundMeFragment);

        vp = findViewById(R.id.container_home);
        vp.setAdapter(sectionPagerAdapter);
        vp.setOffscreenPageLimit(1);
        vp.addOnPageChangeListener(this);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_cameras);
//        tabLayout.getTabAt(1).setIcon(R.drawable.ic_visualize);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_profile);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_aroundme);
    }







    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mNavHelper != null) {
            switch (position){
                case 0:
                    mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(CAMERAS_FRAGMENT_ITEM_ID));
                    break;
                case 1:
                    mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(PROFILE_FRAGMENT_ITEM_ID));
                    break;
                case 2:
                    mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(AROUNDME_FRAGMENT_ITEM_ID));
                    break;

            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




