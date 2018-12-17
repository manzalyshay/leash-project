package com.shaym.leash.ui.home.chat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.FireBaseUsersHelper.BROADCAST_USER;
import static com.shaym.leash.logic.utils.FireBaseUsersHelper.BROADCAST_USER_BY_ID;
import static com.shaym.leash.ui.home.HomeActivity.FROM_UID_KEY;

public class ChatFragment extends DialogFragment implements View.OnClickListener {

    public static final String EXTRA_CHAT_KEY = "chat_key";

    private static final String TAG = "ChatFragment";
    private RecyclerView mMessageRecycler;
    private DatabaseReference mChatReference;
    private EditText mMessageField;
    private Button mSendButton;
    private MessageListAdapter mAdapter;
    private String mChatKey;
    private Profile mUser;
    private Profile mConvPartner;

    private static String mToUid;
    private RequestQueue mRequestQueue;

    public static String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public ChatFragment () {
    }

    public static ChatFragment newInstance(String toUID){
        ChatFragment ChatFragment = new ChatFragment();

        Bundle args = new Bundle();
        mToUid = toUID;
        String key = getUid() + "_" + toUID;
        args.putString(EXTRA_CHAT_KEY, key);
        ChatFragment.setArguments(args);

        return ChatFragment;
    }



    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        Bundle args = getArguments();

        v.setOnTouchListener((v1, event) -> true);

        LocalBroadcastManager.getInstance(v.getContext()).registerReceiver(mUserReceiver,
                new IntentFilter(BROADCAST_USER));

        LocalBroadcastManager.getInstance(v.getContext()).registerReceiver(mConvPartnerReceiver,
                new IntentFilter(BROADCAST_USER_BY_ID));

        LoadUsersData();

        // Get post,forum key from intent
        assert args != null;
        mChatKey = args.getString(EXTRA_CHAT_KEY);

        mRequestQueue = Volley.newRequestQueue(v.getContext());

        if (mChatKey == null ) {
            throw new IllegalArgumentException("Must pass EXTRA_CHAT_KEY");
        }

        mChatReference = FirebaseDatabase.getInstance().getReference()
                .child(CHAT_CONVERSATIONS).child(mChatKey);

        mMessageField = v.findViewById(R.id.edittext_chatbox);
        mSendButton = v.findViewById(R.id.button_chatbox_send);

        mMessageRecycler = v.findViewById(R.id.reyclerview_message_list);
        // use a linear layout manager


        return v;
    }

    public void LoadUsersData() {
        if (mUser == null ||!mUser.getUid().equals(getUid())) {
            FireBaseUsersHelper.getInstance().loadCurrentUserProfile();
        }

        if (mConvPartner == null || !mConvPartner.getUid().equals(mToUid)) {
            FireBaseUsersHelper.getInstance().loadProfileByID(mToUid);
        }


    }

    private BroadcastReceiver mUserReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(TAG + "receiver", "Got mUser message: ");
            Bundle args = intent.getBundleExtra("DATA");

            mUser = (Profile) args.getSerializable("USEROBJ");
            if (mUser!=null && mConvPartner!=null){
                initMessagesView();

            }

        }
    };

    private BroadcastReceiver mConvPartnerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Log.d(TAG + "receiver", "Got ConvPartner message: ");
            Bundle args = intent.getBundleExtra("DATA");

            mConvPartner = (Profile) args.getSerializable("USEROBJ");

            if (mUser!=null && mConvPartner!=null){
                initMessagesView();

            }

        }
    };

    private void initMessagesView() {
        mSendButton.setOnClickListener(this);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));


        mChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "onDataChange:  ChatRef Exists");
                    // Listen for messages
                    setRecyclerAdapter();
                } else {
                    Log.d(TAG, "onDataChange:  Reversing ChatRef Exists");

                    String keysplit[] = mChatKey.split("_");
                    String revkey = keysplit[1] + "_" + keysplit[0];
                    mChatReference = FirebaseDatabase.getInstance().getReference()
                            .child(CHAT_CONVERSATIONS).child(revkey);

                    setRecyclerAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void setRecyclerAdapter(){

        mAdapter = new MessageListAdapter(getActivity(), mChatReference, mConvPartner);
        mMessageRecycler.setAdapter(mAdapter);
        mMessageRecycler.postDelayed(() -> mMessageRecycler.scrollToPosition(mMessageRecycler.getAdapter().getItemCount() - 1), 500);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        super.onPause();
        // Clean up messages listener
        if (mAdapter != null) {
            mAdapter.cleanupListener();
        }
    }

    @Override
    public void onStop() {
        super.onStop();


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_chatbox_send) {
            postMessage();
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        ViewGroup.LayoutParams params = Objects.requireNonNull(getDialog().getWindow()).getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

    }


    private void postMessage() {

        // [START_EXCLUDE]
        if (mUser == null) {
            // User is null, error out
            Log.e(TAG, "User is unexpectedly null");
            Toast.makeText(getActivity(),
                    "Error: could not fetch user.",
                    Toast.LENGTH_SHORT).show();
        } else {

            // Create new message object
            String messageText = mMessageField.getText().toString();
            ChatMessage message = new ChatMessage(mUser.getUid(), mUser.getDisplayname(), messageText, new Date(), false);

            sendNotification(message);
            // Push the message, it will appear in the list
            mChatReference.push().setValue(message);

            // Clear the field
            mMessageField.setText(null);

            mMessageRecycler.postDelayed(() -> mMessageRecycler.scrollToPosition(mMessageRecycler.getAdapter().getItemCount() - 1), 500);

        }
    }


    private void sendNotification(ChatMessage message) {

        JSONObject mainObj = new JSONObject();

        try {
            mainObj.put("to", mConvPartner.getPushtoken());
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", message.author);
            notificationObj.put("body", message.text);
            mainObj.put("notification", notificationObj);
            JSONObject dataObj = new JSONObject();
            dataObj.put(FROM_UID_KEY, message.uid);
            mainObj.put("data", dataObj);

            String PUSH_URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, PUSH_URL, mainObj, response -> Log.d(TAG, "onResponse: Success"), error -> Log.d(TAG, "sendNotification: onError"))
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


}

