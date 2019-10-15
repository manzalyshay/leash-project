package com.shaym.leash.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.facebook.login.LoginManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.shaym.leash.R;
import com.shaym.leash.models.Profile;
import com.shaym.leash.viewmodels.UsersViewModel;
import com.shaym.leash.data.utils.FireBasePostsHelper;
import com.shaym.leash.data.utils.FireBaseUsersHelper;
import com.shaym.leash.data.utils.onPictureUploadedListener;
import com.shaym.leash.ui.authentication.LoginActivity;
import com.shaym.leash.ui.forecast.ForecastFragment;
import com.shaym.leash.ui.forum.ForumFragment;
import com.shaym.leash.ui.gear.GearFragment;
import com.shaym.leash.ui.home.aroundme.AroundMeFragment;
import com.shaym.leash.ui.home.cameras.CamerasFragment;
import com.shaym.leash.ui.home.profile.ProfileFragment;
import com.shaym.leash.ui.utils.UIHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.shaym.leash.data.utils.CONSTANT.PROFILE_PICS;

@RuntimePermissions
public class HomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener, TabLayout.OnTabSelectedListener, PopupMenu.OnMenuItemClickListener, onPictureUploadedListener {

    private static final String TAG = "HomeActivity";
    public static final String FROM_UID_KEY = "FROM_UID_KEY";
    public static final int CHOOSE_PROFILE_PIC = 98;

    public static final String PUSH_RECEIVED = "PUSH_RECEIVED";
    private CamerasFragment mCamerasFragment;
    private AroundMeFragment mAroundMeFragment;
    private ProfileFragment mProfileFramgent;
    private ForecastFragment mForecastFragment;
    private ForumFragment mForumFragment;
    private GearFragment mGearFragment;

    private ViewPager vp;
    private Profile mUser;
    private AppBarLayout mAppBar;
    private TextView mProfileIconUnreadCounter;
    private ImageView mHeaderView;
    private ProgressBar mProfileProgressBarProfileFragment;
    private ImageView mProfilePictureProfileFragment;
    private ProgressBar mToolbarProfileProgressBar;
    private ImageView mToolbarProfilePicture;
    public static TabLayout mTablayout;
    private boolean uiSet;
    public static String mSelectedPostID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        // In Activity's onCreate() for instance
        setContentView(R.layout.activity_home);

        LocalBroadcastManager.getInstance(this).registerReceiver(mPushReceiver,
                new IntentFilter(PUSH_RECEIVED));

        handleIntent();

    }


    public boolean hasChatWith(String id){
        return mProfileFramgent.haschatWith(id);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        HomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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


    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        FireBaseUsersHelper.getInstance().updateUserStatus(false);

    }

    private void updateUI() {
        Log.d(TAG, "updateUI: " + mUser);
        UIHelper.getInstance().attachRoundPic(mUser.getAvatarurl(),mToolbarProfilePicture, mToolbarProfileProgressBar, 100, 100);
        UIHelper.getInstance().attachRoundPic(mUser.getAvatarurl(),mProfilePictureProfileFragment, mProfileProgressBarProfileFragment, 150, 150);

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
//                cf.show(getSupportFragmentManager(), "dialog_chat");

            }

        }
    }

    private void initUsersViewModel() {
        // Obtain a new or prior instance of HotStockViewModel from the
        // ViewModelProviders utility class.
        UsersViewModel mUserViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        LiveData<DataSnapshot> currentUserLiveData = mUserViewModel.getCurrentUserDataSnapshotLiveData();

        currentUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "initUsersViewModel: ");
                mUser = dataSnapshot.getValue(Profile.class);
                if (mUser != null){
                    updateUI();
                    FireBaseUsersHelper.getInstance().updateUserpushToken();
                    FireBaseUsersHelper.getInstance().updateUserStatus(true);
                }
            }
        });


    }


    private void initUI() {
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

        findViewById(R.id.location_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.back_icon_toolbar).setOnClickListener(this);
        findViewById(R.id.back_icon_toolbar).setClickable(false);
        mProfileIconUnreadCounter = findViewById(R.id.profile_icon_unread_counter);
        mTablayout = findViewById(R.id.tab_layout);
        Objects.requireNonNull(Objects.requireNonNull(mTablayout.getTabAt(0)).getIcon()).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Objects.requireNonNull(Objects.requireNonNull(mTablayout.getTabAt(1)).getIcon()).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Objects.requireNonNull(Objects.requireNonNull(mTablayout.getTabAt(2)).getIcon()).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Objects.requireNonNull(Objects.requireNonNull(mTablayout.getTabAt(3)).getIcon()).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        mTablayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.active_bottommenu));

        mTablayout.addOnTabSelectedListener(this);

        mHeaderView = findViewById(R.id.header);

        mProfilePictureProfileFragment = findViewById(R.id.profile_pic_profilefragment);
        mProfilePictureProfileFragment.setOnClickListener(this);

        mProfileProgressBarProfileFragment = findViewById(R.id.profilepic_progressbar_profilefragment);
        mToolbarProfilePicture = findViewById(R.id.profile_icon_pic);
        mToolbarProfilePicture.setOnClickListener(this);
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
        uiSet = true;

    }

    private void setupViewPager() {
        SectionPagerAdapter mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
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
        Log.d(TAG, "setupViewPager: ");
        vp.setOffscreenPageLimit(5);

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
        Log.d(TAG, "onPageSelected: " + position);
        mAroundMeFragment.stopLocationUpdates();
        switch (position){
            case 0:
                UIHelper.getInstance().updateToolBar(UIHelper.CAMERAS_SELECTED, this, mAppBar, mHeaderView );

                break;

            case 1:
                UIHelper.getInstance().updateToolBar(UIHelper.FORECAST_SELECTED, this, mAppBar, mHeaderView );


                break;


            case 2:
                UIHelper.getInstance().updateToolBar(UIHelper.FORUM_SELECTED, this, mAppBar, mHeaderView );
                if (mSelectedPostID != null){
                    mForumFragment.setCurrentPost(mSelectedPostID);
                    mSelectedPostID = null;
                }
                break;


            case 3:
                UIHelper.getInstance().updateToolBar(UIHelper.GEAR_SELECTED, this, mAppBar, mHeaderView );
                if (mSelectedPostID != null){
                    mGearFragment.setCurrentPost(mSelectedPostID);
                    mSelectedPostID = null;
                }
                break;

            case 4:
                UIHelper.getInstance().updateToolBar(UIHelper.PROFILE_SELECTED, this, mAppBar, mHeaderView );
                break;

            case 5:
                UIHelper.getInstance().updateToolBar(UIHelper.AROUNDME_SELECTED, this, mAppBar, mHeaderView );
                mAroundMeFragment.activateLocationUpdates();
                break;

            case 6:
                UIHelper.getInstance().updateToolBar(UIHelper.CHAT_SELECTED, this, mAppBar, mHeaderView );

                break;

        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");

        if (resultCode == Activity.RESULT_OK && requestCode == CHOOSE_PROFILE_PIC) {
            // File object will not be null for RESULT_OK
            File file = ImagePicker.Companion.getFile(data);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap selectedBitmap = BitmapFactory.decodeStream(fis);

            FireBasePostsHelper.getInstance().uploadImage(this, PROFILE_PICS, selectedBitmap, this);
        }

    }
    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    void showRationaleForExtStorage(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("This permission is needed in order to upload image")
                .setPositiveButton("OK", (dialog, which) -> request.proceed())
                .setNegativeButton("Cancel", (dialog, which) -> request.cancel())
                .show();
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void pickImage() {
        ImagePicker.Companion.with(this)
                .crop(1f, 1f)	    		//Crop Square image(Optional)
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start(CHOOSE_PROFILE_PIC);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_icon_toolbar:
                mTablayout.selectTab(mTablayout.getTabAt(0));
                vp.setCurrentItem(0);
                break;

            case R.id.profile_icon_pic:
                vp.setCurrentItem(4);
                break;

            case R.id.location_icon_toolbar:
                vp.setCurrentItem(5);
                break;

            case R.id.profile_pic_profilefragment:
                showPopup(findViewById(R.id.profile_pic_profilefragment));
                break;


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mPushReceiver);

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_profile_pic, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.choose_pic_action:
                HomeActivityPermissionsDispatcher.pickImageWithPermissionCheck(this);
                return true;
            case R.id.logout_action:
                FireBaseUsersHelper.getInstance().updateUserStatus(false);

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                firebaseAuth.addAuthStateListener(firebaseAuth1 -> {
                    if (firebaseAuth1.getCurrentUser() == null){
                        //Do anything here which needs to be done after signout is complete
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    }
                });
                firebaseAuth.signOut();
                LoginManager.getInstance().logOut();

                return true;
            default:
                return false;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d(TAG, "onTabSelected: " + tab.getPosition());
        Objects.requireNonNull(tab.getIcon()).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        vp.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Objects.requireNonNull(tab.getIcon()).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onPictureUploaded(String uploadPath) {
        Log.d(TAG, "onPictureUploaded: ");

        if (!mUser.getAvatarurl().isEmpty()) {
            ArrayList<String> pics = new ArrayList<>();
            pics.add(mUser.getAvatarurl());
            try {
                FireBasePostsHelper.getInstance().deleteImagesFromStorage(pics);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mUser.setAvatarurl(uploadPath);
        FireBaseUsersHelper.getInstance().saveUserByID(mUser.getUid(), mUser);
    }

    @Override
    public void onUploadFailed() {
        Log.d(TAG, "onUploadFailed: ");
    }
}

