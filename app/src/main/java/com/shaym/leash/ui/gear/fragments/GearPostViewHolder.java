package com.shaym.leash.ui.gear.fragments;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.gear.GearPost;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

class GearPostViewHolder extends RecyclerView.ViewHolder {

    private TextView titleView;
    private TextView priceView;
    private TextView authorView;
    private TextView phoneNumView;
    private ImageView gearPic;
    private StorageReference storageReference;
    ImageView starView;
    private ProgressBar progressBar;

    private ImageView deleteView;
    private TextView numStarsView;

    GearPostViewHolder(View itemView) {
        super(itemView);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        titleView = itemView.findViewById(R.id.post_gear_title);
        priceView = itemView.findViewById(R.id.post_gear_price);
        authorView = itemView.findViewById(R.id.post_seller);
        phoneNumView = itemView.findViewById(R.id.seller_phone_number);
        gearPic = itemView.findViewById(R.id.post_gear_photo);
        progressBar = itemView.findViewById(R.id.post_gear_photo_progressbar);

        starView = itemView.findViewById(R.id.star);
        deleteView = itemView.findViewById(R.id.delete);

        numStarsView = itemView.findViewById(R.id.post_num_stars);
    }

    @SuppressLint("SetTextI18n")
    void bindToPost(GearPost post, View.OnClickListener starClickListener, View.OnClickListener deleteClickListener) {
        titleView.setText(post.title);
        priceView.setText(Integer.toString(post.price));
        authorView.setText(post.author);
        phoneNumView.setText(post.phonenumber);
        attachPic(post.imageurl);
        numStarsView.setText(String.valueOf(post.starCount));
        deleteView.setOnClickListener(deleteClickListener);
        starView.setOnClickListener(starClickListener);
    }

    private void attachPic(String url){
        if (!url.isEmpty()) {
                storageReference.child(url).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).resize(200, 200).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().transform(new CircleTransform()).into(gearPic, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(uri).resize(200, 200).centerCrop().transform(new CircleTransform()).into(gearPic, new Callback() {
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
            }
        else {
            progressBar.setVisibility(View.INVISIBLE);

        }
    }
    }
