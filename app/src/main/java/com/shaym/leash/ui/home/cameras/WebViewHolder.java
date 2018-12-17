package com.shaym.leash.ui.home.cameras;

/**
 * Created by shaym on 2/20/18.
 */

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaym.leash.R;


public class WebViewHolder extends RecyclerView.ViewHolder {
    private TextView title, location;
    private ImageView overflow;
    private WebView mWebView;
    private static final String TAG = "WebViewHolder";


    @SuppressLint("SetJavaScriptEnabled")
    WebViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.title1);
        location = view.findViewById(R.id.location1);
        mWebView = view.findViewById(R.id.webview);
        overflow = view.findViewById(R.id.overflow1);
        mWebView.setWebViewClient(new myWebClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setInitialScale(1);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);

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

    public TextView getLocation() {
        return location;
    }

    ImageView getOverflow() {
        return overflow;
    }

    WebView getWebView() {
        return mWebView;
    }



}
