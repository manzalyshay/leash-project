package com.shaym.leash.ui.forum;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.Login;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.logic.utils.ImageUploadHelper;
import com.shaym.leash.ui.forum.fragments.GeneralFragment;
import com.shaym.leash.ui.forum.fragments.SpotsFragment;
import com.shaym.leash.ui.forum.fragments.TripsFragment;
import com.shaym.leash.ui.gear.GearActivity;
import com.shaym.leash.ui.home.SectionPagerAdapter;
import com.shaym.leash.ui.home.chat.ChatFragment;
import com.shaym.leash.ui.utils.BottomNavigationViewHelper;
import com.shaym.leash.ui.utils.NavHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static android.provider.MediaStore.Images.Media.getBitmap;
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS_PICS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS_PICS;
import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.TRIPS_POSTS;
import static com.shaym.leash.logic.utils.FireBaseUsersHelper.BROADCAST_USER;
import static com.shaym.leash.ui.home.chat.ChatFragment.getUid;

/**
 * Created by shaym on 2/14/18.
 */

public class ForumActivity extends AppCompatActivity {
    private static final String TAG = "ForumActivity";
    private static final int ACTIVITY_NUM = 2;
    private ViewPager mVpager;

    @SuppressLint("StaticFieldLeak")
    public static FloatingActionButton mFab;
    public static boolean GeneralPostOpened = false;
    public static boolean SpotsPostOpened = false;
    public static boolean TripsPostOpened = false;
    public final static int GENERAL_FRAGMENT_ITEM_ID = 0301;
    public final static int SPOTS_FRAGMENT_ITEM_ID = 0302;
    public final static int TRIPS_FRAGMENT_ITEM_ID = 0303;
    private Profile mUser;
    private DrawerLayout mDrawerLayout;
    private NavHelper mNavHelper;
    private Dialog mNewPostDialog;
    private ImageView mAttachPreview;
    private String mAttachRef = "";
    private Uri attachPath;
    private final int PICK_IMAGE_REQUEST = 71;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        initUi();
    }

    private void initUi() {
        mDrawerLayout = findViewById(R.id.drawer_layout_forum);
        NavigationView mNavigationView = findViewById(R.id.nav_view_forum);
        BottomNavigationViewHelper mBottomNavHelper = new BottomNavigationViewHelper(ForumActivity.this, ACTIVITY_NUM);
        setupFab();
        setupViewPager();
        setupToolBar();

        mNavHelper = new NavHelper(mNavigationView, mVpager, mBottomNavHelper, ACTIVITY_NUM);
    }


    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_forum);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(ForumActivity.this).registerReceiver(mUserReceiver,
                new IntentFilter(BROADCAST_USER));
        FireBaseUsersHelper.getInstance().loadCurrentUserProfile();
    }

    private BroadcastReceiver mUserReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(TAG + "receiver", "Got message: ");
            Bundle args = intent.getBundleExtra("DATA");

            mUser = (Profile) args.getSerializable("USEROBJ");

            updateUI();
        }
    };

    private void updateUI() {
        mNavHelper.setCurrentUser(mUser);
    }

    private void setupFab() {
        mFab = findViewById(R.id.fab_new_post);
        mFab.setOnClickListener(v -> {
            switch (mVpager.getCurrentItem()) {
                case 0:
                    showPostDialog(GENERAL_POSTS);
                    break;
                case 1:
                    showPostDialog(SPOTS_POSTS);
                    break;
                case 2:
                    showPostDialog(TRIPS_POSTS);
                    break;
            }

        });
    }

    private void setupViewPager() {
        SectionPagerAdapter mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mSectionPagerAdapter.AddFragment(new GeneralFragment());
        mSectionPagerAdapter.AddFragment(new SpotsFragment());
        mSectionPagerAdapter.AddFragment(new TripsFragment());

        mVpager = findViewById(R.id.container_forum);

        mVpager.setAdapter(mSectionPagerAdapter);
        mVpager.setOffscreenPageLimit(2);


        TabLayout tabLayout = findViewById(R.id.tabsforum);
        tabLayout.setupWithViewPager(mVpager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText(R.string.general_menu_title);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText(R.string.spots_menu_title);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText(R.string.trips_menu_title);

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

    private void fabLogic(int pos) {
        switch (pos) {
            case 0:
                if (GeneralPostOpened)
                    mFab.setVisibility(View.INVISIBLE);
                else
                    mFab.setVisibility(View.VISIBLE);
                break;
            case 1:
                if (SpotsPostOpened)
                    mFab.setVisibility(View.INVISIBLE);
                else
                    mFab.setVisibility(View.VISIBLE);
                break;
            case 2:
                if (TripsPostOpened)
                    mFab.setVisibility(View.INVISIBLE);
                else
                    mFab.setVisibility(View.VISIBLE);
                break;
        }

    }

    private void showPostDialog(final String forum) {
        // The method that displays the popup.
        mNewPostDialog = new Dialog(Objects.requireNonNull(ForumActivity.this));
        mNewPostDialog.setContentView(R.layout.dialog_forumpost );
// ...Irrelevant code for customizing the buttons and title

        EditText mSubjectEditText = mNewPostDialog.findViewById(R.id.postdialog_subject);
        EditText mSubjectBodyText = mNewPostDialog.findViewById(R.id.postdialog_body);
        LinearLayout mAttachLayout = mNewPostDialog.findViewById(R.id.postdialog_attach);
        LinearLayout mSubmitLayout = mNewPostDialog.findViewById(R.id.postdialog_submit);
        mAttachPreview = mNewPostDialog.findViewById(R.id.attachment_preview);
        ImageView closedialogpic = mNewPostDialog.findViewById(R.id.postdialog_close);

        mAttachLayout.setOnClickListener(v -> {
                Log.d(TAG, "AttachPictureLayout Clicked ForumPost ");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE_REQUEST);
            });

        mSubmitLayout.setOnClickListener(view -> {
            //get the two inputs
            if (mSubjectEditText.getText().length() == 0 || mSubjectBodyText.getText().length() == 0) {
                Toast.makeText(ForumActivity.this, getString(R.string.fields_missing), Toast.LENGTH_SHORT).show();
            } else if (mUser != null && mUser.getUid().equals(getUid())) {
                // Write new post
                if (mAttachRef.isEmpty()) {
                    FireBasePostsHelper.getInstance().writeNewPost(mUser.getUid(), mUser.getDisplayname(), mSubjectEditText.getText().toString(), mSubjectBodyText.getText().toString(), forum, "");
                }
                else {
                    FireBasePostsHelper.getInstance().writeNewPost(mUser.getUid(), mUser.getDisplayname(), mSubjectEditText.getText().toString(), mSubjectBodyText.getText().toString(), forum, mAttachRef);

                }
                mNewPostDialog.dismiss();
            } else {
                FireBaseUsersHelper.getInstance().loadCurrentUserProfile();
                Toast.makeText(this, R.string.data_loading, Toast.LENGTH_SHORT).show();
            }

        });


        closedialogpic.setOnClickListener(view -> mNewPostDialog.dismiss());


        Objects.requireNonNull(mNewPostDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mNewPostDialog.show();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch (mVpager.getCurrentItem()) {
            case 0:
                GeneralPostOpened = false;
                break;
            case 1:
                SpotsPostOpened = false;
                break;
            case 2:
                TripsPostOpened = false;
                break;
        }
        mFab.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadImage() {

        if (attachPath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.uploading_label));
            progressDialog.show();
            String picref = UUID.randomUUID().toString();
            mAttachRef = FORUM_POSTS_PICS + "/" + getUid() + "/" + picref;
            final StorageReference ref = FireBasePostsHelper.getInstance().getStorageReference().child(mAttachRef);
            Bitmap bmp = null;
            try {
                bmp = ImageUploadHelper.getInstance().modifyOrientation(getBitmap(getContentResolver(), attachPath), ImageUploadHelper.getInstance().getPath(this, attachPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            assert bmp != null;
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            ref.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, R.string.uploaded_label, Toast.LENGTH_SHORT).show();

                        attachPicToDialog();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, getString(R.string.failed_label) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage(getString(R.string.uploaded_label) + (int) progress + "%");
                    });


        }
    }

    private void attachPicToDialog() {
        if (mAttachPreview != null) {
            mAttachPreview.setVisibility(View.VISIBLE);
            Picasso.get().load(attachPath).resize(200, 200).into(mAttachPreview);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            {
                Log.d(TAG, "onActivityResult: Post-Picupload");
                attachPath = data.getData();

                if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
                    uploadImage();
                }
            }
        }
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }
    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                (dialog, which) -> ActivityCompat.requestPermissions((Activity) context,
                        new String[] { permission },
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE));
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadImage();
                } else {
                    Toast.makeText(ForumActivity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }


}







