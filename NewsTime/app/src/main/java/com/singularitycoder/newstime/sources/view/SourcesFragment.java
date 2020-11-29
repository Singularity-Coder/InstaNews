package com.singularitycoder.newstime.sources.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.espresso.IdlingResource;

import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.FragmentSourcesBinding;
import com.singularitycoder.newstime.helper.AppConstants;
import com.singularitycoder.newstime.helper.AppUtils;
import com.singularitycoder.newstime.helper.espresso.ApiIdlingResource;
import com.singularitycoder.newstime.helper.retrofit.StateMediator;
import com.singularitycoder.newstime.helper.retrofit.UiState;
import com.singularitycoder.newstime.sources.model.SourceItem;
import com.singularitycoder.newstime.sources.viewmodel.SourcesViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static java.lang.String.valueOf;

public final class SourcesFragment extends Fragment {

    @NonNull
    private final String TAG = "SourcesFragment";

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @NonNull
    private final List<String> tabTitlesList = new ArrayList<>();

    @Nullable
    private SourcesViewModel sourcesViewModel;

    @Nullable
    private ApiIdlingResource idlingResource;

    @Nullable
    private FragmentSourcesBinding binding;

    public SourcesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSourcesBinding.inflate(inflater, container, false);
        final View viewRoot = binding.getRoot();
        initialise();
        setUpListeners();
        getNewsFromApi();
        return viewRoot;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initialise() {
        sourcesViewModel = new ViewModelProvider(this).get(SourcesViewModel.class);
    }

    private void setUpListeners() {
        binding.conLayNewsHomeRoot.setOnClickListener(v -> {
        });
    }

    private void getNewsFromApi() {
        if (appUtils.hasInternet(getContext())) showOnlineState();
        else showOfflineState();
    }

    private Void showOnlineState() {
        binding.tvNoInternet.setVisibility(View.GONE);
        final String newsType = "sources";
        final String apiKey = AppConstants.NEWS_API_KEY;
        sourcesViewModel.getSourceListFromRepository(newsType, apiKey, idlingResource).observe(getViewLifecycleOwner(), observeLiveData());
        return null;
    }

    private Void showOfflineState() {
        binding.tvNoInternet.setVisibility(View.VISIBLE);
        hideLoading();
        return null;
    }

    @Nullable
    private Observer<StateMediator<Object, UiState, String, String>> observeLiveData() {
        Observer<StateMediator<Object, UiState, String, String>> observer = null;
        if (appUtils.hasInternet(getContext())) {
            observer = stateMediator -> {
                if (UiState.LOADING == stateMediator.getStatus()) showLoadingState(stateMediator);

                if (UiState.SUCCESS == stateMediator.getStatus()) showSuccessState(stateMediator);

                if (UiState.EMPTY == stateMediator.getStatus()) showEmptyState(stateMediator);

                if (UiState.ERROR == stateMediator.getStatus()) showErrorState(stateMediator);
            };
        }
        return observer;
    }

    private void showLoadingState(StateMediator<Object, UiState, String, String> stateMediator) {
        getActivity().runOnUiThread(() -> showLoading());
    }

    private void showSuccessState(StateMediator<Object, UiState, String, String> stateMediator) {
        getActivity().runOnUiThread(() -> {
            if ((AppConstants.KEY_GET_SOURCE_LIST_API_SUCCESS_STATE).equals(stateMediator.getKey())) {
                showNewsListSuccessState(stateMediator);
            }
        });
    }

    private void showNewsListSuccessState(StateMediator<Object, UiState, String, String> stateMediator) {
        hideLoading();

        final Response<SourceItem.SourceResponse> response = (Response<SourceItem.SourceResponse>) stateMediator.getData();

        if (HttpURLConnection.HTTP_OK == response.code()) {
            showHttpOkState(stateMediator, response);
        }

        if (HttpURLConnection.HTTP_BAD_REQUEST == response.code()) {
            showHttpBadRequestState(response);
        }

        if (HttpURLConnection.HTTP_INTERNAL_ERROR == response.code()) {
            showHttpInternalErrorState();
        }
    }

    private void showHttpOkState(StateMediator<Object, UiState, String, String> stateMediator, Response<SourceItem.SourceResponse> response) {
        if (null == response.body()) return;

        final SourceItem.SourceResponse sourceResponse = response.body();
        final List<SourceItem.SourceNews> sourceNews = sourceResponse.getSources();
        for (short i = 0; i < sourceNews.size(); i++) {
            tabTitlesList.add(sourceNews.get(i).getName());
        }
        appUtils.showSnack(binding.conLayNewsHomeRoot, valueOf(stateMediator.getData()), getResources().getColor(R.color.teal_200), "OK", null);
    }

    private void showHttpBadRequestState(Response<SourceItem.SourceResponse> response) {
        if (null == response.errorBody()) return;
        try {
            JSONObject jsonErrorObject = null;
            try {
                jsonErrorObject = new JSONObject(response.errorBody().string());
            } catch (IOException ignored) {
            }
            final String status = jsonErrorObject.getString("status");
            final String code = jsonErrorObject.getString("code");
            final String message = jsonErrorObject.getString("message");
            appUtils.showSnack(binding.conLayNewsHomeRoot, message, getResources().getColor(R.color.teal_200), "OK", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showHttpInternalErrorState() {
        appUtils.showSnack(binding.conLayNewsHomeRoot, "Something is wrong. Try again!", getResources().getColor(R.color.teal_200), "OK", null);
    }

    private void showEmptyState(StateMediator<Object, UiState, String, String> stateMediator) {
        getActivity().runOnUiThread(() -> {
            hideLoading();
            binding.tvNoInternet.setVisibility(View.GONE);
            appUtils.showSnack(binding.conLayNewsHomeRoot, valueOf(stateMediator.getMessage()), getResources().getColor(R.color.teal_200), "OK", null);
        });
    }

    private void showErrorState(StateMediator<Object, UiState, String, String> stateMediator) {
        getActivity().runOnUiThread(() -> {
            hideLoading();
            binding.tvNoInternet.setVisibility(View.GONE);
            appUtils.showSnack(binding.conLayNewsHomeRoot, valueOf(stateMediator.getMessage()), getResources().getColor(R.color.teal_200), "OK", null);
            Log.d(TAG, "liveDataObserver: error: " + stateMediator.getMessage());
        });
    }

    private void showLoading() {
//        binding.tvLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
//        binding.tvLoading.setVisibility(View.GONE);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getWaitingState() {
        if (null == idlingResource) idlingResource = new ApiIdlingResource();
        return idlingResource;
    }
}
