package com.shaym.leash.ui.home.cameras;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shaym.leash.R;

public class FullScreenWebViewFragment extends Fragment {
    final static String URL_KEY = "URL_KEY";
    private WebView mWebview;

    public static FullScreenWebViewFragment newInstance(String url) {
        FullScreenWebViewFragment f = new FullScreenWebViewFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        f.setArguments(args);
        return f;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_webview_fullscreen, container, false);
        Bundle args = getArguments();
        assert args != null;
        String url = args.getString(URL_KEY);
        mWebview = v.findViewById(R.id.webview_fullscreen);
        mWebview.setWebViewClient(new myWebClient());
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.loadUrl(url);
        mWebview.setInitialScale(1);
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setUseWideViewPort(true);
        return v;
    }

    public WebView getWebview() {
        return mWebview;
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