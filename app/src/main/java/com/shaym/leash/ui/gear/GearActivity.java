package com.shaym.leash.ui.gear;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.media.ExifInterface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.ui.gear.fragments.GearFragment;
import com.shaym.leash.ui.gear.fragments.GearPostFragment;
import com.shaym.leash.ui.gear.fragments.NewGearFragment;
import com.shaym.leash.ui.gear.fragments.UsedGearFragment;
import com.shaym.leash.ui.home.SectionPagerAdapter;
import com.shaym.leash.ui.utils.BottomNavigationViewHelper;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.provider.MediaStore.Images.Media.getBitmap;
import static com.shaym.leash.logic.CONSTANT.ALL_GEAR_POSTS;
import static com.shaym.leash.logic.CONSTANT.GEAR_POSTS_PICS;
import static com.shaym.leash.logic.CONSTANT.NEW_GEAR_POSTS;
import static com.shaym.leash.logic.CONSTANT.PROFILE_PICS;
import static com.shaym.leash.logic.CONSTANT.USED_GEAR_POSTS;
import static com.shaym.leash.logic.CONSTANT.USERS_TABLE;
import static com.shaym.leash.logic.CONSTANT.USER_GEAR_POSTS;
import static com.shaym.leash.logic.CONSTANT.USER_POSTS;

/**
 * Created by shaym on 2/14/18.
 */

public class GearActivity extends AppCompatActivity {
    private static final String TAG = "GearActivity";
    private static final int ACTIVITY_NUM = 3;
    private BottomNavigationViewHelper mBottomNavHelper;
    private SectionPagerAdapter mSectionPagerAdapter;
    private ViewPager mVpager;
    private DatabaseReference mDatabase;
    public static FloatingActionButton mGearFab;
    public static boolean mNewGearPostOpened = false;
    public static boolean mUsedGearPostOpened = false;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    private AlertDialog.Builder mBuilder;
    private ImageView mGearPic;
    private String mGearPicRef;
    private String mGearType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        setContentView(R.layout.activity_gear);
        mBottomNavHelper = new BottomNavigationViewHelper(this, ACTIVITY_NUM);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setupFab();
        setupViewPager();

    }

    private void setupFab() {
        mGearFab = findViewById(R.id.fab_new_post_gear);
        mGearFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mVpager.getCurrentItem()) {
                    case 0:
                        mGearType = NEW_GEAR_POSTS;
                        break;
                    case 1:
                        mGearType = USED_GEAR_POSTS;
                        break;
                }
                showPostDialog(mGearType);
                mGearFab.setVisibility(View.INVISIBLE);
            }
        });
    }

    private String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    private void setupViewPager() {
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mSectionPagerAdapter.AddFragment(new NewGearFragment());
        mSectionPagerAdapter.AddFragment(new UsedGearFragment());

        mVpager = (ViewPager) findViewById(R.id.container_gear);

        mVpager.setAdapter(mSectionPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabgear);
        tabLayout.setupWithViewPager(mVpager);
        tabLayout.getTabAt(0).setText("New Gear");
        tabLayout.getTabAt(1).setText("Used Gear");

        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        Log.e(TAG, "selected page = " + position);
                        break;

                    default:

                        Log.e(TAG, "selected page = " + position);
                        break;
                }
                fabLogic(position);

            }
        };
        mVpager.addOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(0);
    }

    private void fabLogic(int position) {
        switch (position) {
            case 0:
                if (mNewGearPostOpened)
                    mGearFab.setVisibility(View.INVISIBLE);
                else
                    mGearFab.setVisibility(View.VISIBLE);
                break;
            case 1:
                if (mUsedGearPostOpened)
                    mGearFab.setVisibility(View.INVISIBLE);
                else
                    mGearFab.setVisibility(View.VISIBLE);
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch (mVpager.getCurrentItem()) {
            case 0:
                mNewGearPostOpened = false;
                break;
            case 1:
                mUsedGearPostOpened = false;
                break;

        }
        mGearFab.setVisibility(View.VISIBLE);
    }


    // [START write_fan_out]
    private void writeNewGearPost(String uid, String type, String author, String title, int price, String phonenumber, String description, String imageurl) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String typekey = mDatabase.child(type).push().getKey();

        GearPost post = new GearPost(uid, type, author, title, price, phonenumber, description, imageurl);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + ALL_GEAR_POSTS + "/" + typekey, postValues);
        childUpdates.put("/" + USER_GEAR_POSTS + "/" + uid + "/" + typekey, postValues);
        childUpdates.put("/" + type + "/" + typekey, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    private void showPostDialog(final String geartype) {
        mBuilder = new AlertDialog.Builder(GearActivity.this);
        //you should edit this to fit your needs
        mBuilder.setTitle("New Post");

        final EditText title = new EditText(GearActivity.this);
        title.setHint("Title");//optional
        final EditText price = new EditText(GearActivity.this);
        price.setHint("Price");//optional
        final EditText phonenumber = new EditText(GearActivity.this);
        phonenumber.setHint("Phone Number");//optional
        final Button uploadgearpic = new Button(GearActivity.this);
        uploadgearpic.setText("Attach Image");
        uploadgearpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onMenuItemClick: ");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        mGearPic = new ImageView(GearActivity.this);
        mGearPic.setVisibility(View.INVISIBLE);
        //in my example i use TYPE_CLASS_NUMBER for input only numbers
        title.setInputType(InputType.TYPE_CLASS_TEXT);
        phonenumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        price.setInputType(InputType.TYPE_CLASS_NUMBER);

        LinearLayout lay = new LinearLayout(GearActivity.this);
        lay.setOrientation(LinearLayout.VERTICAL);
        lay.addView(title);
        lay.addView(price);
        lay.addView(phonenumber);
        lay.addView(uploadgearpic);
        lay.addView(mGearPic);
        mBuilder.setView(lay);

        // Set up the buttons
        mBuilder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });


        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the two inputs
                if (title.getText().length() == 0 || price.getText().length() == 0) {
                    Toast.makeText(GearActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(GearActivity.this,
                                                "Error: could not fetch user.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Write new post
                                        if (mGearPicRef != null) {
                                            writeNewGearPost(userId, geartype, user.getDisplayname(), title.getText().toString(), Integer.parseInt(price.getText().toString()), phonenumber.getText().toString(), geartype, mGearPicRef);
                                            mGearPicRef = null;
                                        } else {
                                            writeNewGearPost(userId, geartype, user.getDisplayname(), title.getText().toString(), Integer.parseInt(price.getText().toString()), phonenumber.getText().toString(), geartype, "");

                                        }
                                        dialog.dismiss();
                                        mGearFab.setVisibility(View.VISIBLE);
                                    }


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            {
                Log.d(TAG, "onActivityResult: Post-Picupload");
                filePath = data.getData();
                uploadImage();
            }
        }
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(GearActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String picref = UUID.randomUUID().toString();
            mGearPicRef = GEAR_POSTS_PICS + "/" + getUid() + "/" + picref;
            final StorageReference ref = storageReference.child(mGearPicRef);
            Bitmap bmp = null;
            try {
                bmp = getBitmap(getContentResolver(), filePath);
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
                            Toast.makeText(GearActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                            attachPicToDialog();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(GearActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void attachPicToDialog() {
        if (mGearPic != null) {
            mGearPic.setVisibility(View.VISIBLE);
            Picasso.get().load(filePath).resize(400, 400).into(mGearPic);
        }

    }




}






