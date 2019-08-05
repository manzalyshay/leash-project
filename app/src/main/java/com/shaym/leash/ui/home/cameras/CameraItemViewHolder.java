package com.shaym.leash.ui.home.cameras;

/**
 * Created by shaym on 2/20/18.
 */

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shaym.leash.R;


public class CameraItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView title;
    private ImageView mCameraCover;
    private ImageView mPlayCover;
    private ProgressBar mProgressbar;
    private static final String TAG = "CameraItemViewHolder";
    private CamerasAdapter mCamerasAdapter;

    @SuppressLint("SetJavaScriptEnabled")
    CameraItemViewHolder(View view, CamerasAdapter adapter) {
        super(view);
        title = view.findViewById(R.id.title1);
//        location = view.findViewById(R.id.location1);
//        overflow = view.findViewById(R.id.overflow1);
        mCameraCover = view.findViewById(R.id.camera_cover);
        mPlayCover = view.findViewById(R.id.play_cover);
        mProgressbar = view.findViewById(R.id.camera_cover_pbar);
//        mWebView.setWebViewClient(new myWebClient());
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.setInitialScale(1);
//        mWebView.getSettings().setLoadWithOverviewMode(true);
//        mWebView.getSettings().setUseWideViewPort(true);
        itemView.setOnClickListener(this);
        this.mCamerasAdapter = adapter;
    }

    @Override
    public void onClick(View v) {
        // Below line is just like a safety check, because sometimes holder could be null,
        // in that case, getAdapterPosition() will return RecyclerView.NO_POSITION
        Log.d(TAG, "onClick: ");
        if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

        // Updating old as well as new positions
        mCamerasAdapter.notifyItemChanged(mCamerasAdapter.selected_position);
        mCamerasAdapter.selected_position = getAdapterPosition();
        mCamerasAdapter.notifyItemChanged(mCamerasAdapter.selected_position);

    }

    public ImageView getPlayCover() {
        return mPlayCover;
    }

    public void setPlayCover(ImageView mPlayCover) {
        this.mPlayCover = mPlayCover;
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {

        }
    }
    public TextView getTitle() {
        return title;
    }



    public ImageView getCover() {
        return mCameraCover;
    }

    public ProgressBar getProgressbar() {
        return mProgressbar;
    }




}
