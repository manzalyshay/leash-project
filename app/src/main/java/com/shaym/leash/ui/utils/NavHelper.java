package com.shaym.leash.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.authentication.LoginActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import static com.shaym.leash.ui.forecast.ForecastFragment.CALIFORNIA_FRAGMENT_ITEM_ID;
import static com.shaym.leash.ui.forecast.ForecastFragment.ISRAEL_FRAGMENT_ITEM_ID;

public class NavHelper implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public final static int CAMERAS_FRAGMENT_ITEM_ID = 0101;
    public final static int CAMERAS_ISRAEL_ITEM_ID = 02201;
    public final static int CAMERAS_CALIFORNIA_ITEM_ID = 02202;
    public final static int PROFILE_FRAGMENT_ITEM_ID = 0103;
    public final static int AROUNDME_FRAGMENT_ITEM_ID = 0104;
    public final static int HOME_ITEM_ID = 01;
    public final static int FORECAST_ITEM_ID = 02;
    public final static int FORUM_ITEM_ID = 03;
    public final static int GEAR_ITEM_ID = 04;

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


    private ViewPager vp;
    public NavHelper(NavigationView navigationView, ViewPager vp,   int ActivityNum) {

        this.mNavigationView = navigationView;
        this.mContext = navigationView.getContext();
        this.vp = vp;

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
//                initForumActivityMenu();

                break;

            case 3:
                initGearActivityMenu();
                break;
        }

        initGeneralMenu(activitynum);

        mNavigationView.invalidate();
    }

    private void initGeneralMenu(int activitynum) {
        Menu submenu2 = navMenu.addSubMenu(mContext.getString(R.string.app_name));

        MenuItem item5 = submenu2.add(3, HOME_ITEM_ID, 0, mContext.getString(R.string.home_menu_item));
        item5.setIcon(R.drawable.ic_cameras); // add icon with drawable resource

        MenuItem item6 = submenu2.add(3,FORECAST_ITEM_ID, 1, mContext.getString(R.string.forecast_menu_item));
        item6.setIcon(R.drawable.ic_forecast); // add icon with drawable resource

        MenuItem item7 = submenu2.add(3, FORUM_ITEM_ID, 2, mContext.getString(R.string.forum_menu_item));
        item7.setIcon(R.drawable.ic_forum); // add icon with drawable resource

        MenuItem item8 = submenu2.add(3, GEAR_ITEM_ID, 3, mContext.getString(R.string.gear_menu_item));
        item8.setIcon(R.drawable.ic_equip); // add icon with drawable resource

        switch (activitynum){
            case 0:
                item5.setChecked(true);
                break;
            case 1:
                item6.setChecked(true);
                break;
            case 2:
                item7.setChecked(true);
                break;
            case 3:
                item8.setChecked(true);
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
//
//        submenu.add(1, NEWGEAR_FRAGMENT_ITEM_ID, 0, mContext.getString(R.string.newgear_menu_title));
//        //item.setIcon(R.drawable.ic_israel); // add icon with drawable resource
//
//        submenu.add(1, USEDGEAR_FRAGMENT_ITEM_ID, 1, mContext.getString(R.string.usedgear_menu_title));
//        //item2.setIcon(R.drawable.ic_california); // add icon with drawable resource


    }



    private void initForecaseActivityMenu() {
        Menu submenu = navMenu.addSubMenu(mContext.getString(R.string.forecast_menu_item));

        submenu.add(1, ISRAEL_FRAGMENT_ITEM_ID, 0, mContext.getString(R.string.cameras_title_israel));
        //item.setIcon(R.drawable.ic_israel); // add icon with drawable resource

        submenu.add(1,CALIFORNIA_FRAGMENT_ITEM_ID, 1, mContext.getString(R.string.cameras_title_california));
        //item2.setIcon(R.drawable.ic_california); // add icon with drawable resource

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


    private void updateUI() {
        Log.d(TAG, "updateUI: ");

        if (!mUser.getDisplayname().isEmpty()) {
            mNavUsername.setText(mUser.getDisplayname());
        }

        if (!mUser.getAvatarURL().isEmpty()) {
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


//                        case VISUALIZE_FRAGMENT_ITEM_ID:
//                            vp.setCurrentItem(1);
//                            break;

            case PROFILE_FRAGMENT_ITEM_ID:
                vp.setCurrentItem(4);

                break;

            case AROUNDME_FRAGMENT_ITEM_ID:
                vp.setCurrentItem(5);
                break;

            case ISRAEL_FRAGMENT_ITEM_ID:
                Toast.makeText(mNavigationView.getContext(), "Israel Forecast", Toast.LENGTH_LONG).show();
                break;

            case CALIFORNIA_FRAGMENT_ITEM_ID:
                Toast.makeText(mNavigationView.getContext(), "California Forecast", Toast.LENGTH_LONG).show();
                break;


        }
        // close drawer when item is tapped
        ((DrawerLayout)mNavigationView.getParent()).closeDrawers();

        return true;
    }

    public void setCurrentUser(Profile currentUser) {
        mUser = currentUser;
        updateUI();
    }
}