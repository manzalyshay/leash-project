package com.shaym.leash.ui.home;

/**
 * Created by shaym on 2/14/18.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.shaym.leash.R;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.user.UsersViewModel;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.forecast.ForecastFragment;
import com.shaym.leash.ui.forum.ForumFragment;
import com.shaym.leash.ui.gear.GearFragment;
import com.shaym.leash.ui.home.aroundme.AroundMeFragment;
import com.shaym.leash.ui.home.cameras.CamerasFragment;
import com.shaym.leash.ui.home.profile.ProfileFragment;
import com.shaym.leash.ui.utils.FabClickedListener;
import com.shaym.leash.ui.utils.NavHelper;
import com.shaym.leash.ui.utils.UIHelper;

import static android.R.id.home;

public class HomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private static final String TAG = "HomeActivity";
    public static final String FROM_UID_KEY = "FROM_UID_KEY";
    public static final String PUSH_RECEIVED = "PUSH_RECEIVED";

    private static final int ACTIVITY_NUM = 0;
    public final static String REGISTER_KEY = "REGISTER";


    private CamerasFragment mCamerasFragment;
    private AroundMeFragment mAroundMeFragment;
    private ProfileFragment mProfileFramgent;
    private ForecastFragment mForecastFragment;
    private ForumFragment mForumFragment;
    private GearFragment mGearFragment;
    private FloatingActionButton mFab;
    private FabClickedListener mFabClickedListener;
    public ViewPager vp;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private NavHelper mNavHelper;
    private Profile mUser;
    private AppBarLayout mAppBar;
    private TextView mProfileIconUnreadCounter;
    private LinearLayout mCamerasMenuItem;
    private LinearLayout mForecastMenuItem;
    private ImageView mSepearatorMenuItem;
    private LinearLayout mForumMenuItem;
    private LinearLayout mGearMenuItem;
    private ImageView mHeaderView;
    private BottomAppBar mBottomAppBar;
    private ProgressBar mProfileProgressBar;
    private ImageView mProfilePicture;
    private ProgressBar mToolbarProfileProgressBar;
    private ImageView mToolbarProfilePicture;
    private boolean uiSet;
    private UsersViewModel mUserViewModel;
    private SectionPagerAdapter mSectionPagerAdapter;
    private boolean mFabPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        // In Activity's onCreate() for instance


        setContentView(R.layout.activity_home);

        LocalBroadcastManager.getInstance(this).registerReceiver(mPushReceiver,
                new IntentFilter(PUSH_RECEIVED));

    }

    private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(TAG, "onReceive: ");
           if (mUser != null){
               mUser.setUnreadcounter(mUser.getUnreadcounter() + 1);
               FireBaseUsersHelper.getInstance().saveUserByID(mUser.getUid(), mUser);
           }

        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        if (!uiSet) {
            initUI();
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        initUsersViewModel();
        handleIntent();
    }


    private void updateUI() {
        Log.d(TAG, "updateUI: ");
        FireBasePostsHelper.getInstance().attachRoundPic(mUser.getAvatarurl(),mToolbarProfilePicture, mToolbarProfileProgressBar, 100, 100);
        uiSet = true;

        if (mUser.getUnreadcounter() > 0){
            mProfileIconUnreadCounter.setVisibility(View.VISIBLE);
            mProfileIconUnreadCounter.setText(String.valueOf(mUser.getUnreadcounter()));
        }
        else {
            mProfileIconUnreadCounter.setVisibility(View.GONE);

        }
    }

    private void handleIntent() {
        Log.d(TAG, "handleIntent: ");
        Bundle b = getIntent().getExtras();
        if(b != null) {

            String mFromUID = b.getString(FROM_UID_KEY);

            //If came from chat push -> trigger conversation
            if (mFromUID !=null && !mFromUID.isEmpty()) {
                Log.d(TAG, "handleIntent: " + mFromUID);
//                ChatFragment cf = ChatFragment.newInstance(mFromUID);
//                cf.show(getSupportFragmentManager(), "activity_chat");

            }

        }
    }

    private void initUsersViewModel() {
        // Obtain a new or prior instance of HotStockViewModel from the
        // ViewModelProviders utility class.
        mUserViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        LiveData<DataSnapshot> currentUserLiveData = mUserViewModel.getCurrentUserDataSnapshotLiveData();

        currentUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "initUsersViewModel: ");
                mUser = dataSnapshot.getValue(Profile.class);
                if (mUser != null){
                    updateUI();
                    FireBaseUsersHelper.getInstance().updateUserpushToken();
                }
            }
        });
    }


    private void initUI() {
//        new KeyboardUtil(this, findViewById(R.id.container_home));

        mAppBar = findViewById(R.id.app_bar);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
        if (params.getBehavior() == null){

            AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
            params.setBehavior(behavior);
    }

        mAppBar.setLayoutParams(params);



        mFab = findViewById(R.id.fab_new_post);
        mFab.setOnClickListener(this);
        findViewById(R.id.location_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.profile_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.menu_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.back_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.back_icon_toolbar).setClickable(false);
        mProfileIconUnreadCounter = findViewById(R.id.profile_icon_unread_counter);
        mCamerasMenuItem = findViewById(R.id.bottom_nav_cameras);
        mCamerasMenuItem.setOnClickListener(this);
        mForecastMenuItem = findViewById(R.id.bottom_nav_forecast);
        mForecastMenuItem.setOnClickListener(this);
        mSepearatorMenuItem = findViewById(R.id.bottom_nav_sepearator);
        mForumMenuItem = findViewById(R.id.bottom_nav_forum);
        mForumMenuItem.setOnClickListener(this);
        mGearMenuItem = findViewById(R.id.bottom_nav_gear);
        mGearMenuItem.setOnClickListener(this);
        mBottomAppBar = findViewById(R.id.bottom_bar);


        UIHelper.getInstance().setMenuItemSelected(findViewById(R.id.bottom_nav_cameras_selectedindicator), this);
        mHeaderView = findViewById(R.id.header);

        mProfilePicture = findViewById(R.id.profile_pic);
        mProfileProgressBar = findViewById(R.id.profilepic_progressbar);
        mToolbarProfilePicture = findViewById(R.id.profile_icon_toolbar);
        mToolbarProfileProgressBar = findViewById(R.id.profile_icon_progressbar);

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

        View rootView = findViewById(R.id.general_layout);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int heightDiff = rootView.getRootView().getHeight() - (r.bottom - r.top);

                if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                    //ok now we know the keyboard is up...
                    mBottomAppBar.setVisibility(View.GONE);
                    mFab.setVisibility(View.GONE);

                }else{
                    //ok now we know the keyboard is down...
                    mBottomAppBar.setVisibility(View.VISIBLE);
                    if (mFabPage)
                    mFab.setVisibility(View.VISIBLE);

                }
            }
        });

        uiSet = true;

    }

    private void setupViewPager() {
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mSectionPagerAdapter.AddFragment(mCamerasFragment);
//        sectionPagerAdapter.AddFragment(mVisualizeFragment);
        mSectionPagerAdapter.AddFragment(mForecastFragment);
        mSectionPagerAdapter.AddFragment(mForumFragment);
        mSectionPagerAdapter.AddFragment(mGearFragment);
        mSectionPagerAdapter.AddFragment(mProfileFramgent);
        mSectionPagerAdapter.AddFragment(mAroundMeFragment);

        vp = findViewById(R.id.container_home);
        vp.setAdapter(mSectionPagerAdapter);
        vp.setClipToPadding(true);
        vp.setPadding(5,0,5,0);
        vp.addOnPageChangeListener(this);

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to leave this app?")
                .setPositiveButton("Yes", (dialog, which) -> HomeActivity.super.onBackPressed())
                .setNegativeButton("No", null)
                .show();
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "onPageSelected: ");
        switch (position){
            case 0:
                UIHelper.getInstance().updateToolBar(UIHelper.CAMERAS_SELECTED, this, mAppBar, mHeaderView );
                UIHelper.getInstance().setMenuItemSelected(findViewById(R.id.bottom_nav_cameras_selectedindicator), this);
                mSepearatorMenuItem.setVisibility(View.GONE);
                mFab.hide();
                mFabPage = false;
                break;

            case 1:
                UIHelper.getInstance().updateToolBar(UIHelper.FORECAST_SELECTED, this, mAppBar, mHeaderView );
                UIHelper.getInstance().setMenuItemSelected(findViewById(R.id.bottom_nav_forecast_selectedindicator), this);
                mSepearatorMenuItem.setVisibility(View.GONE);
                mFab.hide();
                mFabPage = false;

//                mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(FORECAST_ITEM_ID));
                break;


            case 2:
                UIHelper.getInstance().updateToolBar(UIHelper.FORUM_SELECTED, this, mAppBar, mHeaderView );
                UIHelper.getInstance().setMenuItemSelected(findViewById(R.id.bottom_nav_forum_selectedindicator), this);
                mSepearatorMenuItem.setVisibility(View.VISIBLE);
                mFabClickedListener = mForumFragment;
                mFab.show();
                mFabPage = true;

//                mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(AROUNDME_FRAGMENT_ITEM_ID));
                break;


            case 3:
                UIHelper.getInstance().updateToolBar(UIHelper.GEAR_SELECTED, this, mAppBar, mHeaderView );
                UIHelper.getInstance().setMenuItemSelected(findViewById(R.id.bottom_nav_gear_selectedindicator), this);
                mSepearatorMenuItem.setVisibility(View.VISIBLE);
                mFabClickedListener = mGearFragment;
                mFabPage = true;

                mFab.show();
//                mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(GEAR_ITEM_ID));
                break;

            case 4:
                UIHelper.getInstance().updateToolBar(UIHelper.PROFILE_SELECTED, this, mAppBar, mHeaderView );
                mSepearatorMenuItem.setVisibility(View.GONE);
                mFab.hide();
                mFabPage = false;

//                mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(PROFILE_FRAGMENT_ITEM_ID));
                break;

            case 5:
                UIHelper.getInstance().updateToolBar(UIHelper.AROUNDME_SELECTED, this, mAppBar, mHeaderView );
                mSepearatorMenuItem.setVisibility(View.GONE);
                mFab.hide();
                mFabPage = false;

//                mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(PROFILE_FRAGMENT_ITEM_ID));
                break;

            case 6:
                UIHelper.getInstance().updateToolBar(UIHelper.CHAT_SELECTED, this, mAppBar, mHeaderView );
                mSepearatorMenuItem.setVisibility(View.GONE);
                mFab.hide();
                mFabPage = false;

//                mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(PROFILE_FRAGMENT_ITEM_ID));
                break;

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
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.fab_new_post:
                mFabClickedListener.onFabClicked();
                break;

            case R.id.bottom_nav_cameras:
            case R.id.back_icon_toolbar:

                vp.setCurrentItem(0);
                break;

            case R.id.bottom_nav_forecast:
                vp.setCurrentItem(1);
                break;

            case R.id.bottom_nav_forum:
                vp.setCurrentItem(2);

                break;

            case R.id.bottom_nav_gear:
                vp.setCurrentItem(3);
                break;

            case R.id.profile_icon_toolbar:
                vp.setCurrentItem(4);
                break;

            case R.id.location_icon_toolbar:
                vp.setCurrentItem(5);
                break;

            case R.id.menu_icon_toolbar:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPushReceiver);

    }
}




