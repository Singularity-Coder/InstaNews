package com.singularitycoder.newstime.helpers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.singularitycoder.newstime.R;

public final class WebViewActivity extends AppCompatActivity {

    @NonNull
    private final String TAG = "HelperWebViewActivity";

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @Nullable
    private WebView webView;

    @Nullable
    private ProgressBar mProgressBar;

    @Nullable
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    private String newsSourceUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helperObject.setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_helper_web_view);
        getIntentData();
        initialiseViews();
        initialiseWebView();
        showWebView();
        swipeRefreshLayout.setOnRefreshListener(this::showWebView);
    }

    private void initialiseViews() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mProgressBar = findViewById(R.id.load_progress);

        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar = new ProgressBar(WebViewActivity.this);
    }

    private void initialiseWebView() {
        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new NewsWebViewClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.clearCache(true);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        newsSourceUrl = intent.getStringExtra("SOURCE_URL");
    }

    private void showWebView() {
        webView.loadUrl(newsSourceUrl);
        swipeRefreshLayout.setRefreshing(false);
    }

    final class NewsWebViewClient extends WebViewClient {
        public void onPageFinished(WebView view, String url) {
//            webView.loadUrl("javascript:startup()");
            mProgressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
