package com.shaym.leash.ui.home.cameras;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaym.leash.R;

import tcking.github.com.giraffeplayer2.VideoView;

/**
 * Created by shaym on 2/20/18.
 */


public class PlayerViewHolder extends RecyclerView.ViewHolder {
    public TextView title, location;
    private ImageView overflow;
    private VideoView videoView;

    PlayerViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.title0);
        location = view.findViewById(R.id.location0);
        videoView = view.findViewById(R.id.videoview);
        overflow = view.findViewById(R.id.overflow0);
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getLocation() {
        return location;
    }

    ImageView getOverflow() {
        return overflow;
    }

    VideoView getVideoView() {
        return videoView;
    }
}
