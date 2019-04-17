package com.shaym.leash.ui.forum;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.shaym.leash.R;
import com.shaym.leash.logic.forum.Post;
import com.shaym.leash.ui.forum.PostViewHolder;
import com.shaym.leash.ui.forum.onPostSelectedListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ForumAdapter extends FirebaseRecyclerAdapter<Post, PostViewHolder> {

    private onPostSelectedListener mListener;


    private static final String TAG = "ForumAdapter";
    private int selected_position = 0; // You have to set this globally in the Adapter class


    ForumAdapter(FirebaseRecyclerOptions options , onPostSelectedListener listener) {
        super(options);
        Log.d(TAG, "ForumAdapter: ");
        this.mListener = listener;

    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d(TAG, "onCreateViewHolder: ");
        return new PostViewHolder(inflater.inflate(R.layout.item_post, parent, false));
    }


    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post model) {
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