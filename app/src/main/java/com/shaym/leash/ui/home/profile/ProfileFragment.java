package com.shaym.leash.ui.home.profile;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.ui.forum.ForumAdapter;
import com.shaym.leash.ui.forum.onPostSelectedListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.FORUM_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.SPOTS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.TRIPS_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USED_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;

/**
 * Created by shaym on 2/17/18.
 */


public class ProfileFragment extends Fragment implements onPostSelectedListener, View.OnClickListener  {

    private static final String TAG = "ProfileFragment";
    private TextView mForumTab;
    private TextView mStoreTab;
    private TextView mInboxTab;


    private String mProfilePicRef;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private int mCurrentTab;

    public ProfileFragment instance;
    private Profile mUser;
    private AlertDialog mChangeMailDialog;
    private RecyclerView mRecyclerView;
    private ProfileAdapter mAdapter;
    private DatabaseReference mDatabase;

    public ProfileFragment(){
        instance = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCurrentTab = 0;
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        initUI();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null){
            mAdapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void initUI() {
        mForumTab = Objects.requireNonNull(getView()).findViewById(R.id.forum_activity);
        mStoreTab = getView().findViewById(R.id.store_activity);
        mInboxTab = getView().findViewById(R.id.inbox);

        mForumTab.setOnClickListener(this);
        mStoreTab.setOnClickListener(this);
        mInboxTab.setOnClickListener(this);

        mRecyclerView = Objects.requireNonNull(getView()).findViewById(R.id.profile_list);
        mRecyclerView.setHasFixedSize(true);

        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery();
        Log.d(TAG, "initUi: " + postsQuery.toString());

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();

        mAdapter = new ProfileAdapter(options, this );
        mRecyclerView.setAdapter(mAdapter);

    }

    private void swapAdapter() {
        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery();
        Log.d(TAG, "initUi: " + postsQuery.toString());

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post.class)
                .build();

        ProfileAdapter newAdapter = new ProfileAdapter(options, this );

        newAdapter.startListening();

        mRecyclerView.swapAdapter(newAdapter, true);

        mAdapter = newAdapter;
    }

    private Query getQuery() {
        Query query = null;

        switch (mCurrentTab){
            case 0:
                query = mDatabase.child(FORUM_POSTS).child(USER_POSTS).child(getUid());
                break;
            case 1:
                query = mDatabase.child(GEAR_POSTS).child(USED_GEAR_POSTS).child(USER_POSTS).child(getUid());
                break;
            case 2:
                query = mDatabase.child(CHAT_CONVERSATIONS).child(USER_POSTS).child(getUid());
                break;
        }

        return query;

    }

//    public void LoadUserData() {
//        if (mUser == null ||!mUser.getUid().equals(getUid())) {
//            FireBaseUsersHelper.getInstance().loadCurrentUserProfile();
//            FireBaseUsersHelper.getInstance().loadUserProfileData();
//
//        }
//        else {
//            updateUI();
//        }
//
//    }


//    private void updateUI() {
//        FireBaseUsersHelper.getInstance().LoadUserPic(mUser.getAvatarURL(), mProfileImage, mProfilePicProgressBar, 400);
//        mDisplayName.setText(mUser.getDisplayname());
//        mPhoneNumView.setText(mUser.getPhonenumber());
//        mEmailView.setText(mUser.getEmail());
//        isEmailHidden.setChecked(!mUser.isIsemailhidden());
//        isPhoneNumHidden.setChecked(!mUser.isIsphonehidden());
//
//        mUserForumPostsAmount.setText(String.valueOf(mUser.getForumpostsamount()));
//        mUserGearPostsAmount.setText(String.valueOf(mUser.getgearpostsamount()));
//
//        mInboxMsgsAmount.setText(String.valueOf(mUser.getConversationssmount()));
//        if (mUser.getUnreadmsgsamount() >0){
//            mInboxMsgsAmount.setTextColor(Color.RED);
//        }
//
//    }



//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//            case R.id.profilepic:
//            showProfilePicMenu(view);
//            break;
//
//            case R.id.userpostslayout:
//                if (mUser == null || mUser.getForumpostsamount() ==0) {
//                    Toast.makeText(getActivity(), "No Posts For User", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    assert getFragmentManager() != null;
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//                // Replace whatever is in the fragment_container view with this fragment,
//                // and add the transaction to the back stack if needed
//                    transaction.replace(R.id.root_frame_profile_fragment, mUsersPostsFragment, USER_POSTS);
//
//                    transaction.addToBackStack(null);
//
//                    transaction.commit();
//                }
//                break;
//
//
//
//            case R.id.gearpostslayout:
////                if (mUser == null || mUser.getgearpostsamount() ==0) {
////                    Toast.makeText(getActivity(), "No Gear Posts For User", Toast.LENGTH_SHORT).show();
////                }
////                else {
////                    assert getFragmentManager() != null;
////                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
////
////// Replace whatever is in the fragment_container view with this fragment,
////// and add the transaction to the back stack if needed
////                    transaction.replace(R.id.root_frame_profile_fragment, mUsersGearPostsFragment, USER_GEAR_POSTS);
////
////                    transaction.addToBackStack(null);
////
////                    transaction.commit();
////                }
//                break;
//
//            case R.id.incomingmessageslayout:
//                if (mUser == null || mUser.getConversationssmount() ==0) {
//                    Toast.makeText(getActivity(), "No Inbox Messages For User", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    assert getFragmentManager() != null;
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//// Replace whatever is in the fragment_container view with this fragment,
//// and add the transaction to the back stack if needed
//                    transaction.replace(R.id.root_frame_profile_fragment, mUserInboxFragment, "");
//
//                    transaction.addToBackStack(null);
//
//                    transaction.commit();
//                }
//                break;
//
//            case R.id.email_edit_btn:
//                if (!mEmailView.isEnabled()) {
//                    mEmailView.setEnabled(true);
//                    mEmailView.requestFocus();
//                }
//                else{
//                    if(LoginActivity.isEmailValid(mEmailView.getText().toString().trim())) {
//                        mChangeMailDialog.show();
//                        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
//                        mChangeMailDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
//                            if (!mPassVerf.getText().toString().isEmpty())
//                            {
//                                changeMail(mEmailView.getText().toString().trim(), mPassVerf.getText().toString().trim());
//                                mChangeMailDialog.dismiss();
//                            }
//                            else
//                            {
//                                Toast.makeText(getContext(), R.string.enter_password_title, Toast.LENGTH_LONG).show();
//
//                            }
//                        });
//
//                    }
//                    else {
//                        Toast.makeText(getContext(), R.string.enter_valid_email_message, Toast.LENGTH_SHORT).show();
//                    }
//                }
//                break;
//
//            case R.id.phone_edit_button:
//                if (!mPhoneNumView.isEnabled()) {
//                    mPhoneNumView.setEnabled(true);
//                    mPhoneNumView.requestFocus();
//                }
//                else{
//                    if (mPhoneNumView.getText().toString().length() == 10) {
//                        mPhoneNumView.setEnabled(false);
//                        mUser.setPhonenumber(mPhoneNumView.getText().toString());
//                        FireBaseUsersHelper.getInstance().getDatabase().child(USERS_TABLE).child(getUid()).setValue(mUser);
//                    }
//                    else {
//                        Toast.makeText(getContext(), "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                break;
//        }
//    }


//    private void showProfilePicMenu(View view) {
//
//        PopupMenu popup = new PopupMenu(Objects.requireNonNull(this.getContext()), view);
//        popup.getMenu().add(0, UPLOAD_IMAGE_ID, 0, R.string.change_profile_pic);
//        popup.getMenu().add(0, DELETE_IMAGE_ID, 1, R.string.delete_profile_pic);
//        popup.setOnMenuItemClickListener(this);// to implement on click event on items of menu
//
//        popup.show();
//    }

//    private void uploadImage() {
//
//        if(filePath != null)
//        {
//            final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
//            String picref = UUID.randomUUID().toString();
//            mProfilePicRef = PROFILE_PICS +"/"+ getUid() + picref;
//            final StorageReference ref = FireBaseUsersHelper.getInstance().getStorageReference().child(mProfilePicRef);
//            Bitmap bmp = null;
//            try {
//                bmp = getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), filePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            assert bmp != null;
//            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
//            byte[] data = baos.toByteArray();
//            ref.putBytes(data)
//                    .addOnSuccessListener(taskSnapshot -> {
//                        progressDialog.dismiss();
//                        Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
//
//                        FireBaseUsersHelper.getInstance().attachProfilePic(mProfilePicRef);
////                        attachProfilePic();
//             })
//                    .addOnFailureListener(e -> {
//                        progressDialog.dismiss();
//                        Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    })
//                    .addOnProgressListener(taskSnapshot -> {
//                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
//                                .getTotalByteCount());
//                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
//                    });
//
//
//
//        }
//    }

//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: ");
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
//                && data != null && data.getData() != null) {
//            {
//                Log.d(TAG, "onActivityResult: post image pick");
//                filePath = data.getData();
//                uploadImage();
//            }
//        }
//    }
//
//    private void attachProfilePic() {
//        Picasso.get().load(filePath).resize(400, 400).transform(new CircleTransform()).into(mProfileImage);
//    }


    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }
//
//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        switch (item.getItemId()) {
//            case UPLOAD_IMAGE_ID:
//                Log.d(TAG, "onMenuItemClick: ");
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//                break;
//
//            case DELETE_IMAGE_ID:
//
//                break;
//        }
//        return true;
//    }

//    private void changeMail(String newmail, String pass){
//        mChangeMailProgressBar.setVisibility(View.VISIBLE);
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        assert user != null;
//        AuthCredential credential = EmailAuthProvider
//                .getCredential(Objects.requireNonNull(user.getEmail()).trim(), pass); // Current Login Credentials \\
//        // Prompt the user to re-provide their sign-in credentials
//        user.reauthenticate(credential)
//                .addOnCompleteListener(task -> {
//                    Log.d(TAG, "User re-authenticated.");
//                    //Now change your email address \\
//                    //----------------Code for Changing Email Address----------\\
//                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
//                    user1.updateEmail(newmail.trim())
//                            .addOnCompleteListener(task1 -> {
//                                if (task1.isSuccessful()) {
//                                    Log.d(TAG, "User email address updated.");
//                                    mUser.setEmail(mEmailView.getText().toString().trim());
//                                    FireBaseUsersHelper.getInstance().getDatabase().child(USERS_TABLE).child(getUid()).setValue(mUser);
//                                    mEmailView.setEnabled(false);
//                                }
//                                else {
//                                    Toast.makeText(getContext(), "Autentication failed.", Toast.LENGTH_SHORT).show();
//                                }
//
//                                mChangeMailProgressBar.setVisibility(View.INVISIBLE);
//
//                            });
//                    //----------------------------------------------------------\\
//                });
//
//
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.forum_activity:
                setButtonChecked(mForumTab);
                mCurrentTab = 0;
                swapAdapter();
                break;

            case R.id.store_activity:
                setButtonChecked(mStoreTab);
                mCurrentTab = 1;
                swapAdapter();
                break;

            case R.id.inbox:
                setButtonChecked(mInboxTab);
                mCurrentTab = 2;
                swapAdapter();
                break;
        }
    }

    private void setButtonChecked(TextView mCurrentForum) {
        mForumTab.setBackgroundResource(R.color.transparent);
        mStoreTab.setBackgroundResource(R.color.transparent);
        mInboxTab.setBackgroundResource(R.color.transparent);

        mCurrentForum.setBackgroundResource(R.drawable.underline_cameras);
    }

    @Override
    public void onPostSelected(Post post) {

    }



}


