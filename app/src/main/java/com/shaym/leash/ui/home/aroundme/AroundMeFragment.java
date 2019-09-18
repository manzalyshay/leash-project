package com.shaym.leash.ui.home.aroundme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.shaym.leash.R;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.user.UsersViewModel;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.utils.UIHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import static com.shaym.leash.logic.utils.CONSTANT.ROLE_USER;


/**
 * Created by shaym on 2/17/18.
 */

@RuntimePermissions
public class AroundMeFragment extends Fragment implements OnMapReadyCallback, OnMyLocationButtonClickListener,
        OnMarkerClickListener {
    private GoogleMap mGoogleMap;
    private static final String TAG = "AroundMeFragment";
    private Set<PoiTarget> poiTargets = new HashSet<>();
    private HashMap<Profile, LatLng> markerlocation = new HashMap<>();
    private List<Marker> markerList = new ArrayList<>();
    private static final float COORDINATE_OFFSET = 0.00002f; // You can change this value according to your need
    private int MAX_NUMBER_OF_MARKERS;
    private SupportMapFragment mapFragment;
    private List<Profile> mAllUsers = new ArrayList<>();
    private Profile mUser;
    private LocationManager locationManager;
    private LocationListener mLocationListener;

    public AroundMeFragment (){}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_aroundme, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);

        AroundMeFragmentPermissionsDispatcher.initLocationProvidersWithPermissionCheck(this);


    }

    @SuppressLint("MissingPermission")
    @NeedsPermission({ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void initLocationProviders() {
        locationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: " + location);
                // Called when a new location is found by the network location provider.
                if (location != null) {
                    if (mUser!= null && mUser.getRole().equals(ROLE_USER))
                        FireBaseUsersHelper.getInstance().updateUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        mapFragment.getMapAsync(AroundMeFragment.this);

    }


   public void activateLocationUpdates(){
        AroundMeFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);

   }

    public void stopLocationUpdates() {
        locationManager.removeUpdates(mLocationListener);
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission({ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    void startLocationUpdates() {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

    }


    @OnShowRationale({ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    void showRationaleForFineLocation(PermissionRequest request) {
        showRationaleDialog(R.string.text_location_permission, request);
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(getContext())
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    request.proceed();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    request.cancel();
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AroundMeFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @OnPermissionDenied({ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    void onLocationDenied() {
        Toast.makeText(getContext(), "Denied", Toast.LENGTH_SHORT).show();
    }


    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }

    private LatLng coordinateForMarker(LatLng loc, Profile user) {

        double[] location = new double[2];
        LatLng loctocheck;
        LatLng loctocheck2;


        for (int i = 0; i <= MAX_NUMBER_OF_MARKERS; i++) {

            if (mapAlreadyHasMarkerForLocation(loc, user)) {

                // If i = 0 then below if condition is same as upper one. Hence, no need to execute below if condition.
                if (i == 0)
                    continue;

                loctocheck = new LatLng(loc.latitude + i
                        * COORDINATE_OFFSET, loc.longitude + i * COORDINATE_OFFSET);

                if (mapAlreadyHasMarkerForLocation(loctocheck, user)) {
                    loctocheck2 = new LatLng(loc.latitude - i
                            * COORDINATE_OFFSET, loc.longitude + -i * COORDINATE_OFFSET);
                    if (!mapAlreadyHasMarkerForLocation(loctocheck2, user)) {
                        location[0] = loctocheck2.latitude;
                        location[1] = loctocheck2.longitude;
                        break;
                    }
                } else {
                    location[0] = loctocheck.latitude;
                    location[1] = loctocheck.longitude;
                }
            } else {
                location[0] = loc.latitude;
                location[1] = loc.longitude;
                break;
            }


        }

        return new LatLng(location[0], location[1]);
    }

    private void updateUsersMarkers() {
        //to fetch all the users of firebase Auth app
        if (mGoogleMap != null && !mAllUsers.isEmpty()) {

            try {
                markerlocation.clear();
                poiTargets.clear();
                MAX_NUMBER_OF_MARKERS = safeLongToInt(mAllUsers.size());

                for (int i = 0; i < mAllUsers.size(); i++) {
                    Profile user = mAllUsers.get(i);
                    LatLng latLng = coordinateForMarker(new LatLng(user.getCurrentlatitude(), user.getCurrentlongitude()), user);
                    markerlocation.put(user, latLng);

                }
                for (Profile user : markerlocation.keySet()) {

                    Marker m = getMarkerFor(user);
                    PoiTarget pt;
                    if (m == null) {
                        m = mGoogleMap.addMarker(new MarkerOptions()
                                .position(Objects.requireNonNull(markerlocation.get(user))));
                        m.setTag(user.getUid());
                        markerList.add(m);
                        pt = new PoiTarget(m);

                    }
                    else {
                        m.remove();
                        Marker newmarker = mGoogleMap.addMarker(new MarkerOptions()
                                .position(Objects.requireNonNull(markerlocation.get(user))));
                        newmarker.setTag(user.getUid());
                        markerList.add(newmarker);
                        pt = new PoiTarget(newmarker);
                    }


                    poiTargets.add(pt);
                    if (user.getRole().equals(ROLE_USER))
                    UIHelper.getInstance().attachRoundPicToPoiTarget(user.getAvatarurl(), pt, 100, 100);
                    else
                        UIHelper.getInstance().attachPicToPoiTarget(user.getAvatarurl(), pt, 200, 200);

                }

                moveCameraToCurrentLocation(new LatLng(mUser.getCurrentlatitude(), mUser.getCurrentlongitude()));

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }




    }



    private Marker getMarkerFor(Profile user) {
        for (int i = 0; i < markerList.size(); i++) {
            if (Objects.equals(markerList.get(i).getTag(), user.getUid()))
                return markerList.get(i);
        }
        return null;
    }





    @SuppressLint("MissingPermission")
    @NeedsPermission({ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mGoogleMap = googleMap;
        try {
            mGoogleMap.clear();
            poiTargets.clear();
        }
        catch (Exception e){
            Log.e(TAG, "onMapReady: " + e.toString());
        }
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        mGoogleMap.setMyLocationEnabled(false);

        mGoogleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMarkerClickListener(this);

        initUsersViewModel();
    }

    private void initUsersViewModel() {

        UsersViewModel mUsersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        LiveData<DataSnapshot> allUserLiveData = mUsersViewModel.getAllUsersDataSnapshotLiveData();
        allUserLiveData.observe(this, dataSnapshot -> {
            Log.d(TAG, "Users View Model Triggered ");
            if (dataSnapshot != null) {
                mAllUsers.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Profile user = ds.getValue(Profile.class);
                    mAllUsers.add(user);
                }
                mUser = FireBaseUsersHelper.getInstance().findProfile(FireBaseUsersHelper.getInstance().getUid(), mAllUsers);

                updateUsersMarkers();

            }
        });
    }


    private void moveCameraToCurrentLocation(LatLng loc) {

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(loc,
                11.0f);
        mGoogleMap.moveCamera(update);

    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        moveCameraToCurrentLocation(new LatLng(mUser.getCurrentlatitude(), mUser.getCurrentlongitude()));
        return true;
    }




    @Override
    public boolean onMarkerClick(Marker marker) {

        for (int i = 0; i<mAllUsers.size(); i++) {
            Profile clickeduser = mAllUsers.get(i);
            if (Objects.equals(marker.getTag(), clickeduser.getUid())) {
                FireBaseUsersHelper.getInstance().showProfilePopup(clickeduser, this);
            }
        }

        return false;
    }


    public class PoiTarget implements Target {
        private Marker m;

        PoiTarget(Marker m) { this.m = m; }

        @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            try {

                m.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                poiTargets.remove(this);
                Log.d(TAG, "onBitmapLoaded: ");

            }
            catch (Exception e){
                Log.d(TAG, "onBitmapLoaded: " + e.toString());
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            poiTargets.remove(this);
            Log.d(TAG, "onBitmapFailed: ");
        }


        @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }



    // Return whether marker with same location is already on map
    private boolean mapAlreadyHasMarkerForLocation(LatLng location, Profile key) {

        for (Profile user : markerlocation.keySet()) {
            double lng = Objects.requireNonNull(markerlocation.get(user)).longitude;
            double lat = Objects.requireNonNull(markerlocation.get(user)).latitude;


            if(lng == location.longitude && lat == location.latitude && !key.getDisplayname().equals(user.getDisplayname())){
                Log.d(TAG, "mapAlreadyHasMarkerForLocation: equal");
                return true;
            }
        }
        return false;
    }







}

