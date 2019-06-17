package com.shaym.leash.ui.home.cameras;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.shaym.leash.R;

import static com.shaym.leash.ui.home.cameras.CamerasFragment.CAMERA_PARCE;

public class WebViewPlayerFragment extends Fragment {
    private WebView mWebview;
    private Camera mCurrentCamera;
    private TextView mTitleview;
    private TextView mLocationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_webviewplayer, container, false);
        mTitleview = v.findViewById(R.id.title_view);
        mLocationView = v.findViewById(R.id.location_view);
        mWebview = v.findViewById(R.id.webview);

        mWebview.setWebViewClient(new myWebClient());
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.setInitialScale(1);
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setUseWideViewPort(true);

        // 1. Get the object in onCreate();
        if (getArguments() != null) {
            mCurrentCamera = getArguments().getParcelable(CAMERA_PARCE);
            updateCamera(mCurrentCamera);
        }

        return v;
    }

    public WebView getWebview() {
        return mWebview;
    }

    public void updateCamera(Camera cam){
        mCurrentCamera = cam;
        mWebview.loadUrl(mCurrentCamera.getUrl());
        mTitleview.setText(mCurrentCamera.getBeachName());
        mLocationView.setText(mCurrentCamera.getLocation());
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
}
