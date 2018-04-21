package com.shaym.leash.ui.home.cameras;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaym.leash.R;

import java.util.List;

import tcking.github.com.giraffeplayer2.PlayerListener;
import tcking.github.com.giraffeplayer2.VideoView;

/**
 * Created by shaym on 2/20/18.
 */


public class PlayerViewHolder extends RecyclerView.ViewHolder {
    public TextView title, location;
    public ImageView overflow;
    public VideoView videoView;
    private PlayerListener playerListener;



    public PlayerViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title0);
        location = (TextView) view.findViewById(R.id.location0);
        videoView = (VideoView) view.findViewById(R.id.videoview);
        overflow = (ImageView) view.findViewById(R.id.overflow0);

    }

    public TextView getTitle() {
        return title;
    }

    public TextView getLocation() {
        return location;
    }

    public ImageView getOverflow() {
        return overflow;
    }

    public VideoView getVideoView() {
        return videoView;
    }
}
