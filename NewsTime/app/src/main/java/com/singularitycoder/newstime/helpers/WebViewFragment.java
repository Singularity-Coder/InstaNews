package com.singularitycoder.newstime.helpers;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.singularitycoder.newstime.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public final class WebViewFragment extends Fragment {

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @Nullable
    @BindView(R.id.web_view)
    WebView webView;

    @NonNull
    private final String TAG = "WebViewFragment";

    @NonNull
    private Unbinder unbinder;

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @Nullable
    private String newsSourceUrl;

    @NonNull
    private String toolbarTitle = "";

    public WebViewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_web_view, container, false);
        getBundleData();
        initialiseViews(rootView);
        setUpToolBar();
        initialiseWebView();
        showWebView();
        swipeRefreshLayout.setOnRefreshListener(this::showWebView);
        return rootView;
    }

    private void getBundleData() {
        if (null != getArguments()) {
            String url = getArguments().getString("SOURCE_URL");
            toolbarTitle = url;
            String escapedQuery = null;
            try {
                escapedQuery = URLEncoder.encode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            newsSourceUrl = "http://www.google.com/#q=" + escapedQuery;
        } else {
            newsSourceUrl = "empty";
            toolbarTitle = "empty";
        }
    }

    private void initialiseViews(View rootView) {
        ButterKnife.bind(this, rootView);
        unbinder = ButterKnife.bind(this, rootView);
        progressBar.setVisibility(View.VISIBLE);
        progressBar = new ProgressBar(getContext());
    }

    private void setUpToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null != activity) {
            activity.setSupportActionBar(toolbar);
            activity.setTitle(toolbarTitle);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

    private void initialiseWebView() {
        webView.requestFocusFromTouch();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setSupportZoom(true);
        webView.setClickable(true);
        webView.clearCache(true);
        getActivity().deleteDatabase("webview.db");
        getActivity().deleteDatabase("webviewCache.db");
        webView.setWebViewClient(new NewsWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                // Do something here
                return super.onJsAlert(view, url, message, result);
            }
        });
    }

    private void showWebView() {
        webView.loadUrl(newsSourceUrl);
        swipeRefreshLayout.setRefreshing(false);
    }

    private class NewsWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            try {
                progressBar.setVisibility(View.VISIBLE);
            } catch (WindowManager.BadTokenException ignored) {
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            try {
                progressBar.setVisibility(View.VISIBLE);
            } catch (WindowManager.BadTokenException ignored) {
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
