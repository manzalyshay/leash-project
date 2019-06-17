package com.shaym.leash.ui.home.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.chat.ChatMessage;
import com.shaym.leash.logic.chat.Conversation;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.ui.home.chat.ChatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.CONVERSATIONS;

public class ProfileConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "ProfileConversationView";
    private TextView convPartnerName;
    private ImageView convPartnerPic;
    private ProgressBar convPartnerProgressBar;
    private ImageView deleteView;

    private TextView LastMessageView;
    private StorageReference storageReference;
    private Fragment mFragment;
   private Profile mConversationPartner;

    ProfileConversationViewHolder(View itemView) {
        super(itemView);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        convPartnerProgressBar = itemView.findViewById(R.id.post_author_photo_progressbar);
        convPartnerPic = itemView.findViewById(R.id.post_author_photo);
        convPartnerName = itemView.findViewById(R.id.post_author);
        LastMessageView = itemView.findViewById(R.id.post_body);

        itemView.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    void bindToPost(Conversation conversation, Profile conversationPartner, ProfileViewModel profileViewModel, Fragment fragment) {
        mFragment = fragment;
        mConversationPartner = conversationPartner;
        convPartnerName.setText(mConversationPartner.getDisplayname());

        FireBasePostsHelper.getInstance().attachRoundPic(mConversationPartner.getAvatarurl(), convPartnerPic, convPartnerProgressBar, 100, 100);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference lastQuery = databaseReference.child(CHAT_CONVERSATIONS).child(CONVERSATIONS).child(conversation.conversationID);
        setConversationObserver(profileViewModel, lastQuery);

    }

    private void setConversationObserver(ProfileViewModel profileViewModel, DatabaseReference conversation) {
        profileViewModel.setCONVERSATION_LIVE_DATA(conversation);

        LiveData<DataSnapshot> conversationLiveData = profileViewModel.getConversationLiveData();

        conversationLiveData.observe(mFragment, dataSnapshot -> {
            if (dataSnapshot != null) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    List<ChatMessage> messageList = new ArrayList<>();
                    ChatMessage message = ds.getValue(ChatMessage.class);
                    messageList.add(message);
                    ChatMessage lastmsg = messageList.get(messageList.size()-1);
                    LastMessageView.setText(lastmsg.text);
                    if (!lastmsg.isread && !lastmsg.uid.equals(getUid())){
                        LastMessageView.setTypeface(null, Typeface.BOLD);

                    }
                }            }
        });
    }

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(mFragment.getActivity(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString(ChatActivity.EXTRA_PARTNER_KEY, mConversationPartner.getUid());
        intent.putExtras(b); //Put your id to your next Intent
        Objects.requireNonNull(mFragment.getActivity()).startActivity(intent);

    }
}