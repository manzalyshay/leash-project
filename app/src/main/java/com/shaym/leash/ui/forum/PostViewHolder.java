package com.shaym.leash.ui.forum;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.user.Profile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static com.shaym.leash.logic.CONSTANT.USERS_TABLE;
import static tcking.github.com.giraffeplayer2.GiraffePlayer.TAG;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public ImageView authorPic;
    public ProgressBar progressBar;
    public ImageView starView;
    public ImageView deleteView;
    public TextView numStarsView;
    public TextView bodyView;
    public Profile postProfile;
    private DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageReference;

    public PostViewHolder(View itemView) {
        super(itemView);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressBar = itemView.findViewById(R.id.post_author_photo_progressbar);
        authorPic = itemView.findViewById(R.id.post_author_photo);
        titleView = itemView.findViewById(R.id.post_title);
        authorView = itemView.findViewById(R.id.post_author);
        starView = itemView.findViewById(R.id.star);
        deleteView = itemView.findViewById(R.id.delete);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
        bodyView = itemView.findViewById(R.id.post_body);
    }


    public void bindToPost(Post post, View.OnClickListener starClickListener, View.OnClickListener deleteClickListener) {
        titleView.setText(post.title);
        authorView.setText(post.author);
        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);
        deleteView.setOnClickListener(deleteClickListener);
        starView.setOnClickListener(starClickListener);

        mDatabase.child(USERS_TABLE).child(post.uid).addListenerForSingleValueEvent(
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
            storageReference.child(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(final Uri uri) {
                    Picasso.get().load(uri).resize(100,100).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(authorPic, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.INVISIBLE);
                            authorPic.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            //Try again online if cache failed
                            Picasso.get()
                                    .load(uri)
                                    .resize(100,100)
                                    .error(R.drawable.ic_launcher)
                                    .transform(new CircleTransform()).into(authorPic, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            authorPic.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Log.v("Picasso","Could not fetch image" + e.toString());
                                        }
                                    });
                        }
                    });

                }
            });
        }
        else {
            Picasso.get().load(R.drawable.ic_launcher).resize(100,100).centerCrop().transform(new CircleTransform()).into(authorPic, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.INVISIBLE);
                    authorPic.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {

                }
            });

        }
    }



}