package com.shaym.leash.ui.gear;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shaym.leash.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshayejh on 19/12/17.
 */

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder>{

    private List<Bitmap> imagesList;


    UploadListAdapter(){
        imagesList = new ArrayList<>();
    }

    public void addImage(Bitmap b){
        imagesList.add(b);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap b = imagesList.get(position);

       holder.preview.setImageBitmap(b);

    }


    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ImageView preview;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            preview = (ImageView) mView.findViewById(R.id.upload_preview);


        }

    }

}
