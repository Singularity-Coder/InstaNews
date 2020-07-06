package com.singularitycoder.newstime.helper;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.singularitycoder.newstime.databinding.FragmentWebViewBinding;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class WebViewFragment extends Fragment {

    @NonNull
    private final String TAG = "WebViewFragment";

    @NonNull
    private final HelperGeneral helperObject = new HelperGeneral();

    @Nullable
    private String newsSourceUrl;

    @NonNull
    private String toolbarTitle = "";

    @Nullable
    private FragmentWebViewBinding binding;

    public WebViewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWebViewBinding.inflate(inflater, container, false);
        View viewRoot = binding.getRoot();
        getBundleData();
        setUpToolBar();
        initialiseWebView();
        showWebView();
        binding.swipeRefreshLayout.setOnRefreshListener(this::showWebView);
        return viewRoot;
    }

    private void getBundleData() {
        if (null != getArguments()) {
            String url = getArguments().getString("SOURCE_URL");
            String title = getArguments().getString("SOURCE_TITLE");

            toolbarTitle = url;

            String escapedQueryUrl = null;
            String escapedQueryTitle = null;

            try {
                escapedQueryUrl = URLEncoder.encode(url, "UTF-8");
                escapedQueryTitle = URLEncoder.encode(title, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            newsSourceUrl = "http://www.google.com/#q=" + escapedQueryUrl + " " + escapedQueryTitle;
        } else {
            newsSourceUrl = "empty";
            toolbarTitle = "empty";
        }
    }

    private void setUpToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null != activity) {
            activity.setSupportActionBar(binding.toolbarLayout.toolbar);
            activity.setTitle(toolbarTitle);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbarLayout.toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

    private void initialiseWebView() {
        binding.webView.requestFocusFromTouch();
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.getSettings().setSaveFormData(true);
        binding.webView.getSettings().setAllowContentAccess(true);
        binding.webView.getSettings().setAllowFileAccess(true);
        binding.webView.getSettings().setAllowFileAccessFromFileURLs(true);
        binding.webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        binding.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        binding.webView.getSettings().setSupportZoom(true);
        binding.webView.setClickable(true);
        binding.webView.clearCache(true);
        getActivity().deleteDatabase("webview.db");
        getActivity().deleteDatabase("webviewCache.db");
        binding.webView.setWebViewClient(new NewsWebViewClient());
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                // Do something here
                return super.onJsAlert(view, url, message, result);
            }
        });
    }

    private void showWebView() {
        binding.webView.loadUrl(newsSourceUrl);
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    private class NewsWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            try {
                binding.progressCircular.setVisibility(View.VISIBLE);
            } catch (WindowManager.BadTokenException ignored) {
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            try {
                binding.progressCircular.setVisibility(View.VISIBLE);
            } catch (WindowManager.BadTokenException ignored) {
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            binding.progressCircular.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
