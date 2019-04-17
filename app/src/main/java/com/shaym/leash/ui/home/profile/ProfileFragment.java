package com.shaym.leash.ui.home.profile;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.authentication.LoginActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Images.Media.getBitmap;
import static com.shaym.leash.logic.utils.CONSTANT.PROFILE_PICS;
import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;
import static com.shaym.leash.logic.utils.CONSTANT.USER_BUNDLE;
import static com.shaym.leash.logic.utils.CONSTANT.USER_GEAR_POSTS;
import static com.shaym.leash.logic.utils.CONSTANT.USER_OBJ;
import static com.shaym.leash.logic.utils.CONSTANT.USER_POSTS;
import static com.shaym.leash.logic.utils.FireBaseUsersHelper.BROADCAST_USER;

/**
 * Created by shaym on 2/17/18.
 */


public class ProfileFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "ProfileFragment";
    //UI Objects
    private ImageView mProfileImage;

    private EditText mPhoneNumView;
    private EditText mEmailView;
    private EditText mPassVerf;

    private TextView mDisplayName;
    private TextView mUserForumPostsAmount;
    private TextView mUserGearPostsAmount;
    private TextView mInboxMsgsAmount;

    private CheckBox isPhoneNumHidden;
    private CheckBox isEmailHidden;

    private ProgressBar mProfilePicProgressBar;
    private ProgressBar mChangeMailProgressBar;

    private UserPostsFragment mUsersPostsFragment;
//    private UserGearPostsFragment mUsersGearPostsFragment;
    private UserInboxFragment mUserInboxFragment;

    private final static int UPLOAD_IMAGE_ID = 01;
    private final static int DELETE_IMAGE_ID = 02;

    private String mProfilePicRef;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    public ProfileFragment instance;
    private Profile mUser;
    private AlertDialog mChangeMailDialog;

    public ProfileFragment(){
        instance = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        initUI();

        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext())).registerReceiver(mUserReceiver,
                new IntentFilter(BROADCAST_USER));
        FireBaseUsersHelper.getInstance().loadCurrentUserProfile();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getContext())).unregisterReceiver(mUserReceiver);

    }

    private void initUI() {
        //Fragments

        View v = getView();
        mUsersPostsFragment =  new UserPostsFragment();
//        mUsersGearPostsFragment = new UserGearPostsFragment();
        mUserInboxFragment = new UserInboxFragment();

        assert v != null;
        mProfileImage = v.findViewById(R.id.profilepic);
        mProfilePicProgressBar = v.findViewById(R.id.profilepic_progressbar);
        mChangeMailProgressBar = v.findViewById(R.id.changemail_progressbar);

        mDisplayName = v.findViewById(R.id.displayname);
        mPhoneNumView = v.findViewById(R.id.userphonenum);
        mPhoneNumView.setEnabled(false);
        mEmailView = v.findViewById(R.id.useremail);
        mEmailView.setEnabled(false);

        mUserForumPostsAmount = v.findViewById(R.id.userpostsamont);
        LinearLayout mUserForumPostsLayout = v.findViewById(R.id.userpostslayout);
        mUserForumPostsLayout.setOnClickListener(this);

        mUserGearPostsAmount = v.findViewById(R.id.gearpostsamount);
        LinearLayout mUserGearPostsLayout = v.findViewById(R.id.gearpostslayout);

        mUserGearPostsLayout.setOnClickListener(this);

        mInboxMsgsAmount = v.findViewById(R.id.incomingmessagesamount);
        LinearLayout mInboxMsgsLayout = v.findViewById(R.id.incomingmessageslayout);

        mInboxMsgsLayout.setOnClickListener(this);

        mProfileImage.setOnClickListener(this);

        ImageButton mEditEmailBtn = v.findViewById(R.id.email_edit_btn);
        mEditEmailBtn.setOnClickListener(this);

        ImageButton mEditPhoneNumBtn = v.findViewById(R.id.phone_edit_button);
        mEditPhoneNumBtn.setOnClickListener(this);

        isEmailHidden = v.findViewById(R.id.email_hidden_checkbox);
        isEmailHidden.setOnCheckedChangeListener(this);

        isPhoneNumHidden = v.findViewById(R.id.phone_hidden_checkbox);
        isPhoneNumHidden.setOnCheckedChangeListener(this);

        initchangeEmailDialog();


    }

    public void LoadUserData() {
        if (mUser == null ||!mUser.getUid().equals(getUid())) {
            FireBaseUsersHelper.getInstance().loadCurrentUserProfile();
            FireBaseUsersHelper.getInstance().loadUserProfileData();

        }
        else {
            updateUI();
        }

    }

    private BroadcastReceiver mUserReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(TAG + "receiver", "Got message: ");
            Bundle args = intent.getBundleExtra(USER_BUNDLE);

            mUser = (Profile) args.getSerializable(USER_OBJ);
            FireBaseUsersHelper.getInstance().loadUserProfileData();
            updateUI();

        }
    };

    private void updateUI() {
        FireBaseUsersHelper.getInstance().LoadUserPic(mUser.getAvatarURL(), mProfileImage, mProfilePicProgressBar, 400);
        mDisplayName.setText(mUser.getDisplayname());
        mPhoneNumView.setText(mUser.getPhonenumber());
        mEmailView.setText(mUser.getEmail());
        isEmailHidden.setChecked(!mUser.isIsemailhidden());
        isPhoneNumHidden.setChecked(!mUser.isIsphonehidden());

        mUserForumPostsAmount.setText(String.valueOf(mUser.getForumpostsamount()));
        mUserGearPostsAmount.setText(String.valueOf(mUser.getgearpostsamount()));

        mInboxMsgsAmount.setText(String.valueOf(mUser.getConversationssmount()));
        if (mUser.getUnreadmsgsamount() >0){
            mInboxMsgsAmount.setTextColor(Color.RED);
        }

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.profilepic:
            showProfilePicMenu(view);
            break;

            case R.id.userpostslayout:
                if (mUser == null || mUser.getForumpostsamount() ==0) {
                    Toast.makeText(getActivity(), "No Posts For User", Toast.LENGTH_SHORT).show();
                }
                else {
                    assert getFragmentManager() != null;
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                    transaction.replace(R.id.root_frame_profile_fragment, mUsersPostsFragment, USER_POSTS);

                    transaction.addToBackStack(null);

                    transaction.commit();
                }
                break;



            case R.id.gearpostslayout:
//                if (mUser == null || mUser.getgearpostsamount() ==0) {
//                    Toast.makeText(getActivity(), "No Gear Posts For User", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    assert getFragmentManager() != null;
//                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//// Replace whatever is in the fragment_container view with this fragment,
//// and add the transaction to the back stack if needed
//                    transaction.replace(R.id.root_frame_profile_fragment, mUsersGearPostsFragment, USER_GEAR_POSTS);
//
//                    transaction.addToBackStack(null);
//
//                    transaction.commit();
//                }
                break;

            case R.id.incomingmessageslayout:
                if (mUser == null || mUser.getConversationssmount() ==0) {
                    Toast.makeText(getActivity(), "No Inbox Messages For User", Toast.LENGTH_SHORT).show();
                }
                else {
                    assert getFragmentManager() != null;
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                    transaction.replace(R.id.root_frame_profile_fragment, mUserInboxFragment, "");

                    transaction.addToBackStack(null);

                    transaction.commit();
                }
                break;

            case R.id.email_edit_btn:
                if (!mEmailView.isEnabled()) {
                    mEmailView.setEnabled(true);
                    mEmailView.requestFocus();
                }
                else{
                    if(LoginActivity.isEmailValid(mEmailView.getText().toString().trim())) {
                        mChangeMailDialog.show();
                        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
                        mChangeMailDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                            if (!mPassVerf.getText().toString().isEmpty())
                            {
                                changeMail(mEmailView.getText().toString().trim(), mPassVerf.getText().toString().trim());
                                mChangeMailDialog.dismiss();
                            }
                            else
                            {
                                Toast.makeText(getContext(), R.string.enter_password_title, Toast.LENGTH_LONG).show();

                            }
                        });

                    }
                    else {
                        Toast.makeText(getContext(), R.string.enter_valid_email_message, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.phone_edit_button:
                if (!mPhoneNumView.isEnabled()) {
                    mPhoneNumView.setEnabled(true);
                    mPhoneNumView.requestFocus();
                }
                else{
                    if (mPhoneNumView.getText().toString().length() == 10) {
                        mPhoneNumView.setEnabled(false);
                        mUser.setPhonenumber(mPhoneNumView.getText().toString());
                        FireBaseUsersHelper.getInstance().getDatabase().child(USERS_TABLE).child(getUid()).setValue(mUser);
                    }
                    else {
                        Toast.makeText(getContext(), "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


    private void showProfilePicMenu(View view) {

        PopupMenu popup = new PopupMenu(Objects.requireNonNull(this.getContext()), view);
        popup.getMenu().add(0, UPLOAD_IMAGE_ID, 0, R.string.change_profile_pic);
        popup.getMenu().add(0, DELETE_IMAGE_ID, 1, R.string.delete_profile_pic);
        popup.setOnMenuItemClickListener(this);// to implement on click event on items of menu

        popup.show();
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this.getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String picref = UUID.randomUUID().toString();
            mProfilePicRef = PROFILE_PICS +"/"+ getUid() + picref;
            final StorageReference ref = FireBaseUsersHelper.getInstance().getStorageReference().child(mProfilePicRef);
            Bitmap bmp = null;
            try {
                bmp = getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), filePath);
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
                        Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();

                        FireBaseUsersHelper.getInstance().attachProfilePic(mProfilePicRef);
                        attachProfilePic();
             })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });



        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            {
                Log.d(TAG, "onActivityResult: post image pick");
                filePath = data.getData();
                uploadImage();
            }
        }
    }

    private void attachProfilePic() {
        Picasso.get().load(filePath).resize(400, 400).transform(new CircleTransform()).into(mProfileImage);
    }


    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case UPLOAD_IMAGE_ID:
                Log.d(TAG, "onMenuItemClick: ");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                break;

            case DELETE_IMAGE_ID:

                break;
        }
        return true;
    }

    private void changeMail(String newmail, String pass){
        mChangeMailProgressBar.setVisibility(View.VISIBLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        AuthCredential credential = EmailAuthProvider
                .getCredential(Objects.requireNonNull(user.getEmail()).trim(), pass); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "User re-authenticated.");
                    //Now change your email address \\
                    //----------------Code for Changing Email Address----------\\
                    FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                    user1.updateEmail(newmail.trim())
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d(TAG, "User email address updated.");
                                    mUser.setEmail(mEmailView.getText().toString().trim());
                                    FireBaseUsersHelper.getInstance().getDatabase().child(USERS_TABLE).child(getUid()).setValue(mUser);
                                    mEmailView.setEnabled(false);
                                }
                                else {
                                    Toast.makeText(getContext(), "Autentication failed.", Toast.LENGTH_SHORT).show();
                                }

                                mChangeMailProgressBar.setVisibility(View.INVISIBLE);

                            });
                    //----------------------------------------------------------\\
                });


    }

    private void initchangeEmailDialog(){

        mPassVerf = new EditText(getActivity());
        mPassVerf.setTransformationMethod(PasswordTransformationMethod.getInstance());
        AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        alert.setMessage(R.string.password_verify_changemail);
        alert.setTitle(R.string.enter_password_title);

        alert.setView(mPassVerf);

        alert.setPositiveButton(R.string.ok, (dialog, whichButton) -> {


        });

        alert.setNegativeButton(R.string.cancel, (dialog, whichButton) -> {
            // what ever you want to do with No option.
            mEmailView.setEnabled(false);
        });

        mChangeMailDialog = alert.create();


    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (mUser != null) {
            switch (compoundButton.getId()) {
                case R.id.email_hidden_checkbox:
                    mUser.setIsemailhidden(!b);
                    break;
                case R.id.phone_hidden_checkbox:
                    mUser.setIsphonehidden(!b);
                    break;
            }

            FireBaseUsersHelper.getInstance().getDatabase().child(USERS_TABLE).child(getUid()).setValue(mUser);
        }
    }

}


