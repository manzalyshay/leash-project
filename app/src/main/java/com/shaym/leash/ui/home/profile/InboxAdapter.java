package com.shaym.leash.ui.home.profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shaym.leash.R;
import com.shaym.leash.logic.chat.ChatMessage;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.ui.home.chat.ChatFragment;

import java.util.List;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.CHAT_CONVERSATIONS;
import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;

public class InboxAdapter extends RecyclerView.Adapter<ConversationViewHolder> {
    private List<String> mUserConversations;
    private DatabaseReference mRootRef;
    private FragmentManager mFM;
    private Profile mConversationPartner;

    InboxAdapter(List<String> myDataset, FragmentManager fm) {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserConversations = myDataset;
        mFM = fm;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inbox, parent, false);
        return new ConversationViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {

        String split[] = mUserConversations.get(position).split("_");
        String mClickedUserID;
        if (split[0].equals(getUid())){
            mClickedUserID = split[1];
        }
        else{
            mClickedUserID = split[0];
        }

        holder.itemView.setOnClickListener(view -> {

            ChatFragment cf = ChatFragment.newInstance(holder.mConversationPartner.getUid());


            cf.show(mFM, "fragment_chat");
        });


        mRootRef.child(USERS_TABLE).child(mClickedUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mConversationPartner = dataSnapshot.getValue(Profile.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference mConversationsRef = mRootRef.child(CHAT_CONVERSATIONS).child(mUserConversations.get(position));
        Query lastQuery = mConversationsRef.limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : Objects.requireNonNull(dataSnapshot).getChildren()) {
                    ChatMessage message = child.getValue(ChatMessage.class);
                    holder.bindToPost(message, mConversationPartner);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Handle possible errors.
            }
        });





    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUserConversations.size();
    }

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }
}