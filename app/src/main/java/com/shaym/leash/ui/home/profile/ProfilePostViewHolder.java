package com.shaym.leash.ui.home.profile;

import android.net.Uri;
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
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.user.Profile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;
import static tcking.github.com.giraffeplayer2.GiraffePlayer.TAG;

public class ProfilePostViewHolder extends RecyclerView.ViewHolder {

    private TextView titleView;
    private TextView authorView;
    private ImageView authorPic;
    private ProgressBar progressBar;
    public ImageView starView;
    private ImageView deleteView;
    private TextView numStarsView;
    private TextView bodyView;
    private Profile postProfile;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;

    public ProfilePostViewHolder(View itemView) {
        super(itemView);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //titleView = itemView.findViewById(R.id.post_title);

        starView = itemView.findViewById(R.id.star);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
        bodyView = itemView.findViewById(R.id.post_body);
    }


    public void bindToPost(Post post, View.OnClickListener starClickListener, View.OnClickListener deleteClickListener) {
        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);


    }






    private void attachPic(String url){
        if (!url.isEmpty()) {
            if (url.charAt(0) == 'g') {
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
                                e.printStackTrace();
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