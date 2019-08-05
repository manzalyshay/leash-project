package com.shaym.leash.ui.home.aroundme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.user.UsersViewModel;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.logic.utils.UsersHelperListener;
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
import static com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;


/**
 * Created by shaym on 2/17/18.
 */

@RuntimePermissions
public class AroundMeFragment extends Fragment implements OnMapReadyCallback, OnMyLocationButtonClickListener,
        OnMarkerClickListener {
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "AroundMeFragment";
    protected Set<PoiTarget> poiTargets = new HashSet<>();
    HashMap<Profile, LatLng> markerlocation = new HashMap<>();
    public static final float COORDINATE_OFFSET = 0.00002f; // You can change this value according to your need
    public int MAX_NUMBER_OF_MARKERS;
    private SupportMapFragment mapFragment;
    private List<Profile> mAllUsers = new ArrayList<>();
    private Profile mUser;
    private UsersViewModel mUsersViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_aroundme, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));

        AroundMeFragmentPermissionsDispatcher.getCurrentLocationWithPermissionCheck(this);
    }



    @SuppressLint("MissingPermission")
    @NeedsPermission(ACCESS_COARSE_LOCATION)
    void getCurrentLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(Objects.requireNonNull(getActivity()), location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        FireBaseUsersHelper.getInstance().updateUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                    mapFragment.getMapAsync(AroundMeFragment.this);

                });


    }

    @OnShowRationale(ACCESS_COARSE_LOCATION)
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


    @OnPermissionDenied(ACCESS_COARSE_LOCATION)
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

    private void addUsersMarkers() {
        //to fetch all the users of firebase Auth app
        if (mGoogleMap != null && !mAllUsers.isEmpty()) {

            mGoogleMap.clear();
            markerlocation.clear();

            MAX_NUMBER_OF_MARKERS = safeLongToInt(mAllUsers.size());

            for (int i = 0; i<mAllUsers.size(); i++) {
                Profile user = mAllUsers.get(i);
                LatLng latLng = coordinateForMarker(new LatLng(user.getCurrentlatitude(), user.getCurrentlongitude()), user);
                markerlocation.put(user, latLng);

            }
            for (Profile user : markerlocation.keySet()) {

                Marker m = mGoogleMap.addMarker(new MarkerOptions()
                        .position(markerlocation.get(user))
                        .snippet(user.getUid()));
                final PoiTarget pt = new PoiTarget(m);

                poiTargets.add(pt);
                attachImageToTarget(user.getAvatarurl(), pt);

            }

            moveCameraToCurrentLocation(new LatLng(mUser.getCurrentlatitude(), mUser.getCurrentlongitude()));

        }




    }

    private void attachImageToTarget(String avatarUrl, PoiTarget pt) {
        if (!avatarUrl.isEmpty()) {

            if (avatarUrl.charAt(0) == 'g') {
                try {
                    FireBaseUsersHelper.getInstance().getStorageReference().child(avatarUrl).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(100, 100).centerCrop().transform(new CircleTransform()).into(pt));

                } catch (Exception e) {
                    Log.d(TAG, "onDataChange: " + e.toString());
                }
            } else {
                try {
                    Picasso.get().load(Uri.parse((avatarUrl))).resize(100, 100).centerCrop().transform(new CircleTransform()).into(pt);

                } catch (Exception e) {
                    Log.d(TAG, "onDataChange: " + e.toString());
                }
            }

        } else {
            Picasso.get().load(R.drawable.launcher_leash).resize(100, 100).centerCrop().transform(new CircleTransform()).into(pt);

        }
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(ACCESS_COARSE_LOCATION)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mGoogleMap = googleMap;
        try {
            mGoogleMap.clear();
            poiTargets.clear();
        }
        catch (Exception e){
            Log.d(TAG, "onMapReady: " + e.toString());
        }
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.setOnMyLocationButtonClickListener(this);
        googleMap.setOnMarkerClickListener(this);

        initUsersViewModel();
    }

    private void initUsersViewModel() {

        mUsersViewModel = ViewModelProviders.of(this).get(UsersViewModel.class);

        LiveData<DataSnapshot> currentUserLiveData = mUsersViewModel.getCurrentUserDataSnapshotLiveData();

        currentUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "initUsersViewModel: ");
                mUser = dataSnapshot.getValue(Profile.class);
            }
        });

        LiveData<DataSnapshot> allUserLiveData = mUsersViewModel.getAllUsersDataSnapshotLiveData();
        allUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                mAllUsers.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Profile user = ds.getValue(Profile.class);
                    mAllUsers.add(user);
                }

                addUsersMarkers();

            }
        });
    }


    private void moveCameraToCurrentLocation(LatLng loc) {

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(loc,
                19.0f);
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
            Profile user = mAllUsers.get(i);
            if (marker.getSnippet().equals(user.getUid())) {
                FireBasePostsHelper.getInstance().showProfilePopup(user, this);
            }
        }

        return false;
    }




    class PoiTarget implements Target {
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
            double lng = markerlocation.get(user).longitude;
            double lat = markerlocation.get(user).latitude;


            if(lng == location.longitude && lat == location.latitude && !key.getDisplayname().equals(user.getDisplayname())){
                Log.d(TAG, "mapAlreadyHasMarkerForLocation: equal");
                return true;
            }
        }
        return false;
    }







}

