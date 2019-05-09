package com.shaym.leash.ui.gear;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.logic.gear.GearPost;
import com.shaym.leash.ui.forum.PostViewHolder;

public class GearAdapter extends FirebaseRecyclerAdapter<GearPost, GearPostViewHolder> {

    private onGearPostSelectedListener mListener;


    private static final String TAG = "GearAdapter";
    private int selected_position = 0; // You have to set this globally in the Adapter class


    GearAdapter(FirebaseRecyclerOptions options , onGearPostSelectedListener listener) {
        super(options);
        Log.d(TAG, "GearAdapter: ");
        this.mListener = listener;

    }


    @NonNull
    @Override
    public GearPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d(TAG, "onCreateViewHolder: ");
        return new GearPostViewHolder(inflater.inflate(R.layout.item_gear_post, parent, false));
    }


    @Override
    protected void onBindViewHolder(@NonNull GearPostViewHolder holder, int position, @NonNull GearPost model) {
        holder.bindToPost(model, null, null);
//        holder.getTitle().setText(cam.getBeachName());


        if (selected_position == position){
//            holder.itemView.setBackgroundResource(R.drawable.underline_cameras);
            mListener.onGearPostSelected(model);
        }
        else {
            holder.itemView.setBackgroundResource(0);

        }

    }









}