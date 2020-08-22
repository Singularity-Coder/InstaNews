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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.test.espresso.IdlingResource;
import androidx.viewpager2.widget.ViewPager2;

import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.FragmentHomeTabBinding;
import com.singularitycoder.newstime.helper.ApiIdlingResource;
import com.singularitycoder.newstime.helper.AppSharedPreference;
import com.singularitycoder.newstime.helper.AppUtils;
import com.singularitycoder.newstime.helper.StateMediator;
import com.singularitycoder.newstime.helper.UiState;
import com.singularitycoder.newstime.helper.WebViewFragment;
import com.singularitycoder.newstime.home.adapter.NewsAdapter;
import com.singularitycoder.newstime.home.adapter.NewsViewPagerAdapter;
import com.singularitycoder.newstime.home.model.NewsItem;
import com.singularitycoder.newstime.home.viewmodel.NewsViewModel;
import com.singularitycoder.newstime.intro.DepthPageTransformer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

public final class HomeTabFragment extends Fragment {

    @NonNull
    private final String TAG = "MainFragment";

    @NonNull
    private final List<NewsItem.NewsArticle> newsList = new ArrayList<>();

    @NonNull
    private final AppUtils appUtils = new AppUtils();

    @Nullable
    private NewsViewModel newsViewModel;

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

    // todo detail view - share, browser, favorite - swipe to go to next article
    // todo audio speech to text
    // todo font and textSize control
    // todo view holder styles
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
        binding.toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

    private void initialise() {
        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
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

    private void getNewsData() {
        if (appUtils.hasInternet(getContext())) showOnlineState();
        else showOfflineState();
    }

    private void showOnlineState() {
        if (null != appSharedPreference.getCountry() && !("").equals(appSharedPreference.getCountry())) {
            strSelectedCountry = appSharedPreference.getCountry();
        }
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
        if (null == newsAdapter) return;

        newsAdapter.setNewsViewListener(position -> {
            final Bundle bundle = new Bundle();
            bundle.putString("SOURCE_URL", newsList.get(position).getSource().getName());
            bundle.putString("SOURCE_TITLE", newsList.get(position).getTitle());
            showFragment(bundle, R.id.con_lay_base_activity_root, new WebViewFragment());
        });
    }

    private void setUpSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(this::getNewsData);
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

    private void showFragment(@Nullable final Bundle bundle, final int parentLayout, @NonNull final Fragment fragment) {
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(parentLayout, fragment)
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

    @Override
    public void onResume() {
        super.onResume();
        getNewsData();
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
}
