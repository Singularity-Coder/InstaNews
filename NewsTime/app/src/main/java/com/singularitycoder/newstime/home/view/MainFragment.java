package com.singularitycoder.newstime.home.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.test.espresso.IdlingResource;

import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.home.adapter.NewsAdapter;
import com.singularitycoder.newstime.databinding.FragmentMainBinding;
import com.singularitycoder.newstime.helper.ApiIdlingResource;
import com.singularitycoder.newstime.helper.AppUtils;
import com.singularitycoder.newstime.helper.StateMediator;
import com.singularitycoder.newstime.helper.UiState;
import com.singularitycoder.newstime.helper.WebViewFragment;
import com.singularitycoder.newstime.home.model.NewsItem;
import com.singularitycoder.newstime.home.viewmodel.NewsViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static java.lang.String.valueOf;

public final class MainFragment extends Fragment {

    @NonNull
    private final String TAG = "MainFragment";

    @NonNull
    private final List<NewsItem.NewsArticle> newsList = new ArrayList<>();

    @NonNull
    private final AppUtils appUtils = new AppUtils();

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    private NewsViewModel newsViewModel;

    @Nullable
    private NewsItem.NewsResponse newsResponse;

    @Nullable
    private ApiIdlingResource idlingResource;

    @Nullable
    private NewsAdapter newsAdapter;

    @NonNull
    private String strSelectedCountry = "in";

    @NonNull
    private String strSelectedCategory = "technology";

    @Nullable
    private FragmentMainBinding binding;

    public MainFragment() {
    }

    public MainFragment(@NonNull final String strSelectedCategory) {
        this.strSelectedCategory = strSelectedCategory;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        final View viewRoot = binding.getRoot();
        initialise();
        setUpRecyclerView();
        setClickListeners();
        binding.swipeRefreshLayout.setOnRefreshListener(this::getNewsData);
        return viewRoot;
    }

    private void initialise() {
        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        new HomeFragment().setLoadNewsOnChangeListener((country) -> {
            strSelectedCountry = country;
            getNewsData();
        });
    }

    private void setUpRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerNews.setLayoutManager(layoutManager);
        newsAdapter = new NewsAdapter(newsList, getContext());
        binding.recyclerNews.setAdapter(newsAdapter);
        binding.recyclerNews.setItemAnimator(new DefaultItemAnimator());
    }

    private void getNewsData() {
        if (appUtils.hasInternet(getContext())) showOnlineState();
        else showOfflineState();
    }

    private void showOnlineState() {
        newsViewModel.getNewsFromRepository(
                strSelectedCountry,
                strSelectedCategory,
                idlingResource
        ).observe(getViewLifecycleOwner(), liveDataObserver());
    }

    private void showOfflineState() {
        binding.tvNoInternet.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setRefreshing(false);
        newsList.clear();

        // If offline get List from Room DB
        newsViewModel.getAllFromRoomDbThroughRepository().observe(getViewLifecycleOwner(), liveDataObserverForRoomDb());
    }

    private void setClickListeners() {
        newsAdapter.setNewsViewListener(position -> {
            final Bundle bundle = new Bundle();
            bundle.putString("SOURCE_URL", newsList.get(position).getSource().getName());
            bundle.putString("SOURCE_TITLE", newsList.get(position).getTitle());
            showFragment(bundle, R.id.con_lay_news_home_root, new WebViewFragment());
        });
    }

    private void showFragment(@Nullable final Bundle bundle, final int parentLayout, @NonNull final Fragment fragment) {
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(parentLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    private Observer<List<NewsItem.NewsArticle>> liveDataObserverForRoomDb() {
        Observer<List<NewsItem.NewsArticle>> observer = null;
        observer = (List<NewsItem.NewsArticle> newsArticles) -> {
            if (null != newsArticles) {
//                newsViewModel.deleteAllFromRoomDbFromRepository();
                newsList.clear();
                newsList.addAll(newsArticles);
                newsAdapter.notifyDataSetChanged();
            }
        };
        return observer;
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
            if (("NEWS").equals(stateMediator.getKey())) {
                newsList.clear();
                newsResponse = (NewsItem.NewsResponse) stateMediator.getData();
                List<NewsItem.NewsArticle> newsArticles = newsResponse.getArticles();
                newsList.addAll(newsArticles);
                newsAdapter.notifyDataSetChanged();
                binding.swipeRefreshLayout.setRefreshing(false);

                // Insert into DB
                for (NewsItem.NewsArticle newsArticle : newsResponse.getArticles()) {
//                    newsViewModel.insertIntoRoomDbFromRepository(newsArticle);
                }

                Toast.makeText(getContext(), valueOf(stateMediator.getData()), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();
        getNewsData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.dispose();
        binding = null;
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getWaitingState() {
        if (null == idlingResource) idlingResource = new ApiIdlingResource();
        return idlingResource;
    }
}
