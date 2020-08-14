package com.singularitycoder.newstime.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.test.espresso.IdlingResource;

import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.adapter.NewsAdapter;
import com.singularitycoder.newstime.databinding.ActivityMainBinding;
import com.singularitycoder.newstime.helper.ApiIdlingResource;
import com.singularitycoder.newstime.helper.AppUtils;
import com.singularitycoder.newstime.helper.CustomDialogFragment;
import com.singularitycoder.newstime.helper.StateMediator;
import com.singularitycoder.newstime.helper.UiState;
import com.singularitycoder.newstime.helper.WebViewFragment;
import com.singularitycoder.newstime.model.NewsItem;
import com.singularitycoder.newstime.viewmodel.NewsViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

import static java.lang.String.valueOf;

public final class MainActivity extends AppCompatActivity implements CustomDialogFragment.ListDialogListener {

    @NonNull
    private final String TAG = "MainActivity";

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
    private ProgressDialog progressDialog;

    @Nullable
    private NewsAdapter newsAdapter;

    @NonNull
    private String strSelectedCountry = "in";

    @NonNull
    private String strSelectedCategory = "technology";

    @Nullable
    private ActivityMainBinding binding;

    // todo unit tests
    // todo dagger
    // todo Own image caching mechanism
    // todo material design

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appUtils.setStatusBarColor(this, R.color.colorPrimaryDark);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialise();
        setUpToolBar();
        setUpRecyclerView();
        getNewsData();
        setClickListeners();
        binding.swipeRefreshLayout.setOnRefreshListener(this::getNewsData);
    }

    private void initialise() {
        progressDialog = new ProgressDialog(this);
        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
    }

    private void setUpToolBar() {
        setSupportActionBar(binding.toolbarLayout.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }

    private void setUpRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerNews.setLayoutManager(layoutManager);
        newsAdapter = new NewsAdapter(newsList, this);
        binding.recyclerNews.setAdapter(newsAdapter);
        binding.recyclerNews.setItemAnimator(new DefaultItemAnimator());
    }

    private void getNewsData() {
        if (appUtils.hasInternet(this)) showOnlineState();
        else showOfflineState();
    }

    private void showOnlineState() {
        newsViewModel.getNewsFromRepository(
                strSelectedCountry,
                strSelectedCategory,
                idlingResource
        ).observe(MainActivity.this, liveDataObserver());
    }

    private void showOfflineState() {
        binding.tvNoInternet.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setRefreshing(false);
        newsList.clear();

        // If offline get List from Room DB
        newsViewModel.getAllFromRoomDbThroughRepository().observe(this, liveDataObserverForRoomDb());
    }

    private void setClickListeners() {
        binding.tvChooseCountry.setOnClickListener(view -> btnShowCountriesDialog());
        binding.tvChooseCategory.setOnClickListener(view -> btnShowCategoriesDialog());
        newsAdapter.setNewsViewListener(position -> {
            final Bundle bundle = new Bundle();
            bundle.putString("SOURCE_URL", newsList.get(position).getSource().getName());
            bundle.putString("SOURCE_TITLE", newsList.get(position).getTitle());
            showFragment(bundle, R.id.con_lay_news_home_root, new WebViewFragment());
        });
    }

    private void btnShowCountriesDialog() {
        final Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "list");
        bundle.putString("KEY_LIST_DIALOG_TYPE", "countries");
        bundle.putString("KEY_TITLE", "Choose Country");
        bundle.putString("KEY_CONTEXT_TYPE", "activity");
        bundle.putString("KEY_CONTEXT_OBJECT", "MainActivity");
        bundle.putStringArray("KEY_LIST", new String[]{"India", "Japan", "China", "Russia", "United States", "United Kingdom", "Israel", "Germany", "Brazil", "Australia"});

        final DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        final Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) fragmentTransaction.remove(previousFragment);
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    private void btnShowCategoriesDialog() {
        final Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "list");
        bundle.putString("KEY_LIST_DIALOG_TYPE", "categories");
        bundle.putString("KEY_TITLE", "Choose Category");
        bundle.putString("KEY_CONTEXT_TYPE", "activity");
        bundle.putString("KEY_CONTEXT_OBJECT", "MainActivity");
        bundle.putStringArray("KEY_LIST", new String[]{"business", "entertainment", "health", "science", "sports", "technology"});

        final DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setArguments(bundle);
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        final Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) fragmentTransaction.remove(previousFragment);
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    private void showFragment(@Nullable final Bundle bundle, final int parentLayout, @NonNull final Fragment fragment) {
        fragment.setArguments(bundle);
        getSupportFragmentManager()
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
        if (appUtils.hasInternet(this)) {
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
        runOnUiThread(() -> {
            progressDialog.setMessage(valueOf(stateMediator.getMessage()));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            showLoading();
        });
    }

    private void showSuccessState(StateMediator<Object, UiState, String, String> stateMediator) {
        runOnUiThread(() -> {
            if (("NEWS").equals(stateMediator.getKey())) {
                newsList.clear();
                newsResponse = (NewsItem.NewsResponse) stateMediator.getData();
                List<NewsItem.NewsArticle> newsArticles = newsResponse.getArticles();
                newsList.addAll(newsArticles);
                newsAdapter.notifyDataSetChanged();
                binding.swipeRefreshLayout.setRefreshing(false);

                // Insert into DB
                for (NewsItem.NewsArticle newsArticle : newsResponse.getArticles()) {
//                                newsViewModel.insertIntoRoomDbFromRepository(newsArticle);
                }

                Toast.makeText(MainActivity.this, valueOf(stateMediator.getData()), Toast.LENGTH_SHORT).show();
                hideLoading();
                binding.tvNoInternet.setVisibility(View.GONE);
            }
        });
    }

    private void showEmptyState(StateMediator<Object, UiState, String, String> stateMediator) {
        runOnUiThread(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.progressCircular.setVisibility(View.GONE);
            binding.tvNothing.setVisibility(View.VISIBLE);
            binding.tvNothing.setText("Nothing to show :(");
            hideLoading();
            binding.tvNoInternet.setVisibility(View.GONE);
            Toast.makeText(this, valueOf(stateMediator.getMessage()), Toast.LENGTH_LONG).show();
        });
    }

    private void showErrorState(StateMediator<Object, UiState, String, String> stateMediator) {
        runOnUiThread(() -> {
            binding.progressCircular.setVisibility(View.GONE);
            binding.tvNothing.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setRefreshing(false);
            hideLoading();
            binding.tvNoInternet.setVisibility(View.GONE);
            Toast.makeText(this, valueOf(stateMediator.getMessage()), Toast.LENGTH_LONG).show();
            Log.d(TAG, "liveDataObserver: error: " + stateMediator.getMessage());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        binding = null;
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getWaitingState() {
        if (null == idlingResource) idlingResource = new ApiIdlingResource();
        return idlingResource;
    }

    @Override
    public void onListDialogItemClick(String listItemText, String listDialogType) {
        if (("countries").equals(listDialogType)) {
            String[] countriesArray = {"in", "jp", "cn", "ru", "us", "gb", "il", "de", "br", "au"};
            String[] countriesArrayAlias = {"India", "Japan", "China", "Russia", "United States", "United Kingdom", "Israel", "Germany", "Brazil", "Australia"};
            for (int i = 0; i < countriesArrayAlias.length; i++) {
                if ((countriesArrayAlias[i]).equals(listItemText)) {
                    strSelectedCountry = countriesArray[i];
                }
            }
            binding.tvChooseCountry.setText("Country: " + listItemText);
            getNewsData();
        }

        if (("categories").equals(listDialogType)) {
            binding.tvChooseCategory.setText("Category: " + listItemText);
            strSelectedCategory = listItemText;
            getNewsData();
        }
    }
}