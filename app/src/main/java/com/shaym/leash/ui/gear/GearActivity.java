package com.shaym.leash.ui.gear;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.gear.fragments.NewGearFragment;
import com.shaym.leash.ui.gear.fragments.UsedGearFragment;
import com.shaym.leash.ui.home.SectionPagerAdapter;
import com.shaym.leash.ui.utils.BottomNavigationViewHelper;
import com.shaym.leash.ui.utils.NavHelper;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static android.provider.MediaStore.Images.Media.getBitmap;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS_PICS;
import static com.shaym.leash.logic.utils.CONSTANT.NEW_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;
import static com.shaym.leash.logic.utils.FireBaseUsersHelper.BROADCAST_USER;

/**
 * Created by shaym on 2/14/18.
 */

public class GearActivity extends AppCompatActivity {
    private static final String TAG = "GearActivity";
    private ViewPager mVpager;
    @SuppressLint("StaticFieldLeak")
    public static FloatingActionButton mGearFab;
    public static boolean mNewGearPostOpened = false;
    public static boolean mUsedGearPostOpened = false;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    private ImageView mGearPic;
    private String mGearPicRef;
    private String mGearType;
    private DrawerLayout mDrawerLayout;
    public final static int NEWGEAR_FRAGMENT_ITEM_ID = 0401;
    public final static int USEDGEAR_FRAGMENT_ITEM_ID = 0402;
    private Profile mUser;
    private NavHelper mNavHelper;
    private static final int ACTIVITY_NUM = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gear);

        initUI();
    }

    private void initUI() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mDrawerLayout = findViewById(R.id.drawer_layout_gear);

        setupFab();
        setupViewPager();
        initToolBar();

        mNavHelper = new NavHelper(findViewById(R.id.nav_view_gear), mVpager, new BottomNavigationViewHelper(GearActivity.this, ACTIVITY_NUM), ACTIVITY_NUM);
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_gear);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(GearActivity.this).registerReceiver(mUserReceiver,
                new IntentFilter(BROADCAST_USER));
        FireBaseUsersHelper.getInstance().loadCurrentUserProfile();
    }


    private BroadcastReceiver mUserReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(TAG +"receiver", "Got message: ");
            Bundle args = intent.getBundleExtra("DATA");

            mUser = (Profile) args.getSerializable("USEROBJ");
            updateUI();
        }
    };

    private void updateUI() {
        mNavHelper.setCurrentUser(mUser);
    }


    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(GearActivity.this).unregisterReceiver(mUserReceiver);
    }

    private void setupFab() {
        mGearFab = findViewById(R.id.fab_new_post_gear);
        mGearFab.setOnClickListener(v -> {
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
        });
    }

    private String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    }

    private void setupViewPager() {
        SectionPagerAdapter mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mSectionPagerAdapter.AddFragment(new NewGearFragment());
        mSectionPagerAdapter.AddFragment(new UsedGearFragment());

        mVpager = findViewById(R.id.container_gear);

        mVpager.setAdapter(mSectionPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabgear);
        tabLayout.setupWithViewPager(mVpager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText(getString(R.string.newgear_menu_title));
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText(getString(R.string.usedgear_menu_title));

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



    private void showPostDialog(final String geartype) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(GearActivity.this);
        //you should edit this to fit your needs
        mBuilder.setTitle(R.string.new_post_label);

        final EditText title = new EditText(GearActivity.this);
        title.setHint(R.string.title_label);//optional
        final EditText price = new EditText(GearActivity.this);
        price.setHint(R.string.price_label);//optional
        final EditText phonenumber = new EditText(GearActivity.this);
        phonenumber.setHint(R.string.phone_number_label);//optional
        final Button uploadgearpic = new Button(GearActivity.this);
        uploadgearpic.setText(R.string.attach_image_label);
        uploadgearpic.setOnClickListener(view -> {

            Log.d(TAG, "onMenuItemClick: ");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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
        mBuilder.setPositiveButton(getString(R.string.ok), (dialog, whichButton) -> {
        });


        mBuilder.setNegativeButton(getString(R.string.cancel), (dialog, whichButton) -> dialog.cancel());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            //get the two inputs
            if (title.getText().length() == 0 || price.getText().length() == 0) {
                Toast.makeText(GearActivity.this, getString(R.string.fields_missing), Toast.LENGTH_SHORT).show();
            } else  {
                // Write new post
                if (mGearPicRef != null) {
                    FireBasePostsHelper.getInstance().writeNewGearPost(mUser.getUid(), geartype, mUser.getDisplayname(), title.getText().toString(), Integer.parseInt(price.getText().toString()), phonenumber.getText().toString(), geartype, mGearPicRef);
                } else {
                    FireBasePostsHelper.getInstance().writeNewGearPost(mUser.getUid(), geartype, mUser.getDisplayname(), title.getText().toString(), Integer.parseInt(price.getText().toString()), phonenumber.getText().toString(), geartype, "");

                }
                dialog.dismiss();
                mGearFab.setVisibility(View.VISIBLE);
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
            progressDialog.setTitle(getString(R.string.uploading_label));
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
            assert bmp != null;
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            ref.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(GearActivity.this, R.string.uploaded_label, Toast.LENGTH_SHORT).show();

                        attachPicToDialog();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(GearActivity.this, getString(R.string.failed_label) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage(getString(R.string.uploaded_label) + (int) progress + "%");
                    });


        }
    }

    private void attachPicToDialog() {
        if (mGearPic != null) {
            mGearPic.setVisibility(View.VISIBLE);
            Picasso.get().load(filePath).resize(400, 400).into(mGearPic);
        }

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



}






