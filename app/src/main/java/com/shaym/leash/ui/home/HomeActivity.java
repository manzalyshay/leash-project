package com.shaym.leash.ui.home;

/**
 * Created by shaym on 2/14/18.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.shaym.leash.R;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.logic.utils.UsersHelperListener;
import com.shaym.leash.ui.forecast.ForecastFragment;
import com.shaym.leash.ui.forum.ForumFragment;
import com.shaym.leash.ui.gear.GearFragment;
import com.shaym.leash.ui.home.aroundme.AroundMeFragment;
import com.shaym.leash.ui.home.cameras.CamerasFragment;
import com.shaym.leash.ui.home.chat.ChatFragment;
import com.shaym.leash.ui.home.profile.ProfileFragment;
import com.shaym.leash.ui.utils.NavHelper;

import static android.R.id.home;
import static com.shaym.leash.logic.utils.CONSTANT.USER_BUNDLE;
import static com.shaym.leash.logic.utils.CONSTANT.USER_OBJ;
import static com.shaym.leash.logic.utils.FireBaseUsersHelper.BROADCAST_USER;
import static com.shaym.leash.ui.authentication.LoginActivity.PROFILE_PIC_KEY;
import static com.shaym.leash.ui.utils.NavHelper.AROUNDME_FRAGMENT_ITEM_ID;
import static com.shaym.leash.ui.utils.NavHelper.CAMERAS_FRAGMENT_ITEM_ID;
import static com.shaym.leash.ui.utils.NavHelper.PROFILE_FRAGMENT_ITEM_ID;

public class HomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, BubbleNavigationChangeListener, View.OnClickListener, UsersHelperListener {

    private static final String TAG = "HomeActivity";
    public static final String FROM_UID_KEY = "FROM_UID_KEY";

    private static final int ACTIVITY_NUM = 0;
    public final static String REGISTER_KEY = "REGISTER";

    private CamerasFragment mCamerasFragment;
    private AroundMeFragment mAroundMeFragment;
    private ProfileFragment mProfileFramgent;
    private ForecastFragment mForecastFragment;
    private ForumFragment mForumFragment;
    private GearFragment mGearFragment;

    public ViewPager vp;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private NavHelper mNavHelper;
    private Profile mUser;
    private BubbleNavigationLinearView mBottomNav;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        // In Activity's onCreate() for instance
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        setContentView(R.layout.activity_home);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");

        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        mProfileFramgent.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onStart() {
        super.onStart();
        initUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent();
        FireBaseUsersHelper.getInstance().getCurrentUserProfile(this);
    }




    private void updateUI() {
        if (mNavHelper == null) {
            mNavHelper = new NavHelper(mNavigationView, vp, ACTIVITY_NUM);
            mNavHelper.setCurrentUser(mUser);
        }
        mNavHelper.setCurrentUser(mUser);
    }



    private void handleIntent() {
        Bundle b = getIntent().getExtras();

        if(b != null) {

            String mFromUID = b.getString(FROM_UID_KEY);

            //If came from chat push -> trigger conversation
            if (mFromUID !=null && !mFromUID.isEmpty()) {
                Log.d(TAG, "handleIntent: " + mFromUID);
                ChatFragment cf = ChatFragment.newInstance(mFromUID);
                cf.show(getSupportFragmentManager(), "fragment_chat");
            }
            else {

                String mProfilePicURL = b.getString(PROFILE_PIC_KEY);
                String mRegisterKey = b.getString(REGISTER_KEY);

                if (mProfilePicURL == null){
                    mProfilePicURL = "";
                }

                // If registered by APP/FB
                if ((mRegisterKey != null && mRegisterKey.equals(REGISTER_KEY)) || !mProfilePicURL.isEmpty()){
                    FireBaseUsersHelper.getInstance().createUserInDB(mProfilePicURL);
                }
            }
        }





    }

    private void initUI() {
        mDrawerLayout = findViewById(R.id.drawer_layout_home);
        mNavigationView = findViewById(R.id.nav_view);
        mBottomNav = findViewById(R.id.bottom_navigation_constraint);
        mBottomNav.setNavigationChangeListener(this);
        findViewById(R.id.location_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.profile_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.menu_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.back_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.profile_back_icon).setOnClickListener(this);
        findViewById(R.id.profile_location_icon).setOnClickListener(this);
        findViewById(R.id.profile_menu_icon).setOnClickListener(this);

        mAroundMeFragment = new AroundMeFragment();
        mProfileFramgent = new ProfileFragment();
        mCamerasFragment = new CamerasFragment();
        mForecastFragment = new ForecastFragment();
        mForumFragment = new ForumFragment();
        mGearFragment = new GearFragment();
        setupViewPager();

        Toolbar toolbar = findViewById(R.id.toolbar_home);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setHomeButtonEnabled(false); // disable the button

        actionbar.setDisplayHomeAsUpEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayShowTitleEnabled(false);


    }

    private void setupViewPager() {
        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        sectionPagerAdapter.AddFragment(mCamerasFragment);
//        sectionPagerAdapter.AddFragment(mVisualizeFragment);
        sectionPagerAdapter.AddFragment(mForecastFragment);
        sectionPagerAdapter.AddFragment(mForumFragment);
        sectionPagerAdapter.AddFragment(mGearFragment);
        sectionPagerAdapter.AddFragment(mProfileFramgent);
        sectionPagerAdapter.AddFragment(mAroundMeFragment);

        vp = findViewById(R.id.container_home);
        vp.setAdapter(sectionPagerAdapter);
        vp.setOffscreenPageLimit(0);
        vp.addOnPageChangeListener(this);

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


    @Override
    public void onNavigationChanged(View view, int position) {
        switch (position) {
            case 0:
                changeHeaderSize(3.0f, 6.0f);
                findViewById(R.id.layout_top_profile).setVisibility(View.GONE);
                findViewById(R.id.layout_top_home).setVisibility(View.VISIBLE);

                break;
            default:
                changeHeaderSize(1.0f, 8.0f);
                break;
        }

        vp.setCurrentItem(position);


    }



    private void changeHeaderSize(float headerSize, float bodySize) {
        RelativeLayout rl = findViewById(R.id.relLayout1);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                rl.getLayoutParams();
        params.weight = headerSize;
        rl.setLayoutParams(params);

        RelativeLayout rl2 = findViewById(R.id.relLayout2);
        LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams)
                rl2.getLayoutParams();
        params2.weight = bodySize;
        rl2.setLayoutParams(params2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.profile_icon_toolbar:
                mBottomNav.setCurrentActiveItem(0);
                vp.setCurrentItem(4);
                changeHeaderSize(4.0f, 5.0f);
                findViewById(R.id.layout_top_home).setVisibility(View.GONE);
                findViewById(R.id.layout_top_profile).setVisibility(View.VISIBLE);
                break;

            case R.id.location_icon_toolbar:
                mBottomNav.setCurrentActiveItem(0);
                vp.setCurrentItem(5);
                changeHeaderSize(1.0f, 8.0f);
                findViewById(R.id.location_icon_toolbar).setVisibility(View.GONE);
                findViewById(R.id.back_icon_toolbar).setVisibility(View.VISIBLE);
                break;


            case R.id.profile_location_icon:
                vp.setCurrentItem(5);
                changeHeaderSize(1.0f, 8.0f);
                findViewById(R.id.layout_top_profile).setVisibility(View.GONE);
                findViewById(R.id.layout_top_home).setVisibility(View.VISIBLE);
                findViewById(R.id.location_icon_toolbar).setVisibility(View.GONE);
                findViewById(R.id.back_icon_toolbar).setVisibility(View.VISIBLE);
                break;

            case R.id.profile_menu_icon:
            case R.id.menu_icon_toolbar:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;


            case R.id.back_icon_toolbar:
                findViewById(R.id.location_icon_toolbar).setVisibility(View.VISIBLE);
                findViewById(R.id.profile_icon_toolbar).setVisibility(View.VISIBLE);
                findViewById(R.id.back_icon_toolbar).setVisibility(View.GONE);

                vp.setCurrentItem(0);
                changeHeaderSize(3.0f, 6.0f);
                break;

                case R.id.profile_back_icon:
                    findViewById(R.id.layout_top_home).setVisibility(View.VISIBLE);
                    findViewById(R.id.layout_top_profile).setVisibility(View.GONE);
                    findViewById(R.id.back_icon_toolbar).setVisibility(View.GONE);
                    findViewById(R.id.profile_icon_toolbar).setVisibility(View.VISIBLE);
                    findViewById(R.id.location_icon_toolbar).setVisibility(View.VISIBLE);

                    changeHeaderSize(3.0f, 6.0f);
                    vp.setCurrentItem(0);
                    break;

        }

    }

    @Override
    public void onCurrentProfileLoaded(Profile mCurrentUser) {
        mUser = mCurrentUser;
        updateUI();
    }
}




