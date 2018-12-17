package com.shaym.leash.ui.home.profile;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.chat.ChatMessage;
import com.shaym.leash.logic.user.Profile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ConversationViewHolder extends RecyclerView.ViewHolder {

    private TextView authorView;
    private ImageView authorPic;
    private ProgressBar progressBar;
    private ImageView deleteView;

    private TextView LastMessageView;
    private StorageReference storageReference;
   Profile mConversationPartner;

    ConversationViewHolder(View itemView) {
        super(itemView);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressBar = itemView.findViewById(R.id.message_author_photo_progressbar);
        authorPic = itemView.findViewById(R.id.message_author_photo);
        authorView = itemView.findViewById(R.id.conv_author);
        deleteView = itemView.findViewById(R.id.delete_conv);
        LastMessageView = itemView.findViewById(R.id.last_message);
    }


    @SuppressLint("SetTextI18n")
    void bindToPost(ChatMessage message, Profile conversationPartner) {
        mConversationPartner = conversationPartner;
        authorView.setText(conversationPartner.getDisplayname());
        LastMessageView.setText(message.author + ": " + message.text);
        deleteView.setVisibility(View.VISIBLE);
//        deleteView.setOnClickListener(deleteClickListener);
        attachPic(conversationPartner.getAvatarURL());
    }

    private void attachPic(String url){
        if (!url.isEmpty()) {
            if (url.charAt(0) == 'p') {
                        storageReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(100, 100).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(authorPic, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(uri).resize(100, 100).centerCrop().transform(new CircleTransform()).into(authorPic, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed

                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    }

                }));
            } else {
                Picasso.get().load(Uri.parse(url)).resize(100, 100).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(authorPic, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(Uri.parse(url)).resize(100, 100).centerCrop().transform(new CircleTransform()).into(authorPic, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed

                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });

                    }

                });
            }
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);

        }
    }

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }


}