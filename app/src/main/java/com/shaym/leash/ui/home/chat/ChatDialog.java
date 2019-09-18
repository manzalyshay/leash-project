package com.shaym.leash.ui.home.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shaym.leash.R;
import com.shaym.leash.logic.chat.ChatMessage;
import com.shaym.leash.logic.chat.ChatViewModel;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.user.UsersViewModel;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.ui.home.HomeActivity;
import com.shaym.leash.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;

public class
ChatDialog extends DialogFragment implements View.OnClickListener, TextWatcher {

    public static final String EXTRA_PARTNER_KEY = "EXTRA_PARTNER_KEY";

    private static final String TAG = "ChatFragment";
    private RecyclerView mMessageRecycler;
    private DatabaseReference mChatReference;
    private EditText mMessageField;
    private Button mSendButton;
    private MessageListAdapter mAdapter;
    private ImageView mProfilePic;
    private ProgressBar mProfilePicProgressBar;
    private TextView mDispalyname;
    private String mChatKey;
    private Profile mUser;
    private Profile mConvPartner;
    private boolean firstTimeCheck;
    private String mToUid;

    public ChatDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ChatDialog newInstance(String mToUid) {
        ChatDialog frag = new ChatDialog();
        Bundle args = new Bundle();
        args.putString(EXTRA_PARTNER_KEY, mToUid);
        frag.setArguments(args);
        return frag;
    }

    public static String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        assert args != null;
        mToUid = args.getString(EXTRA_PARTNER_KEY);
        mChatKey = FireBasePostsHelper.getInstance().getChatKey(getUid(), mToUid);
        mChatReference = FirebaseDatabase.getInstance().getReference()
                .child(CHAT_CONVERSATIONS).child(CONVERSATIONS).child(mChatKey);

        return inflater.inflate(R.layout.dialog_chat, container);
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
        initChatViewModel();
    }

    private void initUsersViewModel() {
        UsersViewModel viewModel = ViewModelProviders.of(this).get(UsersViewModel.class);
        viewModel.setUserByidRef(FirebaseDatabase.getInstance().getReference()
                .child(USERS_TABLE).child(mToUid));
        LiveData<DataSnapshot> currentUserLiveData = viewModel.getCurrentUserDataSnapshotLiveData();
        LiveData<DataSnapshot> convPartnerUserLiveData = viewModel.getUserByIDLiveData();

        currentUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "initUsersViewModel: ");
                mUser = dataSnapshot.getValue(Profile.class);
                mAdapter.setUser(mUser);
            }
        });

        convPartnerUserLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                Log.d(TAG, "initUsersViewModel: ");
                mConvPartner = dataSnapshot.getValue(Profile.class);
                mAdapter.setConvPartner(mConvPartner);
                setConvPartnerUI();
            }
        });
    }

    private void initUI(View v) {
        mMessageField = v.findViewById(R.id.edittext_chatbox);
        mMessageField.addTextChangedListener(this);
        mSendButton = v.findViewById(R.id.button_chatbox_send);
        mMessageRecycler = v.findViewById(R.id.message_list);
        v.findViewById(R.id.close_icon_toolbar).setOnClickListener(v1 -> dismiss());

        mProfilePic = v.findViewById(R.id.profile_icon_toolbar);
        mProfilePicProgressBar = v.findViewById(R.id.profile_icon_progressbar);
        mDispalyname = v.findViewById(R.id.display_name);
        mSendButton.setOnClickListener(this);

        initChatAdapter();

    }

    private void initChatAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setStackFromEnd(true);
//        layoutManager.setReverseLayout(true);
        mMessageRecycler.setLayoutManager(layoutManager);
        // use a linear layout manager
        mAdapter = new MessageListAdapter(mChatKey);
        mMessageRecycler.setAdapter(mAdapter);
    }




    private void setConvPartnerUI() {
        mDispalyname.setText(mConvPartner.getDisplayname());
        UIHelper.getInstance().attachRoundPic(mConvPartner.getAvatarurl(), mProfilePic, mProfilePicProgressBar, 100, 100);
    }

    private void initChatViewModel() {
        ChatViewModel viewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        viewModel.setCHAT_LIVE_DATA(mChatReference);

        LiveData<DataSnapshot> chatLiveData = viewModel.getChatLiveData();
        chatLiveData.observe(this, dataSnapshot -> {
            if (dataSnapshot != null) {
                List<ChatMessage> newData = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ChatMessage message = ds.getValue(ChatMessage.class);
                    newData.add(message);
                }
                mAdapter.setData(newData);
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_chatbox_send) {

            if (!firstTimeCheck) {
                HomeActivity activity = (HomeActivity) getActivity();
                assert activity != null;
                if (!activity.hasChatWith(mToUid)) {
                    FireBasePostsHelper.getInstance().addNewConversation(mChatKey, getUid(), mToUid, new Date());
                }
                firstTimeCheck = true;
                postMessage();
            }

            else{
                postMessage();
                }
            }
        }


    private void postMessage() {
        String messageText = mMessageField.getText().toString().trim();

        FireBasePostsHelper.getInstance().postDirectMessage(messageText, mUser, mConvPartner, mChatReference);
        // Clear the field
        mMessageField.setText(null);

        mMessageRecycler.postDelayed(() -> mMessageRecycler.scrollToPosition(Objects.requireNonNull(mMessageRecycler.getAdapter()).getItemCount() - 1), 500);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (count > 0)
            mSendButton.setEnabled(true);
        else
            mSendButton.setEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}

