package com.singularitycoder.newstime.search;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.test.espresso.IdlingResource;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.categories.CategoriesFragment;
import com.singularitycoder.newstime.databinding.FragmentSearchBinding;
import com.singularitycoder.newstime.helper.ApiIdlingResource;
import com.singularitycoder.newstime.helper.AppSharedPreference;
import com.singularitycoder.newstime.helper.AppUtils;
import com.singularitycoder.newstime.helper.StateMediator;
import com.singularitycoder.newstime.helper.UiState;
import com.singularitycoder.newstime.home.adapter.NewsAdapter;
import com.singularitycoder.newstime.home.adapter.NewsViewPagerAdapter;
import com.singularitycoder.newstime.home.model.NewsItem;
import com.singularitycoder.newstime.home.view.HomeDetailFragment;
import com.singularitycoder.newstime.home.view.ZoomOutPageTransformer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static java.lang.String.valueOf;

public final class SearchFragment extends Fragment {

    private final int REQUEST_CODE_RECOGNISE_SPEECH = 100;

    @NonNull
    private final String TAG = "SearchFragment";

    @NonNull
    private final List<NewsItem.NewsArticle> newsList = new ArrayList<>();

    @NonNull
    private final AppUtils appUtils = new AppUtils();

    @Nullable
    private SearchViewModel searchViewModel;

    @Nullable
    private NewsItem.NewsResponse newsResponse;

    @Nullable
    private ApiIdlingResource idlingResource;

    @Nullable
    private NewsAdapter newsAdapter;

    @Nullable
    private AppSharedPreference appSharedPreference;

    @Nullable
    private FragmentSearchBinding binding;

    public SearchFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        final View viewRoot = binding.getRoot();
        getBundleData();
        initialise();
        setUpLayout();
        setClickListeners();
        setUpSwipeRefresh();
        return viewRoot;
    }

    private void getBundleData() {
        if (null != getArguments()) {
        } else {
        }
    }

    private void initialise() {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        appSharedPreference = AppSharedPreference.getInstance(getContext());
        binding.etSearch.setText("News");
    }

    private void setUpLayout() {
        if (null == appSharedPreference) {
            binding.viewPager.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
            setUpRecyclerView();
            return;
        }

        if (("Vertical Swipe").equals(appSharedPreference.getNewsLayout()) || ("Horizontal Swipe").equals(appSharedPreference.getNewsLayout())) {
            binding.viewPager.setVisibility(View.VISIBLE);
            binding.swipeRefreshLayout.setVisibility(View.GONE);
            setUpViewPager();
        } else {
            binding.viewPager.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
            setUpRecyclerView();
        }
    }

    private void setUpRecyclerView() {
        if (("Grid View").equals(appSharedPreference.getNewsLayout())) {
            binding.recyclerNews.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            final float scale = getResources().getDisplayMetrics().density;
            final int sizeInDp = 16;
            final int sizeInPixels = (int) (sizeInDp * scale + 0.5f);
            binding.recyclerNews.setPadding(0, 0, sizeInPixels, 0);
            binding.recyclerNews.setClipToPadding(false);
            binding.recyclerNews.setClipChildren(true);
        } else {
            binding.recyclerNews.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        newsAdapter = new NewsAdapter(newsList, getContext());
        binding.recyclerNews.setAdapter(newsAdapter);
        binding.recyclerNews.setItemAnimator(new DefaultItemAnimator());
    }

    private void getSearchData() {
        if (appUtils.hasInternet(getContext())) showOnlineState();
        else showOfflineState();
    }

    private void showOnlineState() {
        searchViewModel.getSearchResultsFromRepository(
                valueOf(binding.etSearch.getText()),
                idlingResource
        ).observe(getViewLifecycleOwner(), liveDataObserver());
    }

    private void showOfflineState() {
        binding.tvNoInternet.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setRefreshing(false);
        newsList.clear();
    }

    private void setClickListeners() {
        if (null == newsAdapter) return;

        newsAdapter.setNewsViewListener(position -> {
            final Bundle bundle = new Bundle();
            bundle.putString("NEWS_IMAGE_URL", newsList.get(position).getUrlToImage());
            bundle.putString("NEWS_TITLE", newsList.get(position).getTitle());
            bundle.putString("NEWS_DESCRIPTION", newsList.get(position).getDescription());
            bundle.putString("NEWS_CONTENT", newsList.get(position).getContent());
            bundle.putString("NEWS_AUTHOR", newsList.get(position).getAuthor());
            bundle.putString("NEWS_SOURCE", newsList.get(position).getSource().getName());
            bundle.putString("NEWS_DATE", newsList.get(position).getPublishedAt());
            appUtils.showFragment(getActivity(), bundle, R.id.con_lay_base_activity_root, new HomeDetailFragment());
        });

        binding.btnAdd.setOnClickListener(view -> appUtils.showFragment(getActivity(), null, R.id.con_lay_base_activity_root, new CategoriesFragment()));

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getSearchData();
            }
        });

        binding.ibAudioSearch.setOnClickListener(v -> {
            if (null != getActivity()) startSpeechToText();
        });

        binding.ivBack.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStackImmediate());
    }

    private void setUpSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(this::getSearchData);
        binding.swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
    }

    private void setUpViewPager() {
        binding.viewPager.setAdapter(new NewsViewPagerAdapter(newsList, getContext()));

        if (("Vertical Swipe").equals(appSharedPreference.getNewsLayout())) {
            binding.viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        }

        if (("Horizontal Swipe").equals(appSharedPreference.getNewsLayout())) {
            binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        }

        binding.viewPager.setPageTransformer(new ZoomOutPageTransformer());
        binding.viewPager.registerOnPageChangeCallback(getOnPageChangeCallback());
//        binding.viewPager.setCurrentItem(0);
//        binding.viewPager.getAdapter().notifyDataSetChanged();
    }

    @NotNull
    private ViewPager2.OnPageChangeCallback getOnPageChangeCallback() {
        return new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == newsList.size() - 1) {
                    // last page. make button text to GOT IT

                } else {
                    // still pages are left

                }
            }
        };
    }

    private void showLoading() {
        binding.swipeRefreshLayout.setRefreshing(true);
    }

    private void hideLoading() {
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    private Observer<StateMediator<Object, UiState, String, String>> liveDataObserver() {
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
            if (("SEARCH").equals(stateMediator.getKey())) {
                newsList.clear();
                newsResponse = (NewsItem.NewsResponse) stateMediator.getData();
                final List<NewsItem.NewsArticle> newsArticles = newsResponse.getArticles();
                newsList.addAll(newsArticles);
                final String response = new Gson().toJson(newsResponse);
                Log.d(TAG, "showSuccessState: resp: " + response);

                if (null != newsAdapter) {
                    newsAdapter.notifyDataSetChanged();
                }
                if (null != binding.viewPager.getAdapter()) {
                    binding.viewPager.getAdapter().notifyDataSetChanged();
                }
                binding.swipeRefreshLayout.setRefreshing(false);

                // Insert into DB
//                for (NewsItem.NewsArticle newsArticle : newsResponse.getArticles()) {
//                    newsViewModel.insertIntoRoomDbFromRepository(newsArticle);
//                }

//                Toast.makeText(getContext(), valueOf(stateMediator.getData()), Toast.LENGTH_SHORT).show();
                hideLoading();
                binding.tvNoInternet.setVisibility(View.GONE);
            }
        });
    }

    private void showEmptyState(StateMediator<Object, UiState, String, String> stateMediator) {
        getActivity().runOnUiThread(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.progressCircular.setVisibility(View.GONE);
            binding.tvNothing.setVisibility(View.VISIBLE);
            binding.tvNothing.setText("Nothing to show :(");
            hideLoading();
            binding.tvNoInternet.setVisibility(View.GONE);
            Toast.makeText(getContext(), valueOf(stateMediator.getMessage()), Toast.LENGTH_LONG).show();
        });
    }

    private void showErrorState(StateMediator<Object, UiState, String, String> stateMediator) {
        getActivity().runOnUiThread(() -> {
            binding.progressCircular.setVisibility(View.GONE);
            binding.tvNothing.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setRefreshing(false);
            hideLoading();
            binding.tvNoInternet.setVisibility(View.GONE);
            Toast.makeText(getContext(), valueOf(stateMediator.getMessage()), Toast.LENGTH_LONG).show();
            Log.d(TAG, "liveDataObserver: error: " + stateMediator.getMessage());
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

    @Override
    public void onResume() {
        super.onResume();
        getSearchData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getWaitingState() {
        if (null == idlingResource) idlingResource = new ApiIdlingResource();
        return idlingResource;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RECOGNISE_SPEECH && resultCode == RESULT_OK && null != data) {
            final ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            binding.etSearch.setText(valueOf(result.get(0)));
            getSearchData();
        }
    }
}
