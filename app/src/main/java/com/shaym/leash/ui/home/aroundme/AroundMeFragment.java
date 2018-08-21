package com.shaym.leash.ui.home.aroundme;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.user.Profile;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import at.markushi.ui.CircleButton;

import static com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import static com.shaym.leash.logic.CONSTANT.DIRECT_INCOMING_MESSAGES;
import static com.shaym.leash.logic.CONSTANT.DIRECT_OUTGOING_MESSAGES;
import static com.shaym.leash.logic.CONSTANT.USERS_TABLE;
import static com.shaym.leash.logic.CONSTANT.USER_INBOX_POSTS_AMOUNT;
import static com.shaym.leash.logic.CONSTANT.USER_OUTBOX_POSTS_AMOUNT;


/**
 * Created by shaym on 2/17/18.
 */


public class AroundMeFragment extends Fragment implements OnMapReadyCallback, OnMyLocationButtonClickListener,
        OnMyLocationClickListener, OnMarkerClickListener {
    GoogleMap mGoogleMap;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "AroundMeFragment";
    DatabaseReference rootRef;
    DatabaseReference usersRef;
    StorageReference storageReference;
    FirebaseStorage storage;
    private Set<PoiTarget> poiTargets = new HashSet<PoiTarget>();
    private ValueEventListener mUsersEventListener;
    private ValueEventListener mUsersChatListener;
    private Profile mClickedUser;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_aroundme, container, false);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

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

            mGoogleMap.setOnMarkerClickListener(this);

            mUsersEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        Profile user = ds.getValue(Profile.class);
                        Marker m;
                        LatLng latLng = new LatLng(user.getcurrentlat(), user.getcurrentlng());

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

        usersRef.removeEventListener(mUsersEventListener);
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            usersRef.addListenerForSingleValueEvent(mUsersEventListener);
        }
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

    @Override
    public boolean onMarkerClick(Marker marker) {

        mUsersChatListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mClickedUser = ds.getValue(Profile.class);
                    showPopup(getActivity(), mClickedUser.getUid() );
                }

                usersRef.removeEventListener(mUsersChatListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        usersRef.addValueEventListener(mUsersChatListener);
        return false;
    }


    // The method that displays the popup.
    private void showPopup(final Activity context, final String sendtouid) {
        int popupWidth = 200;
        int popupHeight = 150;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup_map_marker);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_map_marker, viewGroup);
        CircleButton messageBtn = layout.findViewById(R.id.msgbtn);
        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);


        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, getActivity().getWindowManager().getDefaultDisplay().getWidth()/2 , getActivity().getWindowManager().getDefaultDisplay().getHeight()/2);

        CircleButton msgbtn =  layout.findViewById(R.id.msgbtn);
        msgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPostDialog(sendtouid);
                popup.dismiss();
            }
        });

        // Getting a reference to Close button, and close the popup when clicked.
        CircleButton close =  layout.findViewById(R.id.closebtn);

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
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


    private void showPostDialog(final String sendtouid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //you should edit this to fit your needs
        builder.setTitle(R.string.DMLabel);

        final EditText title = new EditText(getContext());
        title.setHint(R.string.DMSubject);//optional
        final EditText body = new EditText(getContext());
        body.setHint(R.string.DMContent);//optional

        //in my example i use TYPE_CLASS_NUMBER for input only numbers
        title.setInputType(InputType.TYPE_CLASS_TEXT);
        body.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        LinearLayout lay = new LinearLayout(getContext());
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(title);
        lay.addView(body);
        builder.setView(lay);

        // Set up the buttons
        builder.setPositiveButton(R.string.SubmitPostLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });


        builder.setNegativeButton(R.string.CancelPostLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the two inputs
                if (title.getText().length() == 0 || body.getText().length() == 0) {
                    Toast.makeText(getContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    final String userId = getUid();
                    mDatabase.child(USERS_TABLE).child(userId).addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Get user value
                                    Profile user = dataSnapshot.getValue(Profile.class);
                                    // [START_EXCLUDE]
                                    if (user == null) {
                                        // User is null, error out
                                        Log.e(TAG, "User " + userId + " is unexpectedly null");
                                        Toast.makeText(getContext(),
                                                "Error: could not fetch user.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Write new post
                                        writeNewPost(userId, user.getDisplayname(), title.getText().toString(), body.getText().toString(), sendtouid);
                                        dialog.dismiss();
                                    }

                                    // Finish this Activity, back to the stream
//                                        setEditingEnabled(true);
                                    // [END_EXCLUDE]

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    // [START_EXCLUDE]
//                                        setEditingEnabled(true);
                                    // [END_EXCLUDE]
                                }
                            });
                    // [END single_value_read]
                }

            }
        });


    }

    private void writeNewPost(String userId, String displayname, String title, String body, String sendtouid) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String outgoingkey = mDatabase.child(DIRECT_OUTGOING_MESSAGES).child(userId).push().getKey();

        Post post = new Post(userId, sendtouid, displayname, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        // Incoming messages : Inbox -> ReceiverID -> SenderID -> Posts
        childUpdates.put("/" + DIRECT_INCOMING_MESSAGES + "/" + sendtouid  + "/" + outgoingkey, postValues);
        // outgoing messages : OutGoingbox -> userId -> sendtouid
        childUpdates.put("/" + DIRECT_OUTGOING_MESSAGES + "/" + userId + "/" + outgoingkey, postValues);


        mDatabase.updateChildren(childUpdates);
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();

    }



}

