package com.singularitycoder.newstime.home.view;

import android.Manifest;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.FragmentHomeDetailBinding;
import com.singularitycoder.newstime.helper.AppSharedPreference;
import com.singularitycoder.newstime.helper.AppUtils;
import com.singularitycoder.newstime.helper.FrescoImageViewerFragment;
import com.singularitycoder.newstime.helper.WebViewFragment;

import java.util.Locale;

public final class HomeDetailFragment extends Fragment {

    @NonNull
    private final String TAG = "HomeDetailFragment";

    @NonNull
    private final AppUtils appUtils = new AppUtils();

    @Nullable
    private AppSharedPreference appSharedPreference;

    @Nullable
    private TextToSpeech textToSpeech;

    @Nullable
    private FragmentHomeDetailBinding binding;

    public HomeDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeDetailBinding.inflate(inflater, container, false);
        final View viewRoot = binding.getRoot();
        getBundleData();
        setUpToolBar();
        initialise();
        initializeTextToSpeech();
        setUpListeners();
        return viewRoot;
    }

    private void getBundleData() {
        if (null == getArguments()) return;
        appUtils.glideImage(getContext(), getArguments().getString("NEWS_IMAGE_URL"), binding.ivHeaderImage);
        binding.tvTitle.setText(getArguments().getString("NEWS_TITLE"));
        binding.tvDescription.setText(getArguments().getString("NEWS_DESCRIPTION"));
        binding.tvContent.setText(getArguments().getString("NEWS_CONTENT"));
        binding.tvAuthor.setText("Author: " + getArguments().getString("NEWS_AUTHOR"));
        binding.tvSource.setText("Source: " + getArguments().getString("NEWS_SOURCE"));
        binding.tvPublishedAt.setText("Published At: " + appUtils.formatDate(getArguments().getString("NEWS_DATE")));
    }

    private void setUpToolBar() {
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null == activity) return;
        activity.setSupportActionBar(binding.toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.setTitle("");
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStackImmediate());
    }

    private void initialise() {
        appSharedPreference = AppSharedPreference.getInstance(getContext());
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.ROOT);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(getContext(), "Language not supported!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Void startTextToSpeech() {
        if (null == getArguments()) return null;

        final String UTTERANCE_ID = getArguments().getString("NEWS_TITLE");
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Started reading " + utteranceId, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onDone(String utteranceId) {
                if (utteranceId.equals(UTTERANCE_ID)) {
//                    Toast.makeText(getContext(), "Saved to " + savedAudioFileName, Toast.LENGTH_LONG).show();
                }
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Done" + utteranceId, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String utteranceId) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error with " + utteranceId, Toast.LENGTH_SHORT).show());
            }
        });

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UtteredWord");

        String textToSpeak = getArguments().getString("NEWS_TITLE") + " " + getArguments().getString("NEWS_DESCRIPTION") + " " + getArguments().getString("NEWS_CONTENT");
        textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, UTTERANCE_ID);

        // Stay silent for 1000 ms
        textToSpeech.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, UTTERANCE_ID);
        return null;
    }

    private void setUpListeners() {
        binding.tvFullStory.setOnClickListener(v -> {
            if (null == getArguments()) return;
            final Bundle bundle = new Bundle();
            bundle.putString("SOURCE_URL", getArguments().getString("NEWS_SOURCE"));
            bundle.putString("SOURCE_TITLE", getArguments().getString("NEWS_TITLE"));
            appUtils.addFragment(getActivity(), bundle, R.id.con_lay_base_activity_root, new WebViewFragment());
        });
        binding.ivHeaderImage.setOnClickListener(v -> {
            if (null == getArguments()) return;
            final Bundle bundle = new Bundle();
            bundle.putString("IMAGE_URL", getArguments().getString("NEWS_IMAGE_URL"));
            bundle.putString("IMAGE_TITLE", getArguments().getString("NEWS_TITLE"));
            appUtils.addFragment(getActivity(), bundle, R.id.con_lay_base_activity_root, new FrescoImageViewerFragment());
        });
        binding.tvShareNews.setOnClickListener(v -> {
            if (null == getArguments()) return;
            appUtils.shareData(getActivity(), getArguments().getString("NEWS_IMAGE_URL"), binding.ivHeaderImage, getArguments().getString("NEWS_TITLE"), getArguments().getString("NEWS_DESCRIPTION"));
        });
        binding.tvReadNews.setOnClickListener(v -> {
            appUtils.checkPermissions(getActivity(), () -> startTextToSpeech(), null, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
