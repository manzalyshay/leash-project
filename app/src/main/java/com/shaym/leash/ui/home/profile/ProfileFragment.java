package com.shaym.leash.ui.home.profile;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shaym.leash.R;
import com.shaym.leash.logic.user.Profile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Images.Media.getBitmap;
import static com.shaym.leash.logic.CONSTANT.DIRECT_INCOMING_MESSAGES;
import static com.shaym.leash.logic.CONSTANT.DIRECT_OUTGOING_MESSAGES;
import static com.shaym.leash.logic.CONSTANT.PROFILE_PICS;
import static com.shaym.leash.logic.CONSTANT.USERS_TABLE;
import static com.shaym.leash.logic.CONSTANT.USER_GEAR_POSTS;
import static com.shaym.leash.logic.CONSTANT.USER_GEAR_POSTS_AMOUNT;
import static com.shaym.leash.logic.CONSTANT.USER_INBOX_POSTS_AMOUNT;
import static com.shaym.leash.logic.CONSTANT.USER_OUTBOX_POSTS_AMOUNT;
import static com.shaym.leash.logic.CONSTANT.USER_POSTS;
import static com.shaym.leash.logic.CONSTANT.USER_POSTS_AMOUNT;
import static java.lang.Math.multiplyExact;
import static java.lang.Math.toIntExact;

/**
 * Created by shaym on 2/17/18.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "ProfileFragment";
    //UI Objects
    private ImageView mProfileImage;
    private TextView mDisplayName;
    private TextView mUserForumPostsAmount;
    private TextView mUserGearPostsAmount;
    private TextView mInboxMsgsAmount;
    private TextView mOutboxMsgsAmount;

    private UserPostsFragment mUsersPostsFragment;
    private UserGearPostsFragment mUsersGearPostsFragment;
    private UserInboxFragment mUserInboxFragment;
    private UserOutboxFragment mUserOutboxFragment;

    private String mProfilePicRef;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private DatabaseReference mDatabase;
    private ValueEventListener mValueEventListener;
    private DatabaseReference mUserNameRef;
    private DatabaseReference mUserForumPostsRef;
    private ValueEventListener mUserPostsEventListener;
    private DatabaseReference mUserGearPostsref;
    private ValueEventListener mUserGearPostsEventListener;

    private DatabaseReference mUserInboxref;
    private DatabaseReference mUserOutboxref;
    private ValueEventListener mUserInboxEventListener;
    private ValueEventListener mUserOutboxEventListener;

    public ProfileFragment instance;
    private Profile mUser;
    public ProgressBar mProfilePicProgressBar;


    public ProfileFragment(){
        instance = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUsersPostsFragment =  new UserPostsFragment();
        mUsersGearPostsFragment = new UserGearPostsFragment();
        mUserInboxFragment = new UserInboxFragment();
        mUserOutboxFragment = new UserOutboxFragment();

        mProfileImage = v.findViewById(R.id.profilepic);
        mProfilePicProgressBar = v.findViewById(R.id.profilepic_progressbar);
        mDisplayName = v.findViewById(R.id.displayname);
        mUserForumPostsAmount = v.findViewById(R.id.userpostsamont);
        mUserForumPostsAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser == null || mUser.getForumpostsamount() ==0) {
                    Toast.makeText(getActivity(), "No Posts For User", Toast.LENGTH_SHORT).show();
                }
                else {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                    transaction.replace(R.id.root_frame_profile_fragment, mUsersPostsFragment, USER_POSTS);

                    transaction.addToBackStack(null);

                    transaction.commit();
                }
            }
        });

        mUserGearPostsAmount = v.findViewById(R.id.gearpostsamount);
        mUserGearPostsAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser == null || mUser.getgearpostsamount() ==0) {
                    Toast.makeText(getActivity(), "No Gear Posts For User", Toast.LENGTH_SHORT).show();
                }
                else {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                    transaction.replace(R.id.root_frame_profile_fragment, mUsersGearPostsFragment, USER_GEAR_POSTS);

                    transaction.addToBackStack(null);

                    transaction.commit();
                }
            }
        });
        mInboxMsgsAmount = v.findViewById(R.id.incomingmessagesamount);
        mInboxMsgsAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser == null || mUser.getInboxmsgsmount() ==0) {
                    Toast.makeText(getActivity(), "No Inbox Messages For User", Toast.LENGTH_SHORT).show();
                }
                else {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                    transaction.replace(R.id.root_frame_profile_fragment, mUserInboxFragment, DIRECT_INCOMING_MESSAGES);

                    transaction.addToBackStack(null);

                    transaction.commit();
                }
            }
        });
        mOutboxMsgsAmount = v.findViewById(R.id.outgoingmessagesamount);
        mOutboxMsgsAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser == null || mUser.getOutboxmsgsmount() ==0) {
                    Toast.makeText(getActivity(), "No Outbox Messages For User", Toast.LENGTH_SHORT).show();
                }
                else {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                    transaction.replace(R.id.root_frame_profile_fragment, mUserOutboxFragment, DIRECT_OUTGOING_MESSAGES);

                    transaction.addToBackStack(null);

                    transaction.commit();
                }
            }
        });

        mProfileImage.setOnClickListener(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        LoadUserData();

        return v;
    }

    public void LoadUserData() {
        if (mUser == null) {
            final String userId = getUid();

            mUserNameRef = mDatabase.child(USERS_TABLE).child(userId);
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "onDataChange:  DB");
                        mUser = dataSnapshot.getValue(Profile.class);
                        Log.d(TAG, "onDataChange: "+ mUser.toString());
                        LoadUI();
                        }
                        else {
                        Log.d(TAG, "onDataChange: User does not exists in DB");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: "+ databaseError);
                }
            };
            mUserNameRef.addListenerForSingleValueEvent(mValueEventListener);
        }
        else {
            LoadUI();
        }

    }

    private void LoadUI() {
        LoadUserPic();
        mDisplayName.setText(mUser.getDisplayname());
        loadPostsAmount();
        loadGearPostsAmount();
        loadInboxAmount();
        loadOutboxAmount();

//        mUserNameRef.removeEventListener(mValueEventListener);

    }

    private void loadOutboxAmount() {
        mUserOutboxref = mDatabase.child(DIRECT_OUTGOING_MESSAGES).child(getUid());
        mUserOutboxEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: GearPosts");
                long amount = dataSnapshot.getChildrenCount();
                int val = (int)amount;
                mUser.setOutboxmsgsmount(val);
                mDatabase.child(USERS_TABLE).child(getUid()).child(USER_OUTBOX_POSTS_AMOUNT).setValue(amount);
                mOutboxMsgsAmount.setText(Integer.toString(mUser.getOutboxmsgsmount()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mUserOutboxref.addValueEventListener(mUserOutboxEventListener);
    }

    private void loadInboxAmount() {
        mUserInboxref = mDatabase.child(DIRECT_INCOMING_MESSAGES).child(getUid());
        mUserInboxEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: GearPosts");
                long amount = dataSnapshot.getChildrenCount();
                int val = (int)amount;
                mUser.setInboxmsgsmount(val);
                mDatabase.child(USERS_TABLE).child(getUid()).child(USER_INBOX_POSTS_AMOUNT).setValue(amount);
                mInboxMsgsAmount.setText(Integer.toString(mUser.getInboxmsgsmount()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mUserInboxref.addValueEventListener(mUserInboxEventListener);
    }

    private void loadGearPostsAmount() {
        mUserGearPostsref = mDatabase.child(USER_GEAR_POSTS).child(getUid());
        mUserGearPostsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: GearPosts");
                long amount = dataSnapshot.getChildrenCount();
                int val = (int)amount;
                mUser.setGearpostsamount(val);
                mDatabase.child(USERS_TABLE).child(getUid()).child(USER_GEAR_POSTS_AMOUNT).setValue(amount);
                mUserGearPostsAmount.setText(Integer.toString(mUser.getgearpostsamount()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mUserGearPostsref.addValueEventListener(mUserGearPostsEventListener);
    }

    private void loadPostsAmount() {
        mUserForumPostsRef = mDatabase.child(USER_POSTS).child(getUid());
        mUserPostsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Posts");
                long amount = dataSnapshot.getChildrenCount();
                int val = (int)amount;
                mUser.setforumpostsamount(val);
                mDatabase.child(USERS_TABLE).child(getUid()).child(USER_POSTS_AMOUNT).setValue(amount);
                mUserForumPostsAmount.setText(Integer.toString(mUser.getForumpostsamount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mUserForumPostsRef.addValueEventListener(mUserPostsEventListener);
    }


    private void LoadUserPic() {
        Log.d(TAG, "LoadUserPic: ");
        if (!mUser.getAvatarURL().isEmpty()) {

            storageReference.child(mUser.getAvatarURL()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(final Uri uri) {
                    Picasso.get().load(uri).resize(400,400).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().into(mProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            mProfilePicProgressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            //Try again online if cache failed
                            Picasso.get()
                                    .load(uri)
                                    .error(R.drawable.ic_launcher)
                                    .into(mProfileImage, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            mProfilePicProgressBar.setVisibility(View.INVISIBLE);

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Log.v("Picasso","Could not fetch image" + e.toString());
                                        }
                                    });
                        }
                    });

                }
            });
        }
        else {
            Picasso.get().load(R.drawable.ic_launcher).networkPolicy(NetworkPolicy.OFFLINE).resize(400,400).centerCrop().into(mProfileImage);
            mProfilePicProgressBar.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public void onClick(View view) {
        if (view == mProfileImage) {
            showProfilePicMenu(view);
        }
    }

    private void showProfilePicMenu(View view) {

        PopupMenu popup = new PopupMenu(this.getContext(), view);
        popup.setOnMenuItemClickListener(this);// to implement on click event on items of menu
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.profile_picture_menu, popup.getMenu());
        popup.show();
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String picref = UUID.randomUUID().toString();
            mProfilePicRef = PROFILE_PICS +"/"+ getUid() + picref;
            final StorageReference ref = storageReference.child(mProfilePicRef);
            Bitmap bmp = null;
            try {
                bmp = getBitmap(getContext().getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();

                            attachPictoProfile(mProfilePicRef);
                            attachProfilePic();
                 }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });



        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            {
                Log.d(TAG, "onActivityResult: post image pick");
                filePath = data.getData();
                uploadImage();
            }
        }
    }

    private void attachProfilePic() {
        Picasso.get().load(filePath).resize(400, 400).into(mProfileImage);
    }

    private void attachPictoProfile(final String userpicref) {
        mUser.setAvatarURL(userpicref);
        mDatabase.child(USERS_TABLE).child(getUid()).setValue(mUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: User Profile pic updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: User Profile  update failed");
                    }
                });

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uploadprofilepic:
                Log.d(TAG, "onMenuItemClick: ");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                break;

            case R.id.deleteprofilepic:

                break;
        }
        return true;
    }



}
