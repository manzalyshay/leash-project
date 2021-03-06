package com.shaym.leash.ui.home.profile;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
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
import com.shaym.leash.R;
import com.shaym.leash.models.ChatMessage;
import com.shaym.leash.models.Conversation;
import com.shaym.leash.models.Profile;
import com.shaym.leash.data.utils.FireBaseUsersHelper;
import com.shaym.leash.ui.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.shaym.leash.data.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.data.utils.CONSTANT.CONVERSATIONS;

public class ProfileConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "ProfileConversationView";
    private TextView convPartnerName;
    private ImageView convPartnerPic;
    private ProgressBar convPartnerProgressBar;
    private ImageView deleteView;

    private TextView LastMessageView;
    private Fragment mFragment;
   private Profile mConversationPartner;

    ProfileConversationViewHolder(View itemView) {
        super(itemView);
        convPartnerProgressBar = itemView.findViewById(R.id.post_author_photo_progressbar);
        convPartnerPic = itemView.findViewById(R.id.post_author_photo);
        convPartnerName = itemView.findViewById(R.id.post_author);
        LastMessageView = itemView.findViewById(R.id.post_body);
        itemView.findViewById(R.id.comments_amount).setVisibility(View.GONE);
        itemView.findViewById(R.id.comments_postfix).setVisibility(View.GONE);

        itemView.findViewById(R.id.expand_icon).setVisibility(View.GONE);

        itemView.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    void bindToPost(Conversation conversation, Profile conversationPartner, ProfileViewModel profileViewModel, Fragment fragment) {
        mFragment = fragment;
        mConversationPartner = conversationPartner;
        convPartnerName.setText(mConversationPartner.getDisplayname());

        UIHelper.getInstance().attachRoundPic(mConversationPartner.getAvatarurl(), convPartnerPic, convPartnerProgressBar, 100, 100);

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
                    else {
                        LastMessageView.setTypeface(null, Typeface.NORMAL);

                    }

                }            }
        });
    }

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @Override
    public void onClick(View v) {

        FireBaseUsersHelper.getInstance().openChatWindow(mFragment, mConversationPartner.getUid());

    }
}