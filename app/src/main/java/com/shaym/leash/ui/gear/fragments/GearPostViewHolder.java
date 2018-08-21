package com.shaym.leash.ui.gear.fragments;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.aroundme.CircleTransform;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.GearPost;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class GearPostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView priceView;
    public TextView authorView;
    public TextView phoneNumView;
    public ImageView gearPic;
    FirebaseStorage storage;
    StorageReference storageReference;
    public ImageView starView;
    public ProgressBar progressBar;

    public ImageView deleteView;
    public TextView numStarsView;

    public GearPostViewHolder(View itemView) {
        super(itemView);
        storage = FirebaseStorage.getInstance();
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

    public void bindToPost(GearPost post, View.OnClickListener starClickListener, View.OnClickListener deleteClickListener) {
        titleView.setText(post.title);
        priceView.setText(Integer.toString(post.price));
        authorView.setText(post.author);
        phoneNumView.setText(post.phonenumber);
        attachPic(post.imageurl);
        numStarsView.setText(String.valueOf(post.starCount));
        deleteView.setOnClickListener(deleteClickListener);
        starView.setOnClickListener(starClickListener);
    }

    public void attachPic(String url){
     if (!url.isEmpty()) {
        storageReference.child(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                Picasso.get().load(uri).resize(200,200).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().into(gearPic, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                        gearPic.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        //Try again online if cache failed
                        Picasso.get()
                                .load(uri)
                                .resize(200,200)
                                .error(R.drawable.ic_launcher)
                                .into(gearPic, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        gearPic.setVisibility(View.VISIBLE);
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
         Picasso.get().load(R.drawable.ic_launcher).resize(200,200).centerCrop().into(gearPic, new Callback() {
             @Override
             public void onSuccess() {
                 progressBar.setVisibility(View.INVISIBLE);
                 gearPic.setVisibility(View.VISIBLE);
             }

             @Override
             public void onError(Exception e) {

             }
         });

     }

    }
    }
