package com.shaym.leash.ui.home.aroundme;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.MainApplication;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.user.Profile;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashSet;
import java.util.Set;

import static com.google.android.gms.maps.GoogleMap.*;
import static com.shaym.leash.logic.CONSTANT.AVATAR_URL;
import static com.shaym.leash.logic.CONSTANT.USERS_TABLE;

/**
 * Created by shaym on 2/17/18.
 */


public class AroundMeFragment extends Fragment implements OnMapReadyCallback, OnMyLocationButtonClickListener,
        OnMyLocationClickListener {
    GoogleMap mGoogleMap;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "AroundMeFragment";
    DatabaseReference rootRef;
    DatabaseReference usersRef;
    StorageReference storageReference;
    FirebaseStorage storage;
    private Set<PoiTarget> poiTargets = new HashSet<PoiTarget>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_aroundme, container, false);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        // Gets the MapView from the XML layout and creates it
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        checkLocationPermission();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child(USERS_TABLE);

        return v;
    }

    private void addUsersMarkers() {
        //to fetch all the users of firebase Auth app
        if (mGoogleMap != null) {
            mGoogleMap.clear();

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        Profile user = ds.getValue(Profile.class);
                        Marker m;
                        LatLng latLng = new LatLng(user.getcurrentlat(), user.getcurrentlng());

                        ds.getRef().child(AVATAR_URL).setValue("");
                        m = mGoogleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(user.getDisplayname()));
                        final PoiTarget pt = new PoiTarget(m);
                        poiTargets.add(pt);
                        if (!user.getAvatarURL().isEmpty()) {
                            storageReference.child(user.getAvatarURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    Picasso.get().load(uri).resize(100, 100).centerCrop().transform(new CircleTransform()).into(pt);


                                }
                            });

                        }
                        else {
                            Picasso.get().load(R.drawable.ic_leash).resize(100, 100).centerCrop().transform(new CircleTransform()).into(pt);

                        }
        }
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            usersRef.addListenerForSingleValueEvent(eventListener);
        }
    }


    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onMapReady: Permission Denied");
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMyLocationButtonClickListener(this);
        mGoogleMap.setOnMyLocationClickListener(this);

        moveCameraToCurrentLocation();

        addUsersMarkers();

    }

    private void moveCameraToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.

                    Location location = task.getResult();
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(),
                                location.getLongitude());
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng,
                                12.0f);
                        mGoogleMap.moveCamera(update);
                    }
                }
            }
        });
    }


    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this.getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        moveCameraToCurrentLocation();
        return false;    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this.getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainApplication.getInstace().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this.getContext())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mGoogleMap!=null) {
            addUsersMarkers();
        }
    }

    public  void refresh(){
        addUsersMarkers();
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
                    if (ContextCompat.checkSelfPermission(this.getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {

                    Log.d(TAG, "onRequestPermissionsResult: Permission Denied");

                }
                return;
            }

        }
    }


    class PoiTarget implements Target {
        private Marker m;

        public PoiTarget(Marker m) { this.m = m; }

        @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            m.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            poiTargets.remove(this);
            Log.d(TAG, "onBitmapLoaded: ");
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            poiTargets.remove(this);
            Log.d(TAG, "onBitmapFailed: ");
        }


        @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
}

