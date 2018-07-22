package com.shaym.leash.ui.home;

/**
 * Created by shaym on 2/14/18.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shaym.leash.MainApplication;
import com.shaym.leash.R;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.ui.home.cameras.CamerasFragment;
import com.shaym.leash.ui.home.aroundme.AroundMeFragment;
import com.shaym.leash.ui.home.profile.ProfileFragment;
import com.shaym.leash.ui.home.fragments.VisualizeFragment;
import com.shaym.leash.ui.utils.BottomNavigationViewHelper;

import static com.shaym.leash.logic.CONSTANT.USERS_TABLE;
import static com.shaym.leash.logic.CONSTANT.USER_LAT;
import static com.shaym.leash.logic.CONSTANT.USER_LNG;
import static com.shaym.leash.ui.home.aroundme.AroundMeFragment.MY_PERMISSIONS_REQUEST_LOCATION;

public class HomeActivity extends AppCompatActivity
{

    private static final String TAG = "HomeActivity";
    private BottomNavigationViewHelper mBottomNavHelper;
    private static final int ACTIVITY_NUM = 0;
    private FirebaseUser mCurrentUser;
    private FusedLocationProviderClient mFusedLocationClient;
    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    private CamerasFragment mCamerasFragment;
    private AroundMeFragment mAroundMeFragment;
    private ProfileFragment mProfileFramgent;
    private VisualizeFragment mVisualizeFragment;
    public ViewPager vp;

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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        createUserInDB();
        mAroundMeFragment = new AroundMeFragment();
        mProfileFramgent = new ProfileFragment();
        mVisualizeFragment = new VisualizeFragment();
        mCamerasFragment = new CamerasFragment();

        mBottomNavHelper = new BottomNavigationViewHelper(this, ACTIVITY_NUM);
        setupViewPager();
        checkLocationPermission();
     }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainApplication.getInstace().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(HomeActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    private void createUserInDB() {
        DatabaseReference userNameRef = rootRef.child(USERS_TABLE).child(mCurrentUser.getUid());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange: User does not exist in DB");
                    Profile userProfile = new Profile(mCurrentUser.getEmail(),mCurrentUser.getEmail().substring(0, mCurrentUser.getEmail().indexOf("@")), 0.0, 0.0,
                            0, 0, 0, "");
                    rootRef.child(USERS_TABLE).child(mCurrentUser.getUid()).setValue(userProfile);

                }
                Log.d(TAG, "onDataChange: User already exists in DB");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.toString());
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);
    }


    private void setupViewPager() {
        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        sectionPagerAdapter.AddFragment(mCamerasFragment);
        sectionPagerAdapter.AddFragment(mVisualizeFragment);
        sectionPagerAdapter.AddFragment(mProfileFramgent);
        sectionPagerAdapter.AddFragment(mAroundMeFragment);
        vp = (ViewPager) findViewById(R.id.container_home);
        vp.setAdapter(sectionPagerAdapter);
        vp.setOffscreenPageLimit(4);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(vp);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_cameras);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_visualize);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_profile);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_aroundme);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserLocation();
        mAroundMeFragment.refresh();
    }

    private void updateUserLocation() {
        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.d(TAG, "onSuccess: Location updated");
                                rootRef.child(USERS_TABLE).child(mCurrentUser.getUid()).child(USER_LAT).setValue(location.getLatitude());
                                rootRef.child(USERS_TABLE).child(mCurrentUser.getUid()).child(USER_LNG).setValue(location.getLongitude());

                            }
                        }
                    });

        }

     else {

    Log.d(TAG, "onRequestPermissionsResult: Permission Denied");

}

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(HomeActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        // Got last known location. In some rare situations this can be null.
                                        if (location != null) {
                                            Log.d(TAG, "onSuccess: Location updated");
                                            rootRef.child(USERS_TABLE).child(mCurrentUser.getUid()).child(USER_LAT).setValue(location.getLatitude());
                                            rootRef.child(USERS_TABLE).child(mCurrentUser.getUid()).child(USER_LNG).setValue(location.getLongitude());

                                        }
                                    }
                                });

                    }

                } else {

                    Log.d(TAG, "onRequestPermissionsResult: Permission Denied");

                }
                return;
            }

        }
    }





}




