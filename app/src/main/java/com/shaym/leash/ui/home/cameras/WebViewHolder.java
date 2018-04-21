package com.shaym.leash.ui.home.cameras;

/**
 * Created by shaym on 2/20/18.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shaym.leash.R;



/**
 * Created by shaym on 2/20/18.
 */


public class WebViewHolder extends RecyclerView.ViewHolder {
    private TextView title, location;
    private ImageView overflow;
    public WebView mWebView;
    private static final String TAG = "WebViewHolder";


    public WebViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.title1);
        location = (TextView) view.findViewById(R.id.location1);
        mWebView = (WebView) view.findViewById(R.id.webview);
        overflow = (ImageView) view.findViewById(R.id.overflow1);
        mWebView.setWebViewClient(new myWebClient());
        mWebView.getSettings().setJavaScriptEnabled(true);



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

    public ImageView getOverflow() {
        return overflow;
    }

    public WebView getWebView() {
        return mWebView;
    }



}
