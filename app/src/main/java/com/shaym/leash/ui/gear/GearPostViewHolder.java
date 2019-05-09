package com.shaym.leash.ui.gear;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;

class GearPostViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "GearPostViewHolder";
    private TextView desciption;
    private TextView priceView;
    private TextView authorName;
    private TextView gearpostdate;
    private TextView gearpostlocation;

    private ImageView gearPic;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    private ProgressBar authorProgressBar;

    private DatabaseReference mDatabase;
    private ImageView authorPic;

    GearPostViewHolder(View itemView) {
        super(itemView);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        gearpostdate = itemView.findViewById(R.id.gear_post_date);
        gearpostlocation = itemView.findViewById(R.id.gear_post_location);

        desciption = itemView.findViewById(R.id.gear_post_body);
        priceView = itemView.findViewById(R.id.price_amount);
        gearPic = itemView.findViewById(R.id.gear_thumb);
        progressBar = itemView.findViewById(R.id.gear_thumb_progress);
        authorName = itemView.findViewById(R.id.gear_post_author);
        authorPic = itemView.findViewById(R.id.gear_post_author_photo);
        authorProgressBar = itemView.findViewById(R.id.gear_post_author_photo_progressbar);

    }

    @SuppressLint("SetTextI18n")
    void bindToPost(GearPost post, View.OnClickListener starClickListener, View.OnClickListener deleteClickListener) {
        priceView.setText(Integer.toString(post.price));
        desciption.setText(post.body);

        gearpostlocation.setText(post.location);

        int dayspast = FireBasePostsHelper.getInstance().getDaysDifference(post.publishdate);
        if (dayspast != 0) {
            gearpostdate.setText("לפני " + dayspast + " ימים");
        }
        else {
            gearpostdate.setText("היום");

        }

        if (post.images != null && post.images.size() > 0) {
            attachGearPic(post.images.get(0), gearPic, progressBar);
        }
        else {
            gearPic.setVisibility(View.VISIBLE);
        }



        mDatabase.child(USERS_TABLE).child(post.uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get user value
                        Profile postProfile = dataSnapshot.getValue(Profile.class);
                        assert postProfile != null;
                        attachPic(postProfile.getAvatarURL(), authorPic, authorProgressBar);
                        authorName.setText(postProfile.getDisplayname());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
        // [END single_value_read]

    }




    private void attachPic(String url, ImageView imageView, ProgressBar progressBar){
        if (!url.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            if (url.charAt(0) == 'g') {
                storageReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(100, 100).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess: ");
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d(TAG, "onError: ");
                        e.printStackTrace();
                        Picasso.get().load(uri).resize(100, 100).centerCrop().transform(new CircleTransform()).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed

                                progressBar.setVisibility(View.GONE);

                            }
                        });

                    }

                }));
            } else {
                Picasso.get().load(Uri.parse(url)).resize(100, 100).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(Uri.parse(url)).resize(100, 100).centerCrop().transform(new CircleTransform()).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                //Try again online if cache failed
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);

                            }
                        });

                    }

                });
            }
        }
        else {
            progressBar.setVisibility(View.GONE);

        }
    }


    private void attachGearPic(String url, ImageView imageView, ProgressBar progressBar) {
        Log.d(TAG, "attachGearPic: ");
        if (!url.isEmpty()) {
            Log.d(TAG, "attachGearPic: URL NOT EMPTY");

            progressBar.setVisibility(View.VISIBLE);
            storageReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(200, 200).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess: ");
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    Log.d(TAG, "onError: ");
                    e.printStackTrace();
                    Picasso.get().load(uri).resize(200, 200).centerCrop().into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            //Try again online if cache failed

                            progressBar.setVisibility(View.GONE);

                        }
                    });

                }

            }));
        } else {
            progressBar.setVisibility(View.GONE);

        }
    }


}
