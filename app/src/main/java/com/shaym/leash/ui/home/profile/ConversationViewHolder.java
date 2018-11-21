package com.shaym.leash.ui.home.profile;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.chat.ChatMessage;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.user.Profile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static com.shaym.leash.logic.CONSTANT.USERS_TABLE;
import static tcking.github.com.giraffeplayer2.GiraffePlayer.TAG;

public class ConversationViewHolder extends RecyclerView.ViewHolder {

    public TextView authorView;
    public ImageView authorPic;
    public ProgressBar progressBar;
    public ImageView deleteView;

    public TextView LastMessageView;
    public Profile postProfile;
    private DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageReference;

    public ConversationViewHolder(View itemView) {
        super(itemView);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressBar = itemView.findViewById(R.id.message_author_photo_progressbar);
        authorPic = itemView.findViewById(R.id.message_author_photo);
        authorView = itemView.findViewById(R.id.conv_author);
        deleteView = itemView.findViewById(R.id.delete_conv);
        LastMessageView = itemView.findViewById(R.id.last_message);
    }


    public void bindToPost(ChatMessage message, View.OnClickListener deleteClickListener) {
        authorView.setText(message.author);
        LastMessageView.setText(message.text);
        if (message.uid.equals(getUid())){
            deleteView.setVisibility(View.VISIBLE);
        }
//        deleteView.setOnClickListener(deleteClickListener);

        mDatabase.child(USERS_TABLE).child(message.uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        postProfile = dataSnapshot.getValue(Profile.class);
                        attachPic(postProfile.getAvatarURL());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
        // [END single_value_read]
    }






    public void attachPic(String url){
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
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}