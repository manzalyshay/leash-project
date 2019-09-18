package com.shaym.leash.ui.home.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.shaym.leash.R;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.user.UsersViewModel;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.home.HomeActivity;
import com.shaym.leash.ui.utils.UIHelper;

import java.util.Date;
import java.util.Objects;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.Manifest.permission.CALL_PHONE;
import static com.shaym.leash.logic.user.UsersViewModel.getUid;
import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.ROLE_STORE_FCS;
import static com.shaym.leash.logic.utils.CONSTANT.ROLE_STORE_INTERSURF;
import static com.shaym.leash.logic.utils.CONSTANT.ROLE_USER;
import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;

@RuntimePermissions
public class ProfileDialog extends DialogFragment implements View.OnClickListener {

    private static final String EXTRA_USER_KEY = "EXTRA_USER_KEY";

    private static final String TAG = "ProfileDialog";
    private ImageView mProfilePic;
    private ProgressBar mProfilePicProgressBar;
    private TextView mDispalyname;
    private View onlineIndicator;

    private ImageView dmpic;
    private ImageView shakepic;
    private ImageView callpic;
    private Profile mClickedUser;
    private Profile mUser;

    private String mUID;
    private String storephonenum;
    private String storewebsite = "";


    public ProfileDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ProfileDialog newInstance(String userId) {
        ProfileDialog frag = new ProfileDialog();
        Bundle args = new Bundle();
        args.putString(EXTRA_USER_KEY, userId);
        frag.setArguments(args);
        return frag;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        assert args != null;
        mUID = args.getString(EXTRA_USER_KEY);

        return inflater.inflate(R.layout.dialog_profile, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initUI(Objects.requireNonNull(getView()));

    }


    @Override
    public void onStart() {
        super.onStart();

        initUsersViewModel();
    }

    private void initUsersViewModel() {
        UsersViewModel viewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
        viewModel.setUserByidRef(FirebaseDatabase.getInstance().getReference()
                .child(USERS_TABLE).child(mUID));
        LiveData<DataSnapshot> clickeduserlivedata = viewModel.getUserByIDLiveData();
        LiveData<DataSnapshot> currentUserLiveData = viewModel.getCurrentUserDataSnapshotLiveData();


        clickeduserlivedata.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "Clicked User Observer: ");
                mClickedUser = dataSnapshot.getValue(Profile.class);
                updateUI();
            }
        });

        currentUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "CurrentUSER Observer: ");
                mUser = dataSnapshot.getValue(Profile.class);
            }
        });


    }

    private void updateUI() {
        UIHelper.getInstance().attachRoundPic(mClickedUser.getAvatarurl(), mProfilePic, mProfilePicProgressBar, 200, 200);
        mDispalyname.setText(mClickedUser.getDisplayname().trim());

        if (!mClickedUser.isOnline()){
            onlineIndicator.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        }

        if (mClickedUser.getUid().equals(getUid())){
            dmpic.setVisibility(View.GONE);
            shakepic.setVisibility(View.GONE);
            callpic.setVisibility(View.GONE);

        }


            switch (mClickedUser.getRole()) {
                case ROLE_STORE_INTERSURF:
                    storephonenum = "+972036820729";
                    storewebsite = "http://intersurf.co.il";
                    break;

                case ROLE_STORE_FCS:
                    storephonenum = "+972086107767";
                    storewebsite = "https://www.fcs.co.il";
                    break;

                case ROLE_USER:
                    storewebsite = "";
                    callpic.setVisibility(View.GONE);
                    break;
            }

    }

    private void initUI(View v) {
        v.findViewById(R.id.closedialogbtn).setOnClickListener(v1 -> dismiss());

        mProfilePic = v.findViewById(R.id.profilepicaroundme);
        mProfilePic.setOnClickListener(this);
        mProfilePicProgressBar = v.findViewById(R.id.profilepic_progressbar_aroundme);
        onlineIndicator = v.findViewById(R.id.profile_online_indicator);
        mDispalyname = v.findViewById(R.id.displaynamearoundme);
        dmpic = v.findViewById(R.id.dm_aroundme);
        dmpic.setOnClickListener(this);
        shakepic = v.findViewById(R.id.shake_user);
        shakepic.setOnClickListener(this);
        callpic = v.findViewById(R.id.call_user);
        callpic.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void makeMeShake(View view) {
        Animation anim = new TranslateAnimation(-5, 5,0,0);
        anim.setDuration(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(5);
        view.startAnimation(anim);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dm_aroundme:
                FireBaseUsersHelper.getInstance().openChatWindow(this, mClickedUser.getUid());
                dismiss();
                break;

            case R.id.shake_user:
                makeMeShake(shakepic);
                HomeActivity activity = (HomeActivity) getActivity();
                assert activity != null;
                String chatKey = FireBasePostsHelper.getInstance().getChatKey(getUid(), mClickedUser.getUid());
                if (!activity.hasChatWith(chatKey)){
                    FireBasePostsHelper.getInstance().addNewConversation(chatKey, getUid(), mClickedUser.getUid(), new Date());
                }

                if (mUser != null) {
                    FireBasePostsHelper.getInstance().postDirectMessage(getString(R.string.shake), mUser, mClickedUser, FirebaseDatabase.getInstance().getReference()
                            .child(CHAT_CONVERSATIONS).child(CONVERSATIONS).child(chatKey));
                }

                break;

            case R.id.call_user:
                ProfileDialogPermissionsDispatcher.callStoreWithPermissionCheck(this);
                break;

            case R.id.profilepicaroundme:
                if (!storewebsite.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(storewebsite));
                    startActivity(browserIntent);
                }
                break;
        }

    }


        @NeedsPermission(CALL_PHONE)
        void callStore() {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + storephonenum));
            Objects.requireNonNull(getActivity()).startActivity(callIntent);
        }

    @OnShowRationale(CALL_PHONE)
    void showRationaleForFineLocation(PermissionRequest request) {
        showRationaleDialog(request);
    }

    private void showRationaleDialog(PermissionRequest request) {
        new AlertDialog.Builder(getContext())
                .setPositiveButton(R.string.ok, (dialog, which) -> request.proceed())
                .setNegativeButton(R.string.cancel, (dialog, which) -> request.cancel())
                .setCancelable(false)
                .setMessage(R.string.phone_permissions)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ProfileDialogPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @OnPermissionDenied(CALL_PHONE)
    void onPhonenDenied() {
        Toast.makeText(getContext(), "Denied", Toast.LENGTH_SHORT).show();
    }


}

