package com.shaym.leash.ui.forum;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.utils.FireBasePostsHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS_PICS;
import static com.shaym.leash.logic.utils.CONSTANT.GENERAL_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.TRIPS_POSTS;


public class NewForumPostDialog extends DialogFragment {
    public static final String TAG = "NewGearPostDialog";
    private final int PICK_IMAGE_REQUEST = 71;
    private Spinner mCategorySpinner;
    private List<String> mPostImagepaths;
    private Uri filePath;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private EditText mBodyInput;
    private LinearLayout mUploadLayout;
    private LinearLayout mSubmitLayout;

    private StorageReference mStorage;
    private String mNewPostCategory;


    public NewForumPostDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static NewForumPostDialog newInstance() {
        NewForumPostDialog frag = new NewForumPostDialog();
//        Bundle args = new Bundle();
//        args.putString("title", title);
//        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        Spinner categorySpinner = Objects.requireNonNull(getView()).findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.forum_categories_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            uploadImage();
        }
    }

    private void initSubmitLayout() throws IOException {
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
            //get the two inputs
            FireBasePostsHelper.getInstance().writeNewForumPost(getUid(), mBodyInput.getText().toString().trim(), mNewPostCategory, mPostImagepaths);

            Toast.makeText(getContext(), "Post Published.", Toast.LENGTH_SHORT).show();
            this.dismiss();
    }


    private String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(GEAR_POSTS_PICS + "/" + getUid() + "/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        mPostImagepaths.add(ref.toString());
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

}


