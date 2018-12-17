package com.shaym.leash.ui.home.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.chat.ChatMessage;
import com.shaym.leash.logic.user.Profile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final String TAG = "MessageListAdapter";
    private StorageReference storageReference;
    private Profile mConvPartner;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private List<String> mChatMessagesIds = new ArrayList<>();
    private List<ChatMessage> mChatMessages = new ArrayList<>();
    private WeakReference<Activity> mContext;

    void cleanupListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }
    
    MessageListAdapter(Activity activity, DatabaseReference ref, Profile convpartner) {
        storageReference = FirebaseStorage.getInstance().getReference();
        mContext = new WeakReference<>(activity);
        mDatabaseReference = ref;
        mConvPartner = convpartner;
        initMessagesListener();

    }

    private void initMessagesListener() {

        // Create child event listener
        // [START child_event_listener_recycler]

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new message has been added, add it to the displayed list
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);

                // [START_EXCLUDE]
                // Update RecyclerView
                mChatMessagesIds.add(dataSnapshot.getKey());
                mChatMessages.add(message);
                notifyItemInserted(mChatMessages.size() - 1);
                // [END_EXCLUDE]
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A message has changed, use the key to determine if we are displaying this
                // message and if so displayed the changed message.
                ChatMessage newChatMessage = dataSnapshot.getValue(ChatMessage.class);
                String messageKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int messageIndex = mChatMessagesIds.indexOf(messageKey);
                if (messageIndex > -1) {
                    // Replace with the new data
                    mChatMessages.set(messageIndex, newChatMessage);

                    // Update the RecyclerView
                    notifyItemChanged(messageIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + messageKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A message has changed, use the key to determine if we are displaying this
                // message and if so remove it.
                String messageKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int messageIndex = mChatMessagesIds.indexOf(messageKey);
                if (messageIndex > -1) {
                    // Remove data from the list
                    mChatMessagesIds.remove(messageIndex);
                    mChatMessages.remove(messageIndex);

                    // Update the RecyclerView
                    notifyItemRemoved(messageIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + messageKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A message has changed position, use the key to determine if we are
                // displaying this message and if so move it.
                ChatMessage movedChatMessage = dataSnapshot.getValue(ChatMessage.class);
                String messageKey = dataSnapshot.getKey();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postChatMessages:onCancelled", databaseError.toException());
                Activity activity = mContext.get();
                if (activity != null) {
                    Toast.makeText(activity, "Failed to load messages.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        mDatabaseReference.addChildEventListener(mChildEventListener);
        // [END child_event_listener_recycler]

    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }


    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        ChatMessage message =  mChatMessages.get(position);

        if (message.getUid().equals(getUid())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = mChatMessages.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message, position);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView readIcon;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);

            timeText = itemView.findViewById(R.id.text_message_time);

            readIcon = itemView.findViewById(R.id.read_icon);
        }

        void bind(ChatMessage message) {
            messageText.setText(message.getText());
            // Format the stored timestamp into a readable String using method.
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm");
// give a timezone reference for formatting (see comment at the bottom)
            String formattedDate = sdf.format(message.getTimesent());
            timeText.setText(formattedDate);

            if (message.getIsread()){
                readIcon.setVisibility(View.VISIBLE);
            }
            else {
                readIcon.setVisibility(View.INVISIBLE);

            }
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;
        ProgressBar mProfilePicProgressBar;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
            mProfilePicProgressBar = itemView.findViewById(R.id.profilepic_progressbar_message);

        }


        void bind(ChatMessage message, int position) {
            messageText.setText(message.getText());
            LoadUserPic();
            // Format the stored timestamp into a readable String using method.
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm");
// give a timezone reference for formatting (see comment at the bottom)
            String formattedDate = sdf.format(message.getTimesent());
            timeText.setText(formattedDate);

            nameText.setText(message.getAuthor());

            message.setIsread(true);
            mDatabaseReference.child(mChatMessagesIds.get(position)).setValue(message);

        }

        private void LoadUserPic() {
            Log.d(TAG, "LoadUserPic: ");
            if (!mConvPartner.getAvatarURL().isEmpty()) {

                if (mConvPartner.getAvatarURL().charAt(0) == 'p') {
                    storageReference.child(mConvPartner.getAvatarURL()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(400, 400).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            mProfilePicProgressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(uri).resize(400, 400).centerCrop().transform(new CircleTransform()).into(profileImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mProfilePicProgressBar.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    //Try again online if cache failed

                                    mProfilePicProgressBar.setVisibility(View.INVISIBLE);

                                }
                            });

                        }

                    }));
                }
                else {
                    Picasso.get().load(Uri.parse(mConvPartner.getAvatarURL())).resize(400, 400).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            mProfilePicProgressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(Uri.parse(mConvPartner.getAvatarURL())).resize(400, 400).centerCrop().transform(new CircleTransform()).into(profileImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    mProfilePicProgressBar.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    //Try again online if cache failed

                                    mProfilePicProgressBar.setVisibility(View.INVISIBLE);

                                }
                            });

                        }

                    });
                }
            }
            else {
                mProfilePicProgressBar.setVisibility(View.INVISIBLE);

            }

        }


    }




}