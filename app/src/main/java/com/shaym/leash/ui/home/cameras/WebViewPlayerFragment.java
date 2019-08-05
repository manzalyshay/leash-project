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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shaym.leash.R;
import com.shaym.leash.logic.cameras.CameraObject;
import com.shaym.leash.logic.utils.FireBasePostsHelper;

import static com.shaym.leash.ui.home.cameras.CamerasFragment.CAMERA_PARCE;

public class WebViewPlayerFragment extends Fragment {
    private WebView mWebview;
    private CameraObject mCurrentCameraObject;
    private TextView mTitleview;
    private TextView mLocationView;
    private ImageView mSponserLogo;
    private ProgressBar mSponserLogoPbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_webviewplayer, container, false);
        mTitleview = v.findViewById(R.id.title_view);
        mLocationView = v.findViewById(R.id.location_view);
        mWebview = v.findViewById(R.id.webview);
        mSponserLogo = v.findViewById(R.id.sponsor_logo);
        mSponserLogoPbar = v.findViewById(R.id.sponsor_logo_pbar);

        mWebview.setWebViewClient(new myWebClient());
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.setInitialScale(1);
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setUseWideViewPort(true);

        // 1. Get the object in onCreate();
        if (getArguments() != null) {
            mCurrentCameraObject = getArguments().getParcelable(CAMERA_PARCE);
            updateCamera(mCurrentCameraObject);
        }

        return v;
    }

    public WebView getWebview() {
        return mWebview;
    }

    public void updateCamera(CameraObject cam){
        mCurrentCameraObject = cam;
        mWebview.loadUrl(mCurrentCameraObject.getUrl());
        mTitleview.setText(mCurrentCameraObject.getLocation());
        mLocationView.setText(mCurrentCameraObject.getCity());
        FireBasePostsHelper.getInstance().attachPic(mCurrentCameraObject.mSponsorPicRef, mSponserLogo, mSponserLogoPbar, 30, 30);

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
