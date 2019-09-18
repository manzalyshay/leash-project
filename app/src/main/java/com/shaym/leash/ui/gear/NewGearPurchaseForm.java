package com.shaym.leash.ui.gear;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.shaym.leash.R;

import java.util.List;

import permissions.dispatcher.RuntimePermissions;

public class NewGearPurchaseForm extends DialogFragment  {
    public static final String TAG = "NewGearPurchaseForm";

    private Spinner mCitySpinner;
    private List<String> citieslist;
    private EditText mNotesInput;
    private EditText mNameInput;
    private EditText mPhoneInput;


    public NewGearPurchaseForm() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    static NewGearPurchaseForm newInstance() {
        NewGearPurchaseForm frag = new NewGearPurchaseForm();


        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.dialog_newgearpurchase, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //findviews

//        initCitesList();
//        initViews();

    }

//    private void initBodyLayout() {
//        mCitySpinner = Objects.requireNonNull(getView()).findViewById(R.id.cities_spinner);
//        ArrayAdapter<String> citiesadapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
//                R.layout.spinner_item, citieslist);
//        citiesadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mCitySpinner.setAdapter(citiesadapter);
//        mUploadLabel = getView().findViewById(R.id.add_picture_label);
//        mCategorySpinner = getView().findViewById(R.id.category_spinner);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
//                R.array.gear_categories_array, R.layout.spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mCategorySpinner.setAdapter(adapter);
//            setCurrentCategory();
//
//        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                                      @Override
//                                                      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                                          Log.d(TAG, "onItemSelected: " + id);
//                                                          switch ((int) id) {
//                                                              case 0:
//                                                                  mNewPostCategory = BOARDS_POSTS;
//                                                                  break;
//                                                              case 1:
//                                                                  mNewPostCategory = LEASHES_POSTS;
//                                                                  break;
//                                                              case 2:
//                                                                  mNewPostCategory = FINS_POSTS;
//                                                                  break;
//                                                              case 3:
//                                                                  mNewPostCategory = CLOTHING_POSTS;
//                                                                  break;
//                                                              case 4:
//                                                                  mNewPostCategory = OTHER_POSTS;
//                                                                  break;
//
//                                                          }
//                                                      }
//
//                                                      @Override
//                                                      public void onNothingSelected(AdapterView<?> parent) {
//                                                          mNewPostCategory = BOARDS_POSTS;
//
//                                                      }
//                                                  }
//        );
//
//        mBodyInput = getView().findViewById(R.id.gearpost_body);
//        mPriceInput = getView().findViewById(R.id.gearpost_price);
//        mPhoneInput = getView().findViewById(R.id.gearpost_contactphone);
//
//    }
//
//    private void setCurrentCategory() {
//        switch (mNewPostCategory){
//            case BOARDS_POSTS:
//                mCategorySpinner.setSelection(0);
//                break;
//
//            case LEASHES_POSTS:
//                mCategorySpinner.setSelection(1);
//                break;
//
//            case FINS_POSTS:
//                mCategorySpinner.setSelection(2);
//                break;
//
//            case CLOTHING_POSTS:
//                mCategorySpinner.setSelection(3);
//                break;
//
//            case OTHER_POSTS:
//                mCategorySpinner.setSelection(4);
//                break;
//
//        }
//    }
//
//    private void initViews() {
//        initBodyLayout();
//        initUploadLayout();
//        initSubmitLayout();
//    }
//
//    private void initUploadLayout() {
//        LinearLayout mUploadLayout = Objects.requireNonNull(getView()).findViewById(R.id.upload_layout);
//
//        mPostImagepaths = new ArrayList<>();
//
//        mUploadLayout.setOnClickListener(view -> NewGearPostDialogPermissionsDispatcher.pickImageWithPermissionCheck(NewGearPurchaseForm.this));
//
//    }
//
//
//    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
//    void pickImage(){
//
//        ImagePicker.Companion.with(this)
//                .crop(1f, 1f)	    		//Crop Square image(Optional)
//                .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                .start(GEAR_PICK_IMAGE_REQUEST);
//
//
//    }
//
//
//
//    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
//    void showRationaleForExtStorage(final PermissionRequest request){
//        new AlertDialog.Builder(getContext())
//                .setTitle("Permission Needed")
//                .setMessage("This permission is needed in order to upload image")
//                .setPositiveButton("OK", (dialog, which) -> request.proceed())
//                .setNegativeButton("Cancel", (dialog, which) -> request.cancel())
//                .show();
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        NewGearPostDialogPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
//    }
//
//
//
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: ");
//
//        if (resultCode == Activity.RESULT_OK && requestCode == GEAR_PICK_IMAGE_REQUEST) {
//            // File object will not be null for RESULT_OK
//            File file = ImagePicker.Companion.getFile(data);
//            FileInputStream fis = null;
//            try {
//                fis = new FileInputStream(file);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            selectedBitmap = BitmapFactory.decodeStream(fis);
//
//            if (selectedBitmap != null) {
//                mImagePicked = true;
//                mUploadLabel.setText(Objects.requireNonNull(getActivity()).getString(R.string.image_picked));
//                mUploadLabel.setTextColor(Color.BLACK);
//                mUploadLabel.setTypeface(null, Typeface.BOLD);
//            }
//        }
//    }
//
//
//
//    private void initSubmitLayout() {
//        LinearLayout mSubmitLayout = Objects.requireNonNull(getView()).findViewById(R.id.postdialog_submit);
//
//        mSubmitLayout.setOnClickListener(view -> {
//
//            if (mPhoneInput.getText().toString().trim().isEmpty()) {
//                Toast.makeText(getActivity(), "Phone Field is empty.", Toast.LENGTH_SHORT).show();
//            } else if (mPriceInput.getText().toString().trim().isEmpty()) {
//                Toast.makeText(getActivity(), "Price Field is empty.", Toast.LENGTH_SHORT).show();
//            } else if (mBodyInput.getText().toString().trim().isEmpty()) {
//                Toast.makeText(getActivity(), "Body Field is empty.", Toast.LENGTH_SHORT).show();
//            } else {
//
//                submitPost();
//            }
//        });
//    }
//
//
//    private void initCitesList () {
//        Log.d(TAG, "Loading cities...");
//        final Resources resources = Objects.requireNonNull(getActivity()).getResources();
//        InputStream inputStream = resources.openRawResource(R.raw.israel_cities_hebrew);
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
//            citieslist = new ArrayList<>();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                citieslist.add(line);
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG, "DONE loading words.");
//    }
//
//
//    private void submitPost() {
//            //get the two inputs
//            if (!mImagePicked) {
//                publishPost();
//
//            }
//            else{
//                FireBasePostsHelper.getInstance().uploadImage(getContext(), GEAR_POSTS_PICS, selectedBitmap, this);
//            }
//    }
//
//
//    @Override
//    public void onPictureUploaded(String uploadPath){
//        Log.d(TAG, "onPictureUploaded: ");
//        mPostImagepaths.add(uploadPath);
//        publishPost();
//    }
//
//    @Override
//    public void onUploadFailed() {
//        Log.d(TAG, "onUploadFailed: ");
//    }
//
//    private void publishPost() {
//        if (mNewPostStore.isEmpty()) {
//            FireBasePostsHelper.getInstance().writeNewUsedGearPost(FireBaseUsersHelper.getInstance().getUid(), mNewPostCategory, citieslist.get(mCitySpinner.getSelectedItemPosition()), parseValue(mPriceInput.getText().toString().trim()), mPhoneInput.getText().toString().trim(), mBodyInput.getText().toString().trim(), mPostImagepaths);
//        }
//        else {
//            FireBasePostsHelper.getInstance().writeNewGearPost(FireBaseUsersHelper.getInstance().getUid(), mNewPostCategory, citieslist.get(mCitySpinner.getSelectedItemPosition()), parseValue(mPriceInput.getText().toString().trim()), mPhoneInput.getText().toString().trim(), mBodyInput.getText().toString().trim(), mPostImagepaths);
//
//        }
//        Toast.makeText(getContext(), "Post Published.", Toast.LENGTH_SHORT).show();
//        dismiss();
//    }
//
//    private int parseValue(String str) {
//        return str.isEmpty()? -1  : Integer.parseInt(str.trim());
//    }
//



}


