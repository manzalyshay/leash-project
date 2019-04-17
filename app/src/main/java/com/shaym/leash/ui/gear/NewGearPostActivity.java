package com.shaym.leash.ui.gear;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.logic.utils.ImageUploadHelper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static android.provider.MediaStore.Images.Media.getBitmap;
import static com.shaym.leash.logic.utils.CONSTANT.BOARDS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.CLOTHING_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.FINS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS_PICS;
import static com.shaym.leash.logic.utils.CONSTANT.LEASHES_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.OTHER_POSTS;

public class NewGearPostActivity extends AppCompatActivity
{
    private static final String TAG = "NewGearPostActivity";

    private RecyclerView mUploadList;
    private List<String> citieslist;
    private String mNewPostCategory;
    private String mNewPostClothingSize;
    private String mNewPostClothingKind;

    private UploadListAdapter uploadListAdapter;
    StorageReference mStorage;
    private List<Uri> mPostImageUrls;
    private List<String> mPostImagepaths;

    private static final int RESULT_LOAD_IMAGE = 1;
    private LinearLayout mBoardLayout;
    private EditText boardManufactorer;
    private EditText boardVolume;
    private EditText boardWidth;
    private EditText boardHeight;
    private EditText boardYear;
    private EditText boardModel;
    private LinearLayout mLeashesLayout;
    private EditText leashManufactorer;
    private LinearLayout mFinsLayout;
    private EditText finsManufactorer;

    private LinearLayout mClothingLayout;
    private EditText clothingManufactorer;

    private EditText mSubjectBodyText;
    private EditText mContactText;

    private EditText mPriceText;
    private EditText mPhoneText;
    private Spinner citiesSpinner;

    private Uri filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_newgearpost);
        mStorage = FirebaseStorage.getInstance().getReference();

        try {
            initCitesList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initViews();

    }

    @Override
    public void onStart() {
        super.onStart();


    }


    private void initViews() {
        initUploadPreviewList();
        initBoardsLayout();
        initFinsLayout();
        initLeashesLayout();
        initClothingLayout();
        initBodyLayout();
        initUploadLayout();
        initSubmitLayout();

    }

    private void initSubmitLayout() {
        LinearLayout mSubmitLayout = findViewById(R.id.postdialog_submit);
        mPostImagepaths = new ArrayList<>();

        mSubmitLayout.setOnClickListener(view -> {

            if (mContactText.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Contact Field is empty.", Toast.LENGTH_SHORT).show();
            } else if (mPhoneText.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Phone Field is empty.", Toast.LENGTH_SHORT).show();
            } else if (mPriceText.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Price Field is empty.", Toast.LENGTH_SHORT).show();
            } else if (mPostImageUrls.size() > 0) {
                uploadAllImages();
            } else {

                submitPost();
            }
        });
    }




    private void uploadAllImages() {


        for (int i=0; i<mPostImageUrls.size(); i++){
            if (i == mPostImageUrls.size()-1){
                uploadImage(mPostImageUrls.get(i), true);
            }
            else {
                uploadImage(mPostImageUrls.get(i), false);

            }
        }


    }

    private void uploadImage(Uri uri, boolean isLast) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.uploading_label));
        progressDialog.show();
        String picref = UUID.randomUUID().toString();
        String attachref = GEAR_POSTS_PICS + "/" + getUid() + "/" + picref;
        mPostImagepaths.add(attachref);
        final StorageReference ref = FireBasePostsHelper.getInstance().getStorageReference().child(attachref);
        Bitmap bmp = null;
        try {
            bmp = ImageUploadHelper.getInstance().modifyOrientation(getBitmap(getContentResolver(), uri), ImageUploadHelper.getInstance().getPath(this, uri));
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


                    if (isLast){
                        submitPost();
                    }
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


    private void submitPost() {
        //get the two inputs
        switch (mNewPostCategory) {
                    case BOARDS_POSTS:
                        FireBasePostsHelper.getInstance().writeNewBoardGearPost(getUid(), mNewPostCategory, parseValue(boardVolume.getText().toString()), parseValue(boardHeight.getText().toString()), parseValue(boardWidth.getText().toString()), parseValue(boardYear.getText().toString()), boardModel.getText().toString().trim(), boardManufactorer.getText().toString().trim(), mContactText.getText().toString().trim(), citieslist.get(citiesSpinner.getSelectedItemPosition()), parseValue(mPriceText.getText().toString().trim()), mPhoneText.getText().toString().trim(), mSubjectBodyText.getText().toString().trim(), mPostImagepaths);
                        break;

                    case LEASHES_POSTS:
                        FireBasePostsHelper.getInstance().writeNewLeashGearPost(getUid(), mNewPostCategory, leashManufactorer.getText().toString().trim(), mContactText.getText().toString().trim(), citieslist.get(citiesSpinner.getSelectedItemPosition()), parseValue(mPriceText.getText().toString().trim()), mPhoneText.getText().toString().trim(), mSubjectBodyText.getText().toString().trim(), mPostImagepaths);
                        break;

                    case FINS_POSTS:
                        FireBasePostsHelper.getInstance().writeNewFinsGearPost(getUid(), mNewPostCategory, finsManufactorer.getText().toString().trim(), mContactText.getText().toString().trim(), citieslist.get(citiesSpinner.getSelectedItemPosition()), parseValue(mPriceText.getText().toString().trim()), mPhoneText.getText().toString().trim(), mSubjectBodyText.getText().toString().trim(), mPostImagepaths);

                        break;

                    case CLOTHING_POSTS:
                        FireBasePostsHelper.getInstance().writeNewClothingGearPost(getUid(), mNewPostCategory, clothingManufactorer.getText().toString().trim(), mNewPostClothingKind, mNewPostClothingSize, mContactText.getText().toString().trim(), citieslist.get(citiesSpinner.getSelectedItemPosition()), parseValue(mPriceText.getText().toString().trim()), mPhoneText.getText().toString().trim(), mSubjectBodyText.getText().toString().trim(), mPostImagepaths);

                        break;

                    case OTHER_POSTS:
                        FireBasePostsHelper.getInstance().writeNewGearPost(getUid(), mNewPostCategory, mContactText.getText().toString().trim(), citieslist.get(citiesSpinner.getSelectedItemPosition()), parseValue(mPriceText.getText().toString().trim()), mPhoneText.getText().toString().trim(), mSubjectBodyText.getText().toString().trim(), mPostImagepaths);
                        break;
        }
        Toast.makeText(this, "Post Published.", Toast.LENGTH_SHORT).show();

    }


    private void initBodyLayout() {
        citiesSpinner = findViewById(R.id.cities_spinner);
        ArrayAdapter<String> citiesadapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, citieslist);
        citiesadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citiesSpinner.setAdapter(citiesadapter);

        Spinner categorySpinner = findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gear_categories_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: " + id);
                hideLayouts();
                switch ((int) id){
                    case 0:
                        mBoardLayout.setVisibility(View.VISIBLE);
                        mNewPostCategory = BOARDS_POSTS;
                        break;
                    case 1:
                        mLeashesLayout.setVisibility(View.VISIBLE);
                        mNewPostCategory = LEASHES_POSTS;
                        break;
                    case 2:
                        mFinsLayout.setVisibility(View.VISIBLE);
                        mNewPostCategory = FINS_POSTS;
                        break;
                    case 3:
                        mClothingLayout.setVisibility(View.VISIBLE);
                        mNewPostCategory = CLOTHING_POSTS;
                        break;
                    case 4:
                        hideLayouts();
                        mNewPostCategory = OTHER_POSTS;
                        break;

                }
            }

            private void hideLayouts() {

                mFinsLayout.setVisibility(View.GONE);
                mLeashesLayout.setVisibility(View.GONE);
                mClothingLayout.setVisibility(View.GONE);
                mBoardLayout.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mSubjectBodyText = findViewById(R.id.gearpost_body);
        mContactText = findViewById(R.id.gearpost_contact);

        mPriceText = findViewById(R.id.gearpost_price);
        mPhoneText = findViewById(R.id.gearpost_contactphone);

    }



    private void initClothingLayout() {
        mClothingLayout = findViewById(R.id.clothing_layout);
        clothingManufactorer = findViewById(R.id.manufacturer_clothing);
        Spinner clothingSize = findViewById(R.id.clothing_size);

        List<String> clothingSizeArr = Arrays.asList(getResources().getStringArray(R.array.gear_clothing_size));
        ArrayAdapter<String> sizeadapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, clothingSizeArr);
        sizeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clothingSize.setAdapter(sizeadapter);
        clothingSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNewPostClothingSize = clothingSizeArr.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner clothingKind = findViewById(R.id.clothing_kind);
        final List<String> clothingKindArr = Arrays.asList(getResources().getStringArray(R.array.gear_clothing_kind));
        ArrayAdapter<String> kindadapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, clothingKindArr);
        kindadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clothingKind.setAdapter(kindadapter);
        clothingKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNewPostClothingKind = clothingKindArr.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initLeashesLayout() {
        mLeashesLayout = findViewById(R.id.leashes_layout);
        leashManufactorer = findViewById(R.id.manufacturer_leashes);
    }

    private void initFinsLayout() {
        mFinsLayout = findViewById(R.id.fins_layout);
        finsManufactorer = findViewById(R.id.manufacturer_fins);
    }

    private void initBoardsLayout() {
        mBoardLayout = findViewById(R.id.boards_layout);
        boardManufactorer = findViewById(R.id.manufacturer_board);
        boardVolume = findViewById(R.id.board_volume);
        boardWidth = findViewById(R.id.board_width);
        boardHeight = findViewById(R.id.board_height);
        boardYear = findViewById(R.id.board_year);
        boardModel = findViewById(R.id.board_model);
    }

    private void initUploadPreviewList() {
        View v = findViewById(R.id.gear_post_layout);
        mUploadList = v.findViewById(R.id.upload_list);

        uploadListAdapter = new UploadListAdapter();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        //RecyclerView
        mUploadList.setLayoutManager(layoutManager);
//        mUploadList.setHasFixedSize(true);
        mUploadList.setAdapter(uploadListAdapter);
    }

    private void initUploadLayout() {
        LinearLayout uploadlayout = findViewById(R.id.upload_layout);
        mPostImageUrls = new ArrayList<>();

        uploadlayout.setOnClickListener(view -> {
            Log.d(TAG, "initNewGearPostDialog: Upload Images");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE);

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){

                filePath = data.getData();
                mPostImageUrls.add(filePath);
                try{
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    uploadListAdapter.addImage(bitmap);
                    uploadListAdapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


    }

    private int parseValue(String str) {
        return str.isEmpty()? -1  : Integer.parseInt(str.trim());
    }

    private void initCitesList() throws IOException {
        Log.d(TAG, "Loading cities...");
        final Resources resources = this.getResources();
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

    private String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    }

}
