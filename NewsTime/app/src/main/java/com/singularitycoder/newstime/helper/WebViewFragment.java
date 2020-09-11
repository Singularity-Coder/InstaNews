package com.singularitycoder.newstime.helper;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.FragmentWebViewBinding;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public final class WebViewFragment extends Fragment implements ActionBottomDialogFragment.ItemClickListener {

    private final String UTTERANCE_ID = "PastedText";
    private final int REQUEST_CODE_RECOGNISE_SPEECH = 100;

    @NonNull
    private final String TAG = "WebViewFragment";

    @Nullable
    private TextToSpeech textToSpeech;

    @Nullable
    private String newsSourceUrl;

    @NonNull
    private AppUtils appUtils = AppUtils.getInstance();

    @NonNull
    private String toolbarTitle = "";

    @NonNull
    private String savedAudioFileName = "";

    @NonNull
    private List<String> history = new ArrayList<>();

    @Nullable
    private FragmentWebViewBinding binding;

    private BottomSheetBehavior bottomSheetBehavior;

    // todo when u click text to speech it should fetch webpage content n open new frag n start reading. jsoup stuff i think
    // todo Jsoup web scraping - reader mode
    // todo make url an observable field and let others subsribe to it so that u dont have to assign it again n again
    // todo audio reader should be within reader mode not outside it and all audio n tts controls within it - simple

    public WebViewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWebViewBinding.inflate(inflater, container, false);
        final View viewRoot = binding.getRoot();
        getBundleData();
        setUpToolBar();
        createFolderToSaveAudio();
        initializeTextToSpeech();
        initialiseWebView();
        showWebView();
        setUpListeners();
        return viewRoot;
    }

    // todo Reader view bottom sheet - Onclick
    public void showBottomSheet(View view) {
        ActionBottomDialogFragment addPhotoBottomDialogFragment = ActionBottomDialogFragment.newInstance();
        addPhotoBottomDialogFragment.show(getActivity().getSupportFragmentManager(), ActionBottomDialogFragment.TAG);
    }

    // todo Reader view bottom sheet
    private void bottomSheet(View view) {
        // get the bottom sheet view
        ConstraintLayout bottomSheetLayout = view.findViewById(R.id.bottom_sheet2);
        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        // set callback for changes
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        binding.tvBottomSheetState.setText("STATE HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        binding.tvBottomSheetState.setText("STATE EXPANDED");
                        // update toggle button text
                        binding.btnToggleBottomSheet.setText("Expand BottomSheet");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        binding.tvBottomSheetState.setText("STATE COLLAPSED");
                        // update collapsed button text
                        binding.btnToggleBottomSheet.setText("Collapse BottomSheet");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        binding.tvBottomSheetState.setText("STATE DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        binding.tvBottomSheetState.setText("STATE SETTLING");
                        break;
                }
                Log.d(TAG, "onStateChanged: " + newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        // set listener on button click
        binding.btnToggleBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    binding.btnToggleBottomSheet.setText("Collapse BottomSheet");
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    binding.btnToggleBottomSheet.setText("Expand BottomSheet");
                }
            }
        });
    }

    private void getBundleData() {
        if (null != getArguments()) {
            final String url = getArguments().getString("SOURCE_URL");
            final String title = getArguments().getString("SOURCE_TITLE");

            toolbarTitle = url;

            String escapedQueryUrl = null;
            String escapedQueryTitle = null;

            try {
                escapedQueryUrl = URLEncoder.encode(url, "UTF-8");
                escapedQueryTitle = URLEncoder.encode(title, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            newsSourceUrl = "https://www.google.com/search?q=" + escapedQueryUrl + " " + escapedQueryTitle;
        } else {
            newsSourceUrl = "empty";
            toolbarTitle = "empty";
        }
    }

    private void setUpToolBar() {
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null != activity) {
            activity.setSupportActionBar(binding.toolbar);
            activity.setTitle(toolbarTitle);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

    @SuppressLint("SimpleDateFormat")
    private void createFolderToSaveAudio() {
        // Create folder first to save speech audio
        File audioFileDirectory = new File(Environment.getExternalStorageDirectory() + "/SavedTextToSpeechAudio/");
        audioFileDirectory.mkdirs();
        savedAudioFileName = audioFileDirectory.getAbsolutePath() + "/" + UTTERANCE_ID + "_" + new SimpleDateFormat("dd-MM-yyyy-hh_mm_ss").format(new Date()) + ".wav";
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status != TextToSpeech.SUCCESS) return;

            int result = textToSpeech.setLanguage(Locale.ROOT);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getContext(), "Language not supported!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking Now!");
        try {
            startActivityForResult(intent, REQUEST_CODE_RECOGNISE_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(), "Sorry! Your device is not supported!", Toast.LENGTH_SHORT).show();
        }
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
        hideProgress();
    }

    private void setUpListeners() {
        binding.ivRefresh.setOnClickListener(view -> showWebView());
        binding.ivBackward.setOnClickListener(view -> {
            if (null != history && history.size() != 0) {
                for (int i = 0; i < history.size(); i++) {
                    newsSourceUrl = history.get(history.size() - 1);
                }
            }
            showWebView();
        });
        binding.ivForward.setOnClickListener(view -> {
            if (null != history && history.size() != 0) {
                for (int i = 0; i < history.size(); i++) {
                    newsSourceUrl = history.get(history.size() - 1);
                }
            }
            showWebView();
        });
        binding.ivShare.setOnClickListener(view -> appUtils.shareData(getActivity(), null, null, toolbarTitle, newsSourceUrl));
    }

    @Override
    public void onItemClick(String item) {
        // todo Reader View item click. Not the correct bottom sheett though
    }

    private final class NewsWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            try {
                history.add(url);
                newsSourceUrl = url;
                showProgress();
            } catch (WindowManager.BadTokenException ignored) {
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            try {
                showProgress();
            } catch (WindowManager.BadTokenException ignored) {
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideProgress();
        }
    }

    private void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
    }

    // todo rxjava serach

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_web_view, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.string_search_hint) + "</font>"));
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                searchWeb(newText);
                newsSourceUrl = "https://www.google.com/search?q=" + newText;
                showWebView();
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_speak:
                if (null != getActivity()) startSpeechToText();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


//    private void searchWeb(String text) {
//        ArrayList<ContactItem> filterdUsers = new ArrayList<>();
//        //looping through existing elements
//        for (ContactItem user : contactList) {
//            if (user.getStrName().toLowerCase().trim().contains(text.toLowerCase())) {
//                filterdUsers.add(user);
//            }
//        }
//        contactsAdapter.flterList(filterdUsers);
//        contactsAdapter.notifyDataSetChanged();
//        tvListCount.setText(filterdUsers.size() + " Contacts");
//    }

//    public void flterList(ArrayList<ContactItem> list) {
//        this.contactsList = list;
//        notifyDataSetChanged();
//    }

    @Override
    public void onDestroyView() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RECOGNISE_SPEECH && resultCode == RESULT_OK && null != data) {
            final ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            binding.toolbar.setTitle(String.valueOf(result.get(0)));
            newsSourceUrl = "https://www.google.com/search?q=" + result.get(0);
            showWebView();
        }
    }
}
