package com.shaym.leash.ui.home.profile;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.ui.forum.PostViewHolder;
import com.shaym.leash.ui.forum.onPostSelectedListener;

import androidx.annotation.NonNull;

public class ProfileAdapter extends FirebaseRecyclerAdapter<Post, ProfilePostViewHolder> {

    private onPostSelectedListener mListener;

    private static final String TAG = "ProfileAdapter";
    private int selected_position = 0; // You have to set this globally in the Adapter class


    ProfileAdapter(FirebaseRecyclerOptions options , onPostSelectedListener listener) {
        super(options);
        this.mListener = listener;

    }


    @NonNull
    @Override
    public ProfilePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d(TAG, "onCreateViewHolder: ");
        return new ProfilePostViewHolder(inflater.inflate(R.layout.item_post_profile, parent, false));
    }


    @Override
    protected void onBindViewHolder(@NonNull ProfilePostViewHolder holder, int position, @NonNull Post model) {
        holder.bindToPost(model, null, null);
//        holder.getTitle().setText(cam.getBeachName());

        if (selected_position == position){
//            holder.itemView.setBackgroundResource(R.drawable.underline_cameras);
            mListener.onPostSelected(model);
        }
        else {
            holder.itemView.setBackgroundResource(0);

        }

    }









}