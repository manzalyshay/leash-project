package com.shaym.leash.ui.home.cameras;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shaym.leash.R;

import java.util.Objects;

public class FullScreenWebViewFragment extends DialogFragment {
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

        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        v.findViewById(R.id.enlarge_control).setOnClickListener(v1 -> {
            Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            dismiss();

        });
        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }



    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
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