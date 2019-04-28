package com.shaym.leash.ui.gear.fragments;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.shaym.leash.R;
import com.shaym.leash.logic.gear.BoardGearPost;

public class BoardsAdapter extends FirebaseRecyclerAdapter<BoardGearPost, GearPostViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BoardsAdapter(@NonNull FirebaseRecyclerOptions options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull GearPostViewHolder holder, int position, @NonNull BoardGearPost model) {
        holder.bindToPost(model);
    }

    @NonNull
    @Override
    public GearPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new GearPostViewHolder(inflater.inflate(R.layout.item_gear_post, parent, false));
    }
}
