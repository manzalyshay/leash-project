package com.shaym.leash.ui.home.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.shaym.leash.R;
import com.shaym.leash.logic.chat.ChatMessage;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.utils.UIHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final String TAG = "MessageListAdapter";
    private String mChatKey;

    public void setConvPartner(Profile mConvPartner) {
        this.mConvPartner = mConvPartner;
    }

    public void setUser(Profile mUser) {
        this.mUser = mUser;
    }

    private Profile mConvPartner;
    private Profile mUser;
    private List<ChatMessage> data = new ArrayList<>();
    
    MessageListAdapter(String chatKey) {
        mChatKey = chatKey;
    }


    public void setData(List<ChatMessage> newData) {
        if (data != null) {
            PostDiffCallback postDiffCallback = new PostDiffCallback(data, newData);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

            data.clear();
            data.addAll(newData);
            diffResult.dispatchUpdatesTo(this);
        } else {
            // first initialization
            data = newData;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }


    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        ChatMessage message =  data.get(position);

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
        ChatMessage message = data.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
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
            SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");

            timeText.setText(simpleDate.format(message.timesent));

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


        void bind(ChatMessage message) {
            messageText.setText(message.getText());

            SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");

            timeText.setText(simpleDate.format(message.timesent));
            nameText.setText(message.getAuthor());
            if (!message.isread) {
                message.setIsread(true);
                FireBasePostsHelper.getInstance().updateChatMessage(message, mChatKey);

                if (mUser != null) {
                        mUser.setUnreadcounter(mUser.getUnreadcounter() - 1);
                        FireBaseUsersHelper.getInstance().saveUserByID(mUser.getUid(), mUser);

                }
            }

            if (mConvPartner != null) {
                UIHelper.getInstance().attachRoundPic(mConvPartner.getAvatarurl(), profileImage, mProfilePicProgressBar, 100, 100);
            }

        }




    }



    class PostDiffCallback extends DiffUtil.Callback {

        private final List<ChatMessage> oldPosts, newPosts;

        PostDiffCallback(List<ChatMessage> oldPosts, List<ChatMessage> newPosts) {
            this.oldPosts = oldPosts;
            this.newPosts = newPosts;
        }

        @Override
        public int getOldListSize() {
            return oldPosts.size();
        }

        @Override
        public int getNewListSize() {
            return newPosts.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).getKey().equals(newPosts.get(newItemPosition).getKey());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }
}