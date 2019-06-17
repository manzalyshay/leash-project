package com.shaym.leash.ui.gear;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.utils.FireBasePostsHelper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ThemedSpinnerAdapter;
import androidx.fragment.app.DialogFragment;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;
import static com.shaym.leash.logic.utils.CONSTANT.BOARDS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CLOTHING_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.FINS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS_PICS;
import static com.shaym.leash.logic.utils.CONSTANT.LEASHES_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.OTHER_POSTS;

@RuntimePermissions
public class NewGearPostDialog extends DialogFragment implements BSImagePicker.OnSingleImageSelectedListener, BSImagePicker.ImageLoaderDelegate {
    public static final String TAG = "NewGearPostDialog";
    public static final String CATEGORY_KEY = "CATEGORY_KEY";

    public static final int GEAR_PICK_IMAGE_REQUEST = 71;
    private Spinner mCategorySpinner;
    private Spinner mCitySpinner;
    private List<String> citieslist;
    private List<String> mPostImagepaths;
    private Uri filePath;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private EditText mBodyInput;
    private EditText mPriceInput;
    private EditText mPhoneInput;
    private LinearLayout mUploadLayout;
    private LinearLayout mSubmitLayout;
    private TextView mUploadLabel;
    private boolean mImagePicked;
    private StorageReference mStorage;
    private String mNewPostCategory;


    public NewGearPostDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static NewGearPostDialog newInstance(String currentCategory) {
        NewGearPostDialog frag = new NewGearPostDialog();

        Bundle args = new Bundle();
        args.putString(CATEGORY_KEY, currentCategory);
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
        return inflater.inflate(R.layout.dialog_newgearpost, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //findviews

        mStorage = FirebaseStorage.getInstance().getReference();

        try {
            initCitesList();
        } catch (IOException e) {
            e.printStackTrace();
        }


            initViews();

    }

    private void initBodyLayout() {
        mCitySpinner = Objects.requireNonNull(getView()).findViewById(R.id.cities_spinner);
        ArrayAdapter<String> citiesadapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()),
                R.layout.spinner_item, citieslist);
        citiesadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCitySpinner.setAdapter(citiesadapter);
        mUploadLabel = getView().findViewById(R.id.add_picture_label);
        mCategorySpinner = getView().findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gear_categories_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(adapter);
            setCurrentCategory();

        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                      @Override
                                                      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                          Log.d(TAG, "onItemSelected: " + id);
                                                          switch ((int) id) {
                                                              case 0:
                                                                  mNewPostCategory = BOARDS_POSTS;
                                                                  break;
                                                              case 1:
                                                                  mNewPostCategory = LEASHES_POSTS;
                                                                  break;
                                                              case 2:
                                                                  mNewPostCategory = FINS_POSTS;
                                                                  break;
                                                              case 3:
                                                                  mNewPostCategory = CLOTHING_POSTS;
                                                                  break;
                                                              case 4:
                                                                  mNewPostCategory = OTHER_POSTS;
                                                                  break;

                                                          }
                                                      }

                                                      @Override
                                                      public void onNothingSelected(AdapterView<?> parent) {
                                                          mNewPostCategory = BOARDS_POSTS;

                                                      }
                                                  }
        );

        mBodyInput = getView().findViewById(R.id.gearpost_body);
        mPriceInput = getView().findViewById(R.id.gearpost_price);
        mPhoneInput = getView().findViewById(R.id.gearpost_contactphone);

    }

    private void setCurrentCategory() {
        switch (mNewPostCategory){
            case BOARDS_POSTS:
                mCategorySpinner.setSelection(0);
                break;

            case LEASHES_POSTS:
                mCategorySpinner.setSelection(1);
                break;

            case FINS_POSTS:
                mCategorySpinner.setSelection(2);
                break;

            case CLOTHING_POSTS:
                mCategorySpinner.setSelection(3);
                break;

            case OTHER_POSTS:
                mCategorySpinner.setSelection(4);
                break;

        }
    }

    private void initViews() {
        initBodyLayout();
        initUploadLayout();
        initSubmitLayout();
    }

    private void initUploadLayout() {
        mUploadLayout = Objects.requireNonNull(getView()).findViewById(R.id.upload_layout);
        mPostImagepaths = new ArrayList<>();
        mUploadLayout.setOnClickListener(view -> {
            NewGearPostDialogPermissionsDispatcher.pickImageWithPermissionCheck(NewGearPostDialog.this);
        });

    }


    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void pickImage(){
        BSImagePicker singleSelectionPicker = new BSImagePicker.Builder("com.shaym.leash.fileprovider")
                .setSpanCount(3) //Default: 3. This is the number of columns
                .setGridSpacing(Utils.dp2px(2)) //Default: 2dp. Remember to pass in a value in pixel.
                .setPeekHeight(Utils.dp2px(360)) //Default: 360dp. This is the initial height of the dialog.
                .build();

        singleSelectionPicker.show(getChildFragmentManager(), "picker");

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
        NewGearPostDialogPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }








    private void initSubmitLayout() {
        mSubmitLayout = Objects.requireNonNull(getView()).findViewById(R.id.postdialog_submit);

        mSubmitLayout.setOnClickListener(view -> {

            if (mPhoneInput.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Phone Field is empty.", Toast.LENGTH_SHORT).show();
            } else if (mPriceInput.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Price Field is empty.", Toast.LENGTH_SHORT).show();
            } else if (mBodyInput.getText().toString().trim().isEmpty()) {
                Toast.makeText(getActivity(), "Body Field is empty.", Toast.LENGTH_SHORT).show();
            } else {

                submitPost();
            }
        });
    }


    private void initCitesList () throws IOException {
        Log.d(TAG, "Loading cities...");
        final Resources resources = getActivity().getResources();
        InputStream inputStream = resources.openRawResource(R.raw.israel_cities_hebrew);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        citieslist = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                citieslist.add(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
        Log.d(TAG, "DONE loading words.");
    }


    private void submitPost() {
            //get the two inputs
            if (!mImagePicked) {
                publishPost();

            }
            else{
                uploadImage();
            }
    }

    private void publishPost() {
        FireBasePostsHelper.getInstance().writeNewGearPost(getUid(), mNewPostCategory, citieslist.get(mCitySpinner.getSelectedItemPosition()), parseValue(mPriceInput.getText().toString().trim()), mPhoneInput.getText().toString().trim(), mBodyInput.getText().toString().trim(), mPostImagepaths);
        Toast.makeText(getContext(), "Post Published.", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private int parseValue(String str) {
        return str.isEmpty()? -1  : Integer.parseInt(str.trim());
    }


    private String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

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

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            String uploadPath = GEAR_POSTS_PICS + "/" + getUid() + "/" + UUID.randomUUID().toString();
            StorageReference ref = storageReference.child(uploadPath);

            ref.putFile(filePath)
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

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
            filePath = uri;

        if ( filePath != null) {
            mImagePicked = true;
            mUploadLabel.setText("Image Picked!");
            mUploadLabel.setTextColor(Color.RED);
        }
    }

    @Override
    public void loadImage(File imageFile, ImageView ivImage) {

    }
}

