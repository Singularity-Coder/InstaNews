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
import androidx.appcompat.app.AppCompatActivity;
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
import com.singularitycoder.newstime.databinding.FragmentHomeTabBinding;
import com.singularitycoder.newstime.helper.AppConstants;
import com.singularitycoder.newstime.helper.AppSharedPreference;
import com.singularitycoder.newstime.helper.AppUtils;
import com.singularitycoder.newstime.helper.NetworkStateListenerBuilder;
import com.singularitycoder.newstime.helper.espresso.ApiIdlingResource;
import com.singularitycoder.newstime.helper.retrofit.StateMediator;
import com.singularitycoder.newstime.helper.retrofit.UiState;
import com.singularitycoder.newstime.home.adapter.NewsAdapter;
import com.singularitycoder.newstime.home.adapter.NewsViewPagerAdapter;
import com.singularitycoder.newstime.home.model.NewsItem;
import com.singularitycoder.newstime.home.viewmodel.NewsViewModel;
import com.singularitycoder.newstime.sources.viewmodel.SourcesViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static com.singularitycoder.newstime.helper.AppConstants.KEY_GET_NEWS_LIST_API_SUCCESS_STATE;
import static com.singularitycoder.newstime.helper.AppConstants.KEY_GET_NEWS_LIST_FROM_SOURCE_API_SUCCESS_STATE;
import static java.lang.String.valueOf;

public final class HomeTabFragment extends Fragment {

    @NonNull
    private final String TAG = "HomeTabFragment";

    @NonNull
    private final List<NewsItem.NewsArticle> newsList = new ArrayList<>();

    @NonNull
    private final AppUtils appUtils = new AppUtils();

    @Nullable
    private NewsViewModel newsViewModel;

    @Nullable
    private SourcesViewModel sourcesViewModel;

    @Nullable
    private NewsItem.NewsResponse newsResponse;

    @Nullable
    private ApiIdlingResource idlingResource;

    @Nullable
    private NewsAdapter newsAdapter;

    @Nullable
    private AppSharedPreference appSharedPreference;

    @NonNull
    private String strSelectedCountry = "in";

    @NonNull
    private String strSelectedCategory = "technology";

    @Nullable
    private FragmentHomeTabBinding binding;

    // todo detail view - share, browser, favorite - swipe to go to next article, clickable image FRESCO, swipe down to dismiss
    // todo Weather APi integration
    // todo webview features
    // todo draggable settings list to adjust settings position

    // todo audio speech to text
    // todo font and textSize control
    // todo shimmer
    // todo dark mode
    // todo translations
    // todo offline mode not working
    // todo hide tabs on scroll
    // todo unit tests
    // todo dagger
    // todo Own image caching mechanism
    // todo material design

    public HomeTabFragment() {
    }

    public HomeTabFragment(@NonNull final String strSelectedCategory) {
        this.strSelectedCategory = strSelectedCategory;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeTabBinding.inflate(inflater, container, false);
        final View viewRoot = binding.getRoot();
        getBundleData();
        setUpToolBar();
        initialise();
        setUpLayout();
        setClickListeners();
        setUpSwipeRefresh();
        return viewRoot;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != getArguments()) {
            if (null != getArguments().getString("KEY_SOURCE_ID") && !("").equals(getArguments().getString("KEY_SOURCE_ID"))) {
                getNewsListFromSourceFromApi();
            }
        } else {
            getNewsListFromApi();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getBundleData() {
        if (null != getArguments()) {
            binding.toolbar.setVisibility(View.VISIBLE);
        } else {
            binding.toolbar.setVisibility(View.GONE);
        }
    }

    private void setUpToolBar() {
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null != activity) {
            activity.setSupportActionBar(binding.toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (null != getArguments()) activity.setTitle(getArguments().getString("KEY_CATEGORY"));
            else activity.setTitle("News");
        }
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        binding.toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

    private void initialise() {
        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        sourcesViewModel = new ViewModelProvider(this).get(SourcesViewModel.class);
        appSharedPreference = AppSharedPreference.getInstance(getContext());
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
            appUtils.addFragment(getActivity(), bundle, R.id.con_lay_base_activity_root, new HomeDetailFragment());
        });

        binding.btnAdd.setOnClickListener(view -> appUtils.addFragment(getActivity(), null, R.id.con_lay_base_activity_root, new CategoriesFragment()));
    }

    private void setUpSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(this::getNewsListFromApi);
        binding.swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.purple_500));
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

    private void getNewsListFromApi() {
        if (appUtils.hasInternet(getContext())) showNewsListOnlineState();
        else showNewsListOfflineState();
    }

    private void showNewsListOnlineState() {
        if (null != appSharedPreference.getCountry() && !("").equals(appSharedPreference.getCountry())) {
            strSelectedCountry = appSharedPreference.getCountry();
        }
        newsViewModel.getNewsFromRepository(
                strSelectedCountry,
                strSelectedCategory,
                idlingResource
        ).observe(getViewLifecycleOwner(), observeLiveData());
    }

    private void showNewsListOfflineState() {
        binding.tvNoInternet.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setRefreshing(false);
        newsList.clear();

        // If offline get List from Room DB
        newsViewModel.getAllFromRoomDbThroughRepository().observe(getViewLifecycleOwner(), liveDataObserverForRoomDb());
    }

    private void getNewsListFromSourceFromApi() {
        new NetworkStateListenerBuilder(getContext())
                .setOnlineMobileFunction(() -> showOnlineState())
                .setOnlineWifiFunction(() -> showOnlineState())
                .setOfflineFunction(() -> showOfflineState())
                .build();
    }

    private Void showOnlineState() {
        binding.tvNoInternet.setVisibility(View.GONE);
        final String apiKey = AppConstants.NEWS_API_KEY;
        final String sourceId = getArguments().getString("KEY_SOURCE_ID");
        sourcesViewModel.getNewsListFromNewsSourceFromRepository(sourceId, apiKey, idlingResource).observe(getViewLifecycleOwner(), observeLiveData());
        return null;
    }

    private Void showOfflineState() {
        binding.tvNoInternet.setVisibility(View.VISIBLE);
        hideLoading();
        return null;
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
            if ((KEY_GET_NEWS_LIST_API_SUCCESS_STATE).equals(stateMediator.getKey())) {
                showNewsListSuccessState(stateMediator);
            }

            if ((KEY_GET_NEWS_LIST_FROM_SOURCE_API_SUCCESS_STATE).equals(stateMediator.getKey())) {
                showNewsListSuccessState(stateMediator);
            }
        });
    }

    private void showNewsListSuccessState(StateMediator<Object, UiState, String, String> stateMediator) {
        newsList.clear();
        hideLoading();
        binding.tvNothing.setVisibility(View.GONE);
        binding.lottieViewNothing.setVisibility(View.GONE);

        final Response<NewsItem.NewsResponse> response = (Response<NewsItem.NewsResponse>) stateMediator.getData();

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

    private void showHttpOkState(StateMediator<Object, UiState, String, String> stateMediator, Response<NewsItem.NewsResponse> response) {
        if (null == response.body()) return;
        newsResponse = (NewsItem.NewsResponse) response.body();
        final List<NewsItem.NewsArticle> newsArticles = newsResponse.getArticles();
        newsList.addAll(newsArticles);
        final String stringResponse = new Gson().toJson(newsResponse);
        Log.d(TAG, "showSuccessState: resp: " + stringResponse);

        if (null != newsAdapter) {
            newsAdapter.notifyDataSetChanged();
        }
        if (null != binding.viewPager.getAdapter()) {
            binding.viewPager.getAdapter().notifyDataSetChanged();
        }
        binding.swipeRefreshLayout.setRefreshing(false);

        // Insert into DB
//        for (NewsItem.NewsArticle newsArticle : newsResponse.getArticles()) {
//            newsViewModel.insertIntoRoomDbFromRepository(newsArticle);
//        }

        binding.tvNoInternet.setVisibility(View.GONE);
        appUtils.showSnack(binding.getRoot(), valueOf(stateMediator.getData()), getResources().getColor(R.color.white), "OK", null);

        if (0 == newsList.size()) {
            binding.tvNothing.setVisibility(View.VISIBLE);
            binding.lottieViewNothing.setVisibility(View.VISIBLE);
            binding.lottieViewNothing.setAnimation(R.raw.nothing);
            binding.lottieViewNothing.playAnimation();
        }
    }

    private void showHttpBadRequestState(Response<NewsItem.NewsResponse> response) {
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
            appUtils.showSnack(binding.getRoot(), message, getResources().getColor(R.color.white), "OK", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showHttpInternalErrorState() {
        appUtils.showSnack(binding.getRoot(), "Something is wrong. Try again!", getResources().getColor(R.color.white), "OK", null);
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

    private void showLoading() {
        binding.swipeRefreshLayout.setRefreshing(true);
    }

    private void hideLoading() {
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getWaitingState() {
        if (null == idlingResource) idlingResource = new ApiIdlingResource();
        return idlingResource;
    }
}
