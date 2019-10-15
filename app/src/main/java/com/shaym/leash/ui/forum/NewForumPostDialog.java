package com.shaym.leash.ui.forum;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cloudinary.android.MediaManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.shaym.leash.R;
import com.shaym.leash.data.utils.FireBasePostsHelper;
import com.shaym.leash.data.utils.onPictureUploadedListener;
import com.shaym.leash.models.Profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.shaym.leash.data.utils.CONSTANT.FORUM_IMAGES;
import static com.shaym.leash.data.utils.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.data.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.data.utils.CONSTANT.TRIPS_POSTS;

@RuntimePermissions
public class NewForumPostDialog extends DialogFragment implements onPictureUploadedListener {
    public static final String TAG = "NewForumPostDialog";
    public static final String CATEGORY_KEY = "CATEGORY_KEY";

    private final int FORUM_PICK_IMAGE_REQUEST = 72;
    private Spinner mCategorySpinner;
    private List<String> mPostImagepaths;
    private Bitmap selectedBitmap;
    private EditText mBodyInput;
    private Profile mUser;
    private String mNewPostCategory;
    private TextView mUploadLabel;
    private boolean mImagePicked;

    public NewForumPostDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static NewForumPostDialog newInstance(String NewPostCategory) {
        NewForumPostDialog frag = new NewForumPostDialog();
        Bundle args = new Bundle();
        args.putString(CATEGORY_KEY, NewPostCategory);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        assert args != null;
        mNewPostCategory = args.getString(CATEGORY_KEY);
        //Firebase
        return inflater.inflate(R.layout.dialog_newforumpost, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //findviews
        initViews();
    }

    private void initBodyLayout() {
        mCategorySpinner = Objects.requireNonNull(getView()).findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.forum_categories_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(adapter);
        setCurrentCategory();
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                       @Override
                                                       public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                           Log.d(TAG, "onItemSelected: " + id);
                                                           switch ((int) id) {
                                                               case 0:
                                                                   mNewPostCategory = GENERAL_POSTS;
                                                                   break;

                                                               case 1:
                                                                   mNewPostCategory = SPOTS_POSTS;
                                                                   break;
                                                               case 2:
                                                                   mNewPostCategory = TRIPS_POSTS;
                                                                   break;


                                                           }
                                                       }

                                                       @Override
                                                       public void onNothingSelected(AdapterView<?> parent) {
                                                           mNewPostCategory = GENERAL_POSTS;

                                                       }
                                                   }
        );

        mBodyInput = getView().findViewById(R.id.forumpost_body);
        mUploadLabel = getView().findViewById(R.id.upload_label);

    }

    private void setCurrentCategory() {
        switch (mNewPostCategory) {
            case GENERAL_POSTS:
                mCategorySpinner.setSelection(0);
                break;

            case SPOTS_POSTS:
                mCategorySpinner.setSelection(1);
                break;

            case TRIPS_POSTS:
                mCategorySpinner.setSelection(2);
                break;


        }
    }

    private void initViews() {
        initBodyLayout();
        initUploadLayout();
        initSubmitLayout();
    }

    private void initUploadLayout() {
        LinearLayout mUploadLayout = Objects.requireNonNull(getView()).findViewById(R.id.upload_layout);
        mPostImagepaths = new ArrayList<>();
        mUploadLayout.setOnClickListener(view -> {
            NewForumPostDialogPermissionsDispatcher.pickImageWithPermissionCheck(NewForumPostDialog.this);

        });

    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    void showRationaleForExtStorage(final PermissionRequest request) {
        new AlertDialog.Builder(getContext())
                .setTitle("Permission Needed")
                .setMessage("This permission is needed in order to upload image")
                .setPositiveButton("OK", (dialog, which) -> request.proceed())
                .setNegativeButton("Cancel", (dialog, which) -> request.cancel())
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        NewForumPostDialogPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void pickImage() {
        ImagePicker.Companion.with(this)
                .crop(1f, 1f)	    		//Crop Square image(Optional)
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start(FORUM_PICK_IMAGE_REQUEST);

    }


    public void setUser(Profile user){
        mUser = user;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");

        if (resultCode == Activity.RESULT_OK && requestCode == FORUM_PICK_IMAGE_REQUEST) {
            // File object will not be null for RESULT_OK
            File file = ImagePicker.Companion.getFile(data);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            selectedBitmap = BitmapFactory.decodeStream(fis);

            if (selectedBitmap != null) {
                mImagePicked = true;
                mUploadLabel.setText(Objects.requireNonNull(getActivity()).getString(R.string.image_picked));
                mUploadLabel.setTextColor(Color.BLACK);
                mUploadLabel.setTypeface(null, Typeface.BOLD);

            }
        }



    }

    private void initSubmitLayout() {
        LinearLayout mSubmitLayout = Objects.requireNonNull(getView()).findViewById(R.id.postdialog_submit);

        mSubmitLayout.setOnClickListener(view -> {

            if (mBodyInput.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), "Body Field is empty.", Toast.LENGTH_SHORT).show();
            } else {

                submitPost();
            }
        });
    }


    private void submitPost() {
        if (!mImagePicked) {
            publishPost();

        }
        else{
            FireBasePostsHelper.getInstance().uploadImage(getContext(),    mUser.getUid() + "/"+ FORUM_IMAGES , selectedBitmap, this);

        }
    }

    @Override
    public void onPictureUploaded(String uploadPath){
        Log.d(TAG, "onPictureUploaded: ");
        mPostImagepaths.add(uploadPath);
        publishPost();
    }

    @Override
    public void onUploadFailed() {
        Log.d(TAG, "onUploadFailed: ");
    }


    private void publishPost() {
        FireBasePostsHelper.getInstance().writeNewForumPost(mUser, mBodyInput.getText().toString().trim(), mNewPostCategory, mPostImagepaths);
        Toast.makeText(getContext(), getString(R.string.post_published), Toast.LENGTH_SHORT).show();
        this.dismiss();
    }




}


