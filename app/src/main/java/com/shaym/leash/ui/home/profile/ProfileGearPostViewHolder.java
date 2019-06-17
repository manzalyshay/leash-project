package com.shaym.leash.ui.home.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.ui.forum.ForumPostActivity;
import com.shaym.leash.ui.gear.GearPostActivity;

import java.util.Objects;

import static com.shaym.leash.ui.gear.GearPostActivity.EXTRA_POST_KEY;

public class ProfileGearPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView dateView;
    private TextView locationView;
    private ImageView gearThumb;
    private ProgressBar progressBar;
    public ImageView starView;
    private ImageView settingsView;
    private TextView priceView;
    private TextView bodyView;
    private Profile postProfile;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private GearPost mCurrentPost;
    private Fragment mFragment;

    public ProfileGearPostViewHolder(View itemView) {
        super(itemView);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressBar = itemView.findViewById(R.id.gear_thumb_progress);
        gearThumb = itemView.findViewById(R.id.gear_thumb);
        locationView = itemView.findViewById(R.id.gear_post_location);
        starView = itemView.findViewById(R.id.star);
        settingsView = itemView.findViewById(R.id.settings_icon);
        itemView.findViewById(R.id.gear_contact_layout).setVisibility(View.GONE);
        bodyView = itemView.findViewById(R.id.gear_post_body);
        dateView = itemView.findViewById(R.id.gear_post_date);
        priceView = itemView.findViewById(R.id.price_amount);
        itemView.setOnClickListener(this);
    }


    public void bindToPost(GearPost post, Fragment fragment) {

        mFragment = fragment;
        mCurrentPost = post;

        int dayspast = FireBasePostsHelper.getInstance().getDaysDifference(mCurrentPost.date);

        if (dayspast != 0) {
            dateView.setText("לפני " + dayspast + " ימים");
        }
        else {
            dateView.setText("היום");

        }

        locationView.setText(mCurrentPost.location);
        priceView.setText(Integer.toString(mCurrentPost.price));

        bodyView.setText(mCurrentPost.body);

        if (mCurrentPost.images != null) {
            FireBasePostsHelper.getInstance().attachPic(mCurrentPost.images.get(0), gearThumb, progressBar, 200, 200);
        }

    }








    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mFragment.getActivity(), GearPostActivity.class);
        Bundle b = new Bundle();
        b.putString(EXTRA_POST_KEY, mCurrentPost.key);
        intent.putExtras(b); //Put your id to your next Intent
        Objects.requireNonNull(mFragment.getActivity()).startActivity(intent);
    }
}