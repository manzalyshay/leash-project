package com.shaym.leash.ui.utils;

        import android.content.Context;
        import android.content.Intent;
        import android.net.Uri;
        import android.support.annotation.NonNull;
        import android.support.design.widget.NavigationView;
        import android.support.v4.content.LocalBroadcastManager;
        import android.support.v4.view.ViewPager;
        import android.support.v4.widget.DrawerLayout;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.facebook.login.LoginManager;
        import com.google.firebase.auth.FirebaseAuth;
        import com.shaym.leash.MainApplication;
        import com.shaym.leash.R;
        import com.shaym.leash.logic.aroundme.CircleTransform;
        import com.shaym.leash.logic.user.Profile;
        import com.shaym.leash.logic.utils.FireBaseUsersHelper;
        import com.shaym.leash.ui.authentication.LoginActivity;
        import com.squareup.picasso.Callback;
        import com.squareup.picasso.NetworkPolicy;
        import com.squareup.picasso.Picasso;

        import java.util.Objects;

        import static com.shaym.leash.ui.forecast.ForecastActivity.CALIFORNIA_FRAGMENT_ITEM_ID;
        import static com.shaym.leash.ui.forecast.ForecastActivity.ISRAEL_FRAGMENT_ITEM_ID;
        import static com.shaym.leash.ui.forum.ForumActivity.GENERAL_FRAGMENT_ITEM_ID;
        import static com.shaym.leash.ui.forum.ForumActivity.SPOTS_FRAGMENT_ITEM_ID;
        import static com.shaym.leash.ui.forum.ForumActivity.TRIPS_FRAGMENT_ITEM_ID;
        import static com.shaym.leash.ui.gear.GearActivity.NEWGEAR_FRAGMENT_ITEM_ID;
        import static com.shaym.leash.ui.gear.GearActivity.USEDGEAR_FRAGMENT_ITEM_ID;
        import static com.shaym.leash.ui.home.HomeActivity.AROUNDME_FRAGMENT_ITEM_ID;
        import static com.shaym.leash.ui.home.HomeActivity.CAMERAS_CALIFORNIA_ITEM_ID;
        import static com.shaym.leash.ui.home.HomeActivity.CAMERAS_FRAGMENT_ITEM_ID;
        import static com.shaym.leash.ui.home.HomeActivity.CAMERAS_ISRAEL_ITEM_ID;
        import static com.shaym.leash.ui.home.HomeActivity.FORECAST_ITEM_ID;
        import static com.shaym.leash.ui.home.HomeActivity.FORUM_ITEM_ID;
        import static com.shaym.leash.ui.home.HomeActivity.GEAR_ITEM_ID;
        import static com.shaym.leash.ui.home.HomeActivity.HOME_ITEM_ID;
        import static com.shaym.leash.ui.home.HomeActivity.PROFILE_FRAGMENT_ITEM_ID;

public class NavHelper implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private ImageView mNavProfilePic;
    private ImageView mNavSettingsBtn;

    private TextView mNavUsername;
    private NavigationView mNavigationView;
    private Profile mUser;
    private static final String TAG = "NavHelper";

    private Context mContext;
    private ProgressBar mProfilePicProgressBar;
    private Menu navMenu;
    private final static int LOGOUT_ITEM_ID = 991;

    private BottomNavigationViewHelper bottomNavigationViewHelper;

    private ViewPager vp;
    public NavHelper(NavigationView navigationView, ViewPager vp, BottomNavigationViewHelper bottomNavigationViewHelper,  int ActivityNum) {

        this.mNavigationView = navigationView;
        this.mContext = navigationView.getContext();
        this.vp = vp;
        this.bottomNavigationViewHelper = bottomNavigationViewHelper;

        navMenu = navigationView.getMenu();
        initUI(navigationView.getHeaderView(0));

        initMenus(ActivityNum);

        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void initMenus(int activitynum) {
        initSettingsMenu();

        switch (activitynum){
            case 0:
                initHomeActivityMenu();
                break;

            case 1:
                initForecaseActivityMenu();

                break;

            case 2:
                initForumActivityMenu();

                break;

            case 3:
                initGearActivityMenu();

                break;
        }

    }

    private void initSettingsMenu() {
        mNavSettingsBtn.setOnClickListener(this);

        Menu SettingsSubmenu = navMenu.addSubMenu(mNavigationView.getContext().getString(R.string.action_settings));
        SettingsSubmenu.add(0, LOGOUT_ITEM_ID, 0, mNavigationView.getContext().getString(R.string.log_out_text));
        mNavigationView.getMenu().getItem(0).setVisible(false);
    }

    private void initGearActivityMenu() {
        Menu submenu = navMenu.addSubMenu(mContext.getString(R.string.forum_menu_item));

        submenu.add(1, NEWGEAR_FRAGMENT_ITEM_ID, 0, mContext.getString(R.string.newgear_menu_title));
        //item.setIcon(R.drawable.ic_israel); // add icon with drawable resource

        submenu.add(1, USEDGEAR_FRAGMENT_ITEM_ID, 1, mContext.getString(R.string.usedgear_menu_title));
        //item2.setIcon(R.drawable.ic_california); // add icon with drawable resource

        Menu submenu2 = navMenu.addSubMenu(mContext.getString(R.string.app_name));

        MenuItem item5 = submenu2.add(2, HOME_ITEM_ID, 0, mContext.getString(R.string.home_menu_item));
        item5.setIcon(R.drawable.ic_cameras); // add icon with drawable resource

        MenuItem item6 = submenu2.add(2,FORECAST_ITEM_ID, 1, mContext.getString(R.string.forecast_menu_item));
        item6.setIcon(R.drawable.ic_forecast); // add icon with drawable resource

        MenuItem item7 = submenu2.add(2, FORUM_ITEM_ID, 2, mContext.getString(R.string.forum_menu_item));
        item7.setIcon(R.drawable.ic_forum); // add icon with drawable resource

        MenuItem item8 = submenu2.add(2, GEAR_ITEM_ID, 3, mContext.getString(R.string.gear_menu_item));
        item8.setIcon(R.drawable.ic_equip); // add icon with drawable resource

        mNavigationView.invalidate();
    }

    private void initForumActivityMenu() {
        Menu submenu = navMenu.addSubMenu(mContext.getString(R.string.forum_menu_item));

        submenu.add(1, GENERAL_FRAGMENT_ITEM_ID, 0, mContext.getString(R.string.general_menu_title));
        //item.setIcon(R.drawable.ic_israel); // add icon with drawable resource

        submenu.add(1, SPOTS_FRAGMENT_ITEM_ID, 1, mContext.getString(R.string.spots_menu_title));
        //item2.setIcon(R.drawable.ic_california); // add icon with drawable resource

        submenu.add(1, TRIPS_FRAGMENT_ITEM_ID, 1, mContext.getString(R.string.trips_menu_title));
        //item2.setIcon(R.drawable.ic_california); // add icon with drawable resource


        Menu submenu2 = navMenu.addSubMenu(mContext.getString(R.string.app_name));

        MenuItem item5 = submenu2.add(2, HOME_ITEM_ID, 0, mContext.getString(R.string.home_menu_item));
        item5.setIcon(R.drawable.ic_cameras); // add icon with drawable resource

        MenuItem item6 = submenu2.add(2,FORECAST_ITEM_ID, 1, mContext.getString(R.string.forecast_menu_item));
        item6.setIcon(R.drawable.ic_forecast); // add icon with drawable resource

        MenuItem item7 = submenu2.add(2, FORUM_ITEM_ID, 2, mContext.getString(R.string.forum_menu_item));
        item7.setIcon(R.drawable.ic_forum); // add icon with drawable resource

        MenuItem item8 = submenu2.add(2, GEAR_ITEM_ID, 3, mContext.getString(R.string.gear_menu_item));
        item8.setIcon(R.drawable.ic_equip); // add icon with drawable resource

        mNavigationView.invalidate();

    }

    private void initForecaseActivityMenu() {
        Menu submenu = navMenu.addSubMenu(mContext.getString(R.string.forecast_menu_item));

        submenu.add(1, ISRAEL_FRAGMENT_ITEM_ID, 0, mContext.getString(R.string.cameras_title_israel));
        //item.setIcon(R.drawable.ic_israel); // add icon with drawable resource

        submenu.add(1,CALIFORNIA_FRAGMENT_ITEM_ID, 1, mContext.getString(R.string.cameras_title_california));
        //item2.setIcon(R.drawable.ic_california); // add icon with drawable resource


        Menu submenu2 = navMenu.addSubMenu(mContext.getString(R.string.app_name));

        MenuItem item5 = submenu2.add(2, HOME_ITEM_ID, 0, mContext.getString(R.string.home_menu_item));
        item5.setIcon(R.drawable.ic_cameras); // add icon with drawable resource

        MenuItem item6 = submenu2.add(2,FORECAST_ITEM_ID, 1, mContext.getString(R.string.forecast_menu_item));
        item6.setIcon(R.drawable.ic_forecast); // add icon with drawable resource

        MenuItem item7 = submenu2.add(2, FORUM_ITEM_ID, 2, mContext.getString(R.string.forum_menu_item));
        item7.setIcon(R.drawable.ic_forum); // add icon with drawable resource

        MenuItem item8 = submenu2.add(2, GEAR_ITEM_ID, 3, mContext.getString(R.string.gear_menu_item));
        item8.setIcon(R.drawable.ic_equip); // add icon with drawable resource

        mNavigationView.invalidate();
    }

    private void initHomeActivityMenu() {
        Menu submenu = navMenu.addSubMenu(mContext.getString(R.string.home_menu_item));

        MenuItem item = submenu.add(1, CAMERAS_FRAGMENT_ITEM_ID, 0, mContext.getString(R.string.cameras_menu_title));
        item.setIcon(R.drawable.ic_cameras); // add icon with drawable resource
        item.setChecked(true);

//        MenuItem item2 = submenu.add(0, VISUALIZE_FRAGMENT_ITEM_ID, 1, "Visualize");
//        item2.setIcon(R.drawable.ic_visualize); // add icon with drawable resource

        MenuItem item3 = submenu.add(1, PROFILE_FRAGMENT_ITEM_ID, 3, mContext.getString(R.string.profile_menu_title));
        item3.setIcon(R.drawable.ic_profile); // add icon with drawable resource

        MenuItem item4 = submenu.add(1, AROUNDME_FRAGMENT_ITEM_ID, 4, mContext.getString(R.string.around_me_menu_title));
        item4.setIcon(R.drawable.ic_aroundme); // add icon with drawable resource


        Menu camerassubmenu = navMenu.addSubMenu(mContext.getString(R.string.cameras_menu_title)) ;

        camerassubmenu.add(2, CAMERAS_ISRAEL_ITEM_ID, 0, mContext.getString(R.string.cameras_title_israel));

        camerassubmenu.add(2, CAMERAS_CALIFORNIA_ITEM_ID, 1, mContext.getString(R.string.cameras_title_california));

        Menu submenu2 = navMenu.addSubMenu(mContext.getString(R.string.app_name));

        MenuItem item5 = submenu2.add(3, HOME_ITEM_ID, 0, mContext.getString(R.string.home_menu_item));
        item5.setIcon(R.drawable.ic_cameras); // add icon with drawable resource

        MenuItem item6 = submenu2.add(3,FORECAST_ITEM_ID, 1, mContext.getString(R.string.forecast_menu_item));
        item6.setIcon(R.drawable.ic_forecast); // add icon with drawable resource

        MenuItem item7 = submenu2.add(3, FORUM_ITEM_ID, 2, mContext.getString(R.string.forum_menu_item));
        item7.setIcon(R.drawable.ic_forum); // add icon with drawable resource

        MenuItem item8 = submenu2.add(3, GEAR_ITEM_ID, 3, mContext.getString(R.string.gear_menu_item));
        item8.setIcon(R.drawable.ic_equip); // add icon with drawable resource

        mNavigationView.invalidate();
    }

    private void initUI(View drawerheader) {
        mNavProfilePic = drawerheader.findViewById(R.id.nav_userprofilepic);
        mNavSettingsBtn = drawerheader.findViewById(R.id.nav_settings_btn);
        mNavUsername = drawerheader.findViewById(R.id.nav_username);
        mProfilePicProgressBar = drawerheader.findViewById(R.id.propicprogbar);
    }

    public String getUid() {

        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    private void initNavProfile(){
        if (mUser != null ) {
            LoadUI();
        }

    }

    private void LoadUI() {
        Log.d(TAG, "LoadUI: " + mNavigationView.getParent().toString());
        if (mUser.getDisplayname() != null) {
            mNavUsername.setText(mUser.getDisplayname());
        }

        if (mUser.getAvatarURL()!= null && !mUser.getAvatarURL().isEmpty()) {
            if (mUser.getAvatarURL().charAt(0) == 'p') {
                FireBaseUsersHelper.getInstance().getStorageReference().child(mUser.getAvatarURL()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(200, 200).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(mNavProfilePic, new Callback() {
                    @Override
                    public void onSuccess() {
                        mProfilePicProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(uri).resize(200, 200).centerCrop().transform(new CircleTransform()).into(mNavProfilePic, new Callback() {
                            @Override
                            public void onSuccess() {
                                mProfilePicProgressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed

                                mProfilePicProgressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    }

                }));
            }
            else {
                Picasso.get().load(Uri.parse(mUser.getAvatarURL())).resize(200, 200).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(mNavProfilePic, new Callback() {
                    @Override
                    public void onSuccess() {
                        mProfilePicProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(Uri.parse(mUser.getAvatarURL())).resize(200, 200).centerCrop().transform(new CircleTransform()).into(mNavProfilePic, new Callback() {
                            @Override
                            public void onSuccess() {
                                mProfilePicProgressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed

                                mProfilePicProgressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    }

                });
            }
        }
        else {

            mProfilePicProgressBar.setVisibility(View.INVISIBLE);

        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.nav_settings_btn:
                if (mNavSettingsBtn.getRotation() == 0) {
                    mNavSettingsBtn.animate().rotation(180).start();
                    mNavigationView.getMenu().getItem(0).setVisible(true);
                    mNavigationView.getMenu().getItem(1).setVisible(false);
                    mNavigationView.getMenu().getItem(2).setVisible(false);

                    mNavigationView.invalidate();


                }
                else{
                    mNavSettingsBtn.animate().rotation(0).start();
                    mNavigationView.getMenu().getItem(0).setVisible(false);
                    mNavigationView.getMenu().getItem(1).setVisible(true);
                    mNavigationView.getMenu().getItem(2).setVisible(true);

                    mNavigationView.invalidate();
                }

                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        for (int i=0; i<mNavigationView.getMenu().size(); i++){
            mNavigationView.getMenu().getItem(i).setChecked(false);
            if (mNavigationView.getMenu().getItem(i).getSubMenu() != null){
                Log.d(TAG, "NavHelper: " + mNavigationView.getMenu().getItem(i).getSubMenu().toString());
                for (int j=0; j<mNavigationView.getMenu().getItem(i).getSubMenu().size(); j++) {
                    mNavigationView.getMenu().getItem(i).getSubMenu().getItem(j).setChecked(false)   ;                         }
            }
        }
        // set item as selected to persist highlight
        if (item.getItemId() == CAMERAS_ISRAEL_ITEM_ID || item.getItemId() == CAMERAS_CALIFORNIA_ITEM_ID) {
            mNavigationView.getMenu().findItem(CAMERAS_FRAGMENT_ITEM_ID).setChecked(true);
        }
        else {
            item.setChecked(true);
        }
        switch (item.getItemId()){

            case LOGOUT_ITEM_ID:
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent i = new Intent(mNavigationView.getContext(), LoginActivity.class);
                mNavigationView.getContext().startActivity(i);
                break;
            case CAMERAS_FRAGMENT_ITEM_ID:
                vp.setCurrentItem(0);
                break;

            case CAMERAS_ISRAEL_ITEM_ID:
                vp.setCurrentItem(0);

                //Change Cameras
                Log.d("sender", "Broadcasting cameras change");
                Intent intent = new Intent(mNavigationView.getContext().getString(R.string.broadcast_cameras_changed));
                // You can also include some extra data.
                intent.putExtra("val", 0);
                LocalBroadcastManager.getInstance(MainApplication.getInstace().getApplicationContext()).sendBroadcast(intent);

                break;

            case CAMERAS_CALIFORNIA_ITEM_ID:
                //Change Cameras
                vp.setCurrentItem(0);

                Log.d("sender", "Broadcasting cameras change");
                Intent intent1 = new Intent(mNavigationView.getContext().getString(R.string.broadcast_cameras_changed));
                // You can also include some extra data.
                intent1.putExtra("val", 1);
                LocalBroadcastManager.getInstance(MainApplication.getInstace().getApplicationContext()).sendBroadcast(intent1);

                break;

//                        case VISUALIZE_FRAGMENT_ITEM_ID:
//                            vp.setCurrentItem(1);
//                            break;

            case PROFILE_FRAGMENT_ITEM_ID:
                vp.setCurrentItem(1);

                break;

            case AROUNDME_FRAGMENT_ITEM_ID:
                vp.setCurrentItem(2);
                break;

            case ISRAEL_FRAGMENT_ITEM_ID:
                Toast.makeText(mNavigationView.getContext(), "Israel Forecast", Toast.LENGTH_LONG).show();
                break;

            case CALIFORNIA_FRAGMENT_ITEM_ID:
                Toast.makeText(mNavigationView.getContext(), "California Forecast", Toast.LENGTH_LONG).show();
                break;

            case HOME_ITEM_ID:
                bottomNavigationViewHelper.setPage(0);
                break;

            case FORECAST_ITEM_ID:
                bottomNavigationViewHelper.setPage(1);
                break;

            case FORUM_ITEM_ID:
                bottomNavigationViewHelper.setPage(2);
                break;

            case GEAR_ITEM_ID:
                bottomNavigationViewHelper.setPage(3);
                break;

            case GENERAL_FRAGMENT_ITEM_ID:
                vp.setCurrentItem(0);
                break;

            case SPOTS_FRAGMENT_ITEM_ID:
                vp.setCurrentItem(1);
                break;

            case TRIPS_FRAGMENT_ITEM_ID:
                vp.setCurrentItem(2);
                break;

            case NEWGEAR_FRAGMENT_ITEM_ID:
                vp.setCurrentItem(0);
                break;

            case USEDGEAR_FRAGMENT_ITEM_ID:
                vp.setCurrentItem(1);
                break;

        }
        // close drawer when item is tapped
        ((DrawerLayout)mNavigationView.getParent()).closeDrawers();

        return true;
    }

    public void setCurrentUser(Profile mUser) {
        this.mUser = mUser;
        LoadUI();
    }
}