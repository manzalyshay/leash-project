package com.shaym.leash.ui.forum;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import androidx.appcompat.widget.ThemedSpinnerAdapter;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.utils.FireBasePostsHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS_PICS;
import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.TRIPS_POSTS;

@RuntimePermissions
public class NewForumPostDialog extends DialogFragment {
    public static final String TAG = "NewGearPostDialog";
    public static final String CATEGORY_KEY = "CATEGORY_KEY";

    private final int FORUM_PICK_IMAGE_REQUEST = 72;
    private Spinner mCategorySpinner;
    private List<String> mPostImagepaths;
    private Bitmap filePath;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private EditText mBodyInput;
    private LinearLayout mUploadLayout;
    private LinearLayout mSubmitLayout;

    private StorageReference mStorage;
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
        mNewPostCategory = args.getString(CATEGORY_KEY);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        return inflater.inflate(R.layout.dialog_newforumpost, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //findviews

        mStorage = FirebaseStorage.getInstance().getReference();

        try {
            initViews();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        switch (mNewPostCategory){
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

    private void initViews() throws IOException {
        initBodyLayout();
        initUploadLayout();
        initSubmitLayout();
    }

    private void initUploadLayout() {
        mUploadLayout = Objects.requireNonNull(getView()).findViewById(R.id.upload_layout);
        mPostImagepaths = new ArrayList<>();
        mUploadLayout.setOnClickListener(view -> {
            NewForumPostDialogPermissionsDispatcher.pickImageWithPermissionCheck(NewForumPostDialog.this);

        });

    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    void showRationaleForExtStorage(final PermissionRequest request){
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
    public void pickImage(){
//        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getContext());
//        startActivityForResult(chooseImageIntent, FORUM_PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if(requestCode == FORUM_PICK_IMAGE_REQUEST && resultCode == RESULT_OK)
        {
//            try {
////                filePath = ImagePicker.getImageFromResult(getContext(), resultCode, data);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            if ( filePath != null) {
                mImagePicked = true;
                mUploadLabel.setText("Image Picked!");
                mUploadLabel.setTextColor(Color.RED);
            }
        }
    }

    private void initSubmitLayout() {
        mSubmitLayout = Objects.requireNonNull(getView()).findViewById(R.id.postdialog_submit);

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
            uploadImage();
        }
    }


    private String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    }

    private void publishPost() {
        FireBasePostsHelper.getInstance().writeNewForumPost(getUid(), mBodyInput.getText().toString().trim(), mNewPostCategory, mPostImagepaths);
        Toast.makeText(getContext(), "Post Published.", Toast.LENGTH_SHORT).show();
        this.dismiss();
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            String uploadPath = FORUM_POSTS_PICS + "/" + getUid() + "/" + UUID.randomUUID().toString();
            StorageReference ref = storageReference.child(uploadPath);
            byte[] data = convertBitmapToByteArray(filePath);

            ref.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        mPostImagepaths.add(uploadPath);
                        publishPost();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    });
        }
    }

    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = null;
        try {
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            return stream.toByteArray();
        }finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.e(ThemedSpinnerAdapter.Helper.class.getSimpleName(), "ByteArrayOutputStream was not closed");
                }
            }
        }
    }

}


