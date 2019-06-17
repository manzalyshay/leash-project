package com.shaym.leash.ui.home.chat;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shaym.leash.R;
import com.shaym.leash.logic.chat.ChatMessage;
import com.shaym.leash.logic.chat.ChatViewModel;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.user.UsersViewModel;
import com.shaym.leash.logic.utils.FireBasePostsHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;
import static com.shaym.leash.ui.home.HomeActivity.FROM_UID_KEY;

public class ChatActivity extends FragmentActivity implements View.OnClickListener, TextWatcher {

    public static final String EXTRA_PARTNER_KEY = "EXTRA_PARTNER_KEY";

    private static final String TAG = "ChatFragment";
    private RecyclerView mMessageRecycler;
    private DatabaseReference mChatReference;
    private EditText mMessageField;
    private Button mSendButton;
    private MessageListAdapter mAdapter;
    private ImageView mBackBtn;
    private ImageView mProfilePic;
    private ProgressBar mProfilePicProgressBar;
    private TextView mDispalyname;
    private String mChatKey;
    private Profile mUser;
    private Profile mConvPartner;
    private boolean firstTimeCheck;

    private static String mToUid;
    private RequestQueue mRequestQueue;
    boolean isKeyboardShowing = false;


    public static String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up,  R.anim.no_anim); // remember to put it after startActivity, if you put it to above, animation will not working

        Bundle b = getIntent().getExtras();

        if(b != null) {
            mToUid = b.getString(EXTRA_PARTNER_KEY);
        }

        mChatKey = FireBasePostsHelper.getInstance().getChatKey(getUid(), mToUid);
        setContentView(R.layout.activity_chat);

        mRequestQueue = Volley.newRequestQueue(this);

        if (mChatKey == null ) {
            throw new IllegalArgumentException("Must pass EXTRA_PARTNER_KEY");
        }

        mChatReference = FirebaseDatabase.getInstance().getReference()
                .child(CHAT_CONVERSATIONS).child(CONVERSATIONS).child(mChatKey);

        initUI();

    }


    @Override
    public void onBackPressed() {
        if (isKeyboardShowing){
            super.onBackPressed();
        }
        else {
            finish();
            overridePendingTransition(R.anim.no_anim, R.anim.slide_down);
        }
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

    private void initUI() {
        mMessageField = findViewById(R.id.edittext_chatbox);
        mMessageField.addTextChangedListener(this);
        mSendButton = findViewById(R.id.button_chatbox_send);
        mMessageRecycler = findViewById(R.id.message_list);
        mBackBtn = findViewById(R.id.back_icon_toolbar);
        mBackBtn.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.no_anim, R.anim.slide_down);
        });

        mProfilePic = findViewById(R.id.profile_icon_toolbar);
        mProfilePicProgressBar = findViewById(R.id.profile_icon_progressbar);
        mDispalyname = findViewById(R.id.display_name);
        mSendButton.setOnClickListener(this);

        initChatAdapter();
        initKeyboardListener();

    }

    private void initChatAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setStackFromEnd(true);
//        layoutManager.setReverseLayout(true);
        mMessageRecycler.setLayoutManager(layoutManager);
        // use a linear layout manager
        mAdapter = new MessageListAdapter();
        mMessageRecycler.setAdapter(mAdapter);
    }

    private void initKeyboardListener() {
        RelativeLayout rootlayout = findViewById(R.id.chat_root_view);

// ContentView is the root view of the layout of this activity/fragment
        rootlayout.getViewTreeObserver().addOnGlobalLayoutListener(
                () -> {

                    Rect r = new Rect();
                    rootlayout.getWindowVisibleDisplayFrame(r);
                    int screenHeight = rootlayout.getRootView().getHeight();

                    // r.bottom is the position above soft keypad or device button.
                    // if keypad is shown, the r.bottom is smaller than that before.
                    int keypadHeight = screenHeight - r.bottom;

                    Log.d(TAG, "keypadHeight = " + keypadHeight);

                    if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                        // keyboard is opened
                        if (!isKeyboardShowing) {
                            isKeyboardShowing = true;
                        }
                    }
                    else {
                        // keyboard is closed
                        if (isKeyboardShowing) {
                            isKeyboardShowing = false;
                        }
                    }
                });
    }


    private void setConvPartnerUI() {

        mDispalyname.setText(mConvPartner.getDisplayname());
        FireBasePostsHelper.getInstance().attachRoundPic(mConvPartner.getAvatarurl(), mProfilePic, mProfilePicProgressBar, 100, 100);
        
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

            if (!firstTimeCheck){
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child(CHAT_CONVERSATIONS).child(CONVERSATIONS);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild(mChatKey)){
                            FireBasePostsHelper.getInstance().addNewConversation(mChatKey, getUid(), mToUid, mChatKey, new Date());
                        }
                        firstTimeCheck=true;
                        postMessage();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else {
                postMessage();
            }
        }

        else if (v.getId() == R.id.back_icon_toolbar){
        }
    }



    private void postMessage() {
            // Create new message object
            String messageText = mMessageField.getText().toString();

            if (!messageText.isEmpty()) {
                ChatMessage message = new ChatMessage(mUser.getUid(),mChatReference.push().getKey(), mUser.getDisplayname(), messageText, new Date(), false);

                sendNotification(message);
                // Push the message, it will appear in the list
                mChatReference.push().setValue(message);
            }

            // Clear the field
            mMessageField.setText(null);

            mMessageRecycler.postDelayed(() -> mMessageRecycler.scrollToPosition(mMessageRecycler.getAdapter().getItemCount() - 1), 500);
    }


    private void sendNotification(ChatMessage message) {

        JSONObject mainObj = new JSONObject();

        try {

            JSONObject dataObj = new JSONObject();

            dataObj.put(FROM_UID_KEY, message.uid);
            dataObj.put("author", message.author);
            dataObj.put("body", message.text);
            mainObj.put("to", mConvPartner.getPushtoken());
            mainObj.put("priority", "high");

            mainObj.put("data", dataObj);


            String PUSH_URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PUSH_URL, mainObj, response -> Log.d(TAG, "onResponse: Success"), error -> Log.e(TAG,  error.toString()))
            {
                @Override
                public Map<String, String> getHeaders() {

                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("Authorization", "key=AIzaSyDtwfuXPakP3Z6c_uP5aG56tbXOyY6c6YQ");
                    return header;
                }
            };

            mRequestQueue.add(request);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

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

