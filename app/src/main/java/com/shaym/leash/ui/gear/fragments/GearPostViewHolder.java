package com.shaym.leash.ui.gear.fragments;

import android.annotation.SuppressLint;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.gear.GearPost;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

class GearPostViewHolder extends RecyclerView.ViewHolder {

    private TextView titleView;
    private TextView priceView;
    private TextView authorView;
    private ImageView gearPic;
    private StorageReference storageReference;
    private ProgressBar progressBar;

    GearPostViewHolder(View itemView) {
        super(itemView);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        titleView = itemView.findViewById(R.id.gear_title);
        priceView = itemView.findViewById(R.id.gear_price);
        authorView = itemView.findViewById(R.id.gear_contact);
        gearPic = itemView.findViewById(R.id.gear_thumb);
        progressBar = itemView.findViewById(R.id.gear_thumb_progress);


    }

    @SuppressLint("SetTextI18n")
    void bindToPost(GearPost post) {
        priceView.setText(Integer.toString(post.price));
        authorView.setText(post.contact);
        if (post.picsref != null && post.picsref.size() > 0) {
            attachPic(post.picsref.get(0));
        }

    }

    private void attachPic(String url){
        if (!url.isEmpty()) {
                Picasso.get().load(url).resize(200, 200).centerCrop().into(gearPic, new Callback() {
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



        else {
            progressBar.setVisibility(View.INVISIBLE);

        }
    }
    }
