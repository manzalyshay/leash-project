package com.shaym.leash.ui.home;

/**
 * Created by shaym on 2/14/18.
 */


import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
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
import com.shaym.leash.ui.utils.FabClickedListener;
import com.shaym.leash.ui.utils.NavHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static android.R.id.home;
import static com.shaym.leash.ui.authentication.LoginActivity.PROFILE_PIC_KEY;
import static com.shaym.leash.ui.gear.NewGearPostDialog.GEAR_PICK_IMAGE_REQUEST;
import static com.shaym.leash.ui.utils.NavHelper.AROUNDME_FRAGMENT_ITEM_ID;
import static com.shaym.leash.ui.utils.NavHelper.CAMERAS_FRAGMENT_ITEM_ID;
import static com.shaym.leash.ui.utils.NavHelper.FORECAST_ITEM_ID;
import static com.shaym.leash.ui.utils.NavHelper.GEAR_ITEM_ID;
import static com.shaym.leash.ui.utils.NavHelper.PROFILE_FRAGMENT_ITEM_ID;

public class HomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, UsersHelperListener {

    private static final String TAG = "HomeActivity";
    public static final String FROM_UID_KEY = "FROM_UID_KEY";

    private static final int ACTIVITY_NUM = 0;
    public final static String REGISTER_KEY = "REGISTER";
    public final static String CAMERAS_SELECTED = "CAMERAS_SELECTED";
    public final static String PROFILE_SELECTED = "PROFILE_SELECTED";
    public final static String AROUNDME_SELECTED = "AROUNDME_SELECTED";
    public final static String FORECAST_SELECTED = "FORECAST_SELECTED";
    public final static String FORUM_SELECTED = "FORUM_SELECTED";
    public final static String GEAR_SELECTED = "GEAR_SELECTED";

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
    private StorageReference storageReference;
    private boolean uiSet;
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

    private void attachProfilePic(String url, ImageView imageView, ProgressBar progressBar){
        if (!url.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            if (url.charAt(0) == 'g') {
                storageReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(100, 100).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(uri).resize(100, 100).centerCrop().transform(new CircleTransform()).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed

                                progressBar.setVisibility(View.GONE);

                            }
                        });

                    }

                }));
            } else {
                Picasso.get().load(Uri.parse(url)).resize(100, 100).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(Uri.parse(url)).resize(100, 100).centerCrop().transform(new CircleTransform()).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);

                            }
                        });

                    }

                });
            }
        }
        else {
            progressBar.setVisibility(View.GONE);

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: ");
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
        if (!uiSet){
            handleIntent();
            FireBaseUsersHelper.getInstance().getCurrentUserProfile(this);
        }

    }




    private void updateUI() {
        Log.d(TAG, "updateUI: ");
        mNavHelper.setCurrentUser(mUser);
        attachProfilePic(mUser.getAvatarURL(),mToolbarProfilePicture, mToolbarProfileProgressBar);
        uiSet = true;
    }



    private void handleIntent() {
        Log.d(TAG, "handleIntent: ");
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
        storageReference = FirebaseStorage.getInstance().getReference();

        mAppBar = findViewById(R.id.app_bar);
        mFab = findViewById(R.id.fab_new_post);
        mFab.setOnClickListener(this);
        findViewById(R.id.location_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.profile_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.menu_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.back_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.back_icon_toolbar).setClickable(false);

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
        setMenuItemSelected(findViewById(R.id.bottom_nav_cameras_selectedindicator));
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

        mNavHelper = new NavHelper(mNavigationView, vp, ACTIVITY_NUM);

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
        vp.setClipToPadding(true);
        vp.setPadding(5,0,5,0);
        vp.setOffscreenPageLimit(6);
        vp.addOnPageChangeListener(this);

    }

    private void updateToolBar(String currentState){
        switch (currentState) {
            case CAMERAS_SELECTED:
                mAppBar.setExpanded(true);
                findViewById(R.id.location_icon_toolbar).setVisibility(View.VISIBLE);
                findViewById(R.id.profile_layout_toolbar).setVisibility(View.VISIBLE);
                findViewById(R.id.back_icon_toolbar).setVisibility(View.GONE);
                findViewById(R.id.back_icon_toolbar).setClickable(false);

                findViewById(R.id.profile_pic).setVisibility(View.GONE);
                mHeaderView.setImageDrawable(getDrawable(R.drawable.wave_new));
                break;

            case FORECAST_SELECTED:
                mAppBar.setExpanded(false);
                findViewById(R.id.profile_pic).setVisibility(View.GONE);

                break;

            case FORUM_SELECTED:
                mAppBar.setExpanded(false);
                findViewById(R.id.profile_pic).setVisibility(View.GONE);

                break;

            case GEAR_SELECTED:
                mAppBar.setExpanded(false);
                findViewById(R.id.profile_pic).setVisibility(View.GONE);

                break;

            case PROFILE_SELECTED:
                mAppBar.setExpanded(true);
                findViewById(R.id.location_icon_toolbar).setVisibility(View.GONE);
                findViewById(R.id.profile_layout_toolbar).setVisibility(View.GONE);
                findViewById(R.id.back_icon_toolbar).setVisibility(View.VISIBLE);
                findViewById(R.id.back_icon_toolbar).setClickable(true);

                findViewById(R.id.profile_pic).setVisibility(View.VISIBLE);
                mHeaderView.setImageDrawable(getDrawable(R.drawable.surfer_profile));
                attachProfilePic(mUser.getAvatarURL(), mProfilePicture, mProfileProgressBar);

                break;

            case AROUNDME_SELECTED:
                mAppBar.setExpanded(false);
                findViewById(R.id.location_icon_toolbar).setVisibility(View.GONE);
                findViewById(R.id.profile_layout_toolbar).setVisibility(View.GONE);
                findViewById(R.id.back_icon_toolbar).setVisibility(View.VISIBLE);
                findViewById(R.id.back_icon_toolbar).setClickable(true);
                findViewById(R.id.profile_pic).setVisibility(View.GONE);

                break;


        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "onPageSelected: ");
        switch (position){
            case 0:
                updateToolBar(CAMERAS_SELECTED);
                setMenuItemSelected(findViewById(R.id.bottom_nav_cameras_selectedindicator));
                mSepearatorMenuItem.setVisibility(View.GONE);
                mFab.hide();
//                mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(CAMERAS_FRAGMENT_ITEM_ID));
                break;

            case 1:
                updateToolBar(FORECAST_SELECTED);
                setMenuItemSelected(findViewById(R.id.bottom_nav_forecast_selectedindicator));
                mSepearatorMenuItem.setVisibility(View.GONE);
                mFab.hide();
//                mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(FORECAST_ITEM_ID));
                break;


            case 2:
                updateToolBar(FORUM_SELECTED);
                setMenuItemSelected(findViewById(R.id.bottom_nav_forum_selectedindicator));
                mSepearatorMenuItem.setVisibility(View.VISIBLE);
                mFabClickedListener = mForumFragment;
                mFab.show();
//                mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(AROUNDME_FRAGMENT_ITEM_ID));
                break;


            case 3:
                updateToolBar(GEAR_SELECTED);
                setMenuItemSelected(findViewById(R.id.bottom_nav_gear_selectedindicator));
                mSepearatorMenuItem.setVisibility(View.VISIBLE);
                mFabClickedListener = mGearFragment;
                mFab.show();
//                mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(GEAR_ITEM_ID));
                break;

            case 4:
                updateToolBar(PROFILE_SELECTED);
                mSepearatorMenuItem.setVisibility(View.GONE);
                mFab.hide();
//                mNavHelper.onNavigationItemSelected(mNavigationView.getMenu().findItem(PROFILE_FRAGMENT_ITEM_ID));
                break;

            case 5:
                updateToolBar(AROUNDME_SELECTED);
                mSepearatorMenuItem.setVisibility(View.GONE);
                mFab.hide();
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
    public void onCurrentProfileLoaded(Profile mCurrentUser) {
        mUser = mCurrentUser;
        updateUI();
    }

    private void setMenuItemSelected(View menuItemSelectedView){

        findViewById(R.id.bottom_nav_cameras_selectedindicator).setVisibility(View.GONE);
        findViewById(R.id.bottom_nav_forecast_selectedindicator).setVisibility(View.GONE);
        findViewById(R.id.bottom_nav_forum_selectedindicator).setVisibility(View.GONE);
        findViewById(R.id.bottom_nav_gear_selectedindicator).setVisibility(View.GONE);


        menuItemSelectedView.setVisibility(View.VISIBLE);
    }


}




