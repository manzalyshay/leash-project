package com.shaym.leash.ui.gear;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shaym.leash.R;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.logic.user.Profile;
import com.shaym.leash.logic.utils.FireBasePostsHelper;
import com.shaym.leash.logic.utils.FireBaseUsersHelper;

import java.util.List;
import java.util.Objects;

import static com.shaym.leash.logic.utils.CONSTANT.USERS_TABLE;
import static com.shaym.leash.ui.gear.GearPostActivity.EXTRA_POST_KEY;
import static com.shaym.leash.ui.home.chat.ChatActivity.getUid;

class GearPostViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {
    private static final String TAG = "GearPostViewHolder";
    private TextView desciption;
    private TextView priceView;
    private TextView authorName;
    private TextView gearpostdate;
    private TextView gearpostlocation;

    private ImageView gearPic;
    private ImageView settingsPic;

    private StorageReference storageReference;
    private ProgressBar progressBar;
    private ProgressBar authorProgressBar;

    private DatabaseReference mDatabase;
    private ImageView authorPic;
    private GearPost currentPost;
    private Fragment mFragment;
    private Profile postProfile;

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
        settingsPic = itemView.findViewById(R.id.settings_icon);
        itemView.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    void bindToPost(GearPost post, Fragment fragment, Profile postprofile) {
        postProfile = postprofile;
        mFragment = fragment;
        currentPost = post;

        settingsPic.setOnClickListener(this::showPopup);

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
            FireBasePostsHelper.getInstance().attachPic(post.images.get(0), gearPic, progressBar, 200, 200);
        }
        else {
            gearPic.setVisibility(View.VISIBLE);
        }


        if (postProfile != null) {
            FireBasePostsHelper.getInstance().attachRoundPic(postProfile.getAvatarurl(), authorPic, authorProgressBar, 100, 100);
            authorName.setText(postProfile.getDisplayname());
        }

        // [END single_value_read]

    }




        public void showPopup(View v) {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.setOnMenuItemClickListener(this);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.forumpost_settings_menu, popup.getMenu());
            if (getUid().equals(currentPost.uid)){
                popup.getMenu().findItem(R.id.delete).setVisible(true);
            }
            popup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    FireBasePostsHelper.getInstance().deleteGearPost(currentPost);
                    return true;
                case R.id.share:

                    return true;
                default:
                    return false;
            }
        }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mFragment.getActivity(), GearPostActivity.class);
        Bundle b = new Bundle();
        b.putString(EXTRA_POST_KEY, currentPost.key);
        intent.putExtras(b); //Put your id to your next Intent
        Objects.requireNonNull(mFragment.getActivity()).startActivity(intent);
    }
}



