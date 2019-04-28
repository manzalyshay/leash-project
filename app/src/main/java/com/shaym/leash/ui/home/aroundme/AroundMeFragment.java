package com.shaym.leash.ui.home.aroundme;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.Task;
import com.shaym.leash.MainApplication;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.home.chat.ChatFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import static com.shaym.leash.logic.utils.FireBaseUsersHelper.BROADCAST_ALL_USERS;
import static com.shaym.leash.ui.home.chat.ChatFragment.getUid;


/**
 * Created by shaym on 2/17/18.
 */


public class AroundMeFragment extends Fragment implements OnMapReadyCallback, OnMyLocationButtonClickListener,
        OnMarkerClickListener {
    GoogleMap mGoogleMap;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "AroundMeFragment";
    protected Set<PoiTarget> poiTargets = new HashSet<>();
    HashMap<Profile, LatLng> markerlocation;
    public static final float COORDINATE_OFFSET = 0.00002f; // You can change this value according to your need
    public int MAX_NUMBER_OF_MARKERS;
    private SupportMapFragment mapFragment;
    private Dialog mClickedUserDialog;
    private List<Profile> mAllUsers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_aroundme, container, false);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
        checkLocationPermission();

        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext())).registerReceiver(mAllUsersReceiver,
                new IntentFilter(BROADCAST_ALL_USERS));

        FireBaseUsersHelper.getInstance().loadAllUserProfiles();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleMap != null) {
            addUsersMarkers();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext())).unregisterReceiver(mAllUsersReceiver);

    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainApplication.getInstace().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private BroadcastReceiver mAllUsersReceiver = new BroadcastReceiver() {
        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(TAG+ "receiver", "Got message: ");
            try {
                mAllUsers = FireBaseUsersHelper.getInstance().pullUsers();
                updateUserLocation();

                if (mapFragment != null) {
                    mapFragment.getMapAsync(AroundMeFragment.this);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }


        }
    };


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

            mGoogleMap.setOnMarkerClickListener(this);


            MAX_NUMBER_OF_MARKERS = safeLongToInt(mAllUsers.size());
            markerlocation = new HashMap<>();

            for (int i = 0; i<mAllUsers.size(); i++) {
                Profile user = mAllUsers.get(i);

                LatLng latLng = new LatLng(user.getcurrentlat(), user.getcurrentlng());
                markerlocation.put(user, latLng);

            }
            for (Profile user : markerlocation.keySet()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    markerlocation.replace(user, markerlocation.get(user), coordinateForMarker(markerlocation.get(user), user));
                }
                Marker m = mGoogleMap.addMarker(new MarkerOptions()
                        .position(markerlocation.get(user))
                        .title(user.getDisplayname()));
                final PoiTarget pt = new PoiTarget(m);

                poiTargets.add(pt);
                if (!user.getAvatarURL().isEmpty()) {

                    if (user.getAvatarURL().charAt(0) == 'p') {
                        try {
                            FireBaseUsersHelper.getInstance().getStorageReference().child(user.getAvatarURL()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(100, 100).centerCrop().transform(new CircleTransform()).into(pt));

                        } catch (Exception e) {
                            Log.d(TAG, "onDataChange: " + e.toString());
                        }
                    } else {
                        try {
                            Picasso.get().load(Uri.parse((user.getAvatarURL()))).resize(100, 100).centerCrop().transform(new CircleTransform()).into(pt);

                        } catch (Exception e) {
                            Log.d(TAG, "onDataChange: " + e.toString());
                        }
                    }

                } else {
                    Picasso.get().load(R.drawable.ic_launcher).resize(100, 100).centerCrop().transform(new CircleTransform()).into(pt);

                }
            }


        }




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        try {
            mGoogleMap.clear();
            poiTargets.clear();
        }
        catch (Exception e){
            Log.d(TAG, "onMapReady: " + e.toString());
        }
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);


        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this.getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onMapReady: Permission Denied");
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMyLocationButtonClickListener(this);
        moveCameraToCurrentLocation();

        addUsersMarkers();

    }

    private void moveCameraToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this.getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationResult = mFusedLocationClient.getLastLocation();
        locationResult.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Set the map's camera position to the current location of the device.

                Location location = task.getResult();
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(),
                            location.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng,
                            19.0f);
                    mGoogleMap.moveCamera(update);
                }
            }
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        moveCameraToCurrentLocation();
        return false;
    }




    @Override
    public boolean onMarkerClick(Marker marker) {

        for (int i = 0; i<mAllUsers.size(); i++) {
            Profile user = mAllUsers.get(i);
            if (marker.getTitle().equals(user.getDisplayname())) {
                showPopup(user);
            }
        }

        return false;
    }


    // The method that displays the popup.
    private void showPopup(final Profile mClickedUser) {
        mClickedUserDialog = new Dialog(Objects.requireNonNull(getContext()));
        mClickedUserDialog.setContentView(R.layout.dialog_profile);
// ...Irrelevant code for customizing the buttons and title

        ImageView profilepic = mClickedUserDialog.findViewById(R.id.profilepicaroundme);
        ImageView closedialogpic = mClickedUserDialog.findViewById(R.id.closedialogbtn);

        ProgressBar progressBar = mClickedUserDialog.findViewById(R.id.profilepic_progressbar_aroundme);

        if (!mClickedUser.getAvatarURL().isEmpty()) {
            if (mClickedUser.getAvatarURL().charAt(0) == 'p') {
                FireBaseUsersHelper.getInstance().getStorageReference().child(mClickedUser.getAvatarURL()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(400, 400).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(profilepic, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(uri).resize(400, 400).centerCrop().transform(new CircleTransform()).into(profilepic, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed

                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    }

                }));
            } else {
                Picasso.get().load(Uri.parse(mClickedUser.getAvatarURL())).resize(400, 400).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(profilepic, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(Uri.parse(mClickedUser.getAvatarURL())).resize(400, 400).centerCrop().transform(new CircleTransform()).into(profilepic, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed

                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    }

                });
            }
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);

        }


        TextView displayname = mClickedUserDialog.findViewById(R.id.displaynamearoundme);
        displayname.setText(mClickedUser.getDisplayname().trim());

        ImageView mailpic = mClickedUserDialog.findViewById(R.id.mail_aroundme);
        ImageView phonepic = mClickedUserDialog.findViewById(R.id.phone_aroundme);
        ImageView dmpic = mClickedUserDialog.findViewById(R.id.dm_aroundme);

        if (mClickedUser.isIsemailhidden()){
            mailpic.setVisibility(View.INVISIBLE);
        }

        if (mClickedUser.getUid().equals(getUid())){
            dmpic.setVisibility(View.INVISIBLE);
        }
        else{
            mailpic.setOnClickListener(view -> {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{mClickedUser.getEmail().trim()});
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (mClickedUser.isIsphonehidden()){
            phonepic.setVisibility(View.INVISIBLE);
        }
        else{
            phonepic.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mClickedUser.getPhonenumber().trim(), null));
                startActivity(intent);
            });
        }

        dmpic.setOnClickListener(view -> {
//            getFragmentManager().beginTransaction().hide(mapFragment).commit();

            ChatFragment cf = ChatFragment.newInstance(mClickedUser.getUid());


            assert getFragmentManager() != null;
            cf.show(getFragmentManager(), "fragment_chat");

            mClickedUserDialog.dismiss();


        });


        closedialogpic.setOnClickListener(view -> mClickedUserDialog.dismiss());


        Objects.requireNonNull(mClickedUserDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mClickedUserDialog.show();
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


    private void updateUserLocation() {
        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    FireBaseUsersHelper.getInstance().updateUserLocation(location.getLatitude(), location.getLongitude());
                }

            });
        }else {

            Log.d(TAG, "onRequestPermissionsResult: Permission Denied");

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        try {
                            mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> FireBaseUsersHelper.getInstance().updateUserLocation(location.getLatitude(), location.getLongitude()));
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                } else {

                    Log.d(TAG, "onRequestPermissionsResult: Permission Denied");

                }
            }

        }
    }


}

