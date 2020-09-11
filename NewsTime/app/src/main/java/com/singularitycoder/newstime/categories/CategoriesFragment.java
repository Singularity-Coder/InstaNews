package com.singularitycoder.newstime.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.FragmentBaseBinding;
import com.singularitycoder.newstime.databinding.FragmentCategoriesBinding;
import com.singularitycoder.newstime.helper.ApiIdlingResource;
import com.singularitycoder.newstime.helper.AppUtils;
import com.singularitycoder.newstime.helper.WebViewFragment;
import com.singularitycoder.newstime.home.adapter.NewsAdapter;
import com.singularitycoder.newstime.home.model.NewsItem;
import com.singularitycoder.newstime.home.view.HomeTabFragment;
import com.singularitycoder.newstime.home.viewmodel.NewsViewModel;

import java.util.ArrayList;
import java.util.List;

public final class CategoriesFragment extends Fragment {

    @NonNull
    private final String TAG = "CategoriesFragment";

    @NonNull
    private final List<CategoriesItem> categoriesList = new ArrayList<>();

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @Nullable
    private CategoriesAdapter categoriesAdapter;

    @Nullable
    private FragmentCategoriesBinding binding;

    public CategoriesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        final View view = binding.getRoot();
        setUpToolBar();
        setUpRecyclerView();
        setCategories();
        setUpSwipeRefresh();
        setClickListeners();
        return view;
    }

    private void setUpToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbarLayout.toolbar);
        if (null != activity) {
            activity.setTitle(getString(R.string.app_name));
            activity.setTitle("Categories");
//            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbarLayout.toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
        binding.toolbarLayout.toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
//        binding.toolbarLayout.toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
    }

    private void setUpRecyclerView() {
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        categoriesAdapter = new CategoriesAdapter(categoriesList, getContext());
        binding.recyclerView.setAdapter(categoriesAdapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setCategories() {
        if (appUtils.hasInternet(getContext())) showOnlineState();
        else showOfflineState();
    }

    private void showOnlineState() {
        binding.tvNoInternet.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
        categoriesList.clear();
        categoriesList.add(new CategoriesItem("https://cdn.pixabay.com/photo/2011/12/14/12/11/astronaut-11080_960_720.jpg", "Technology"));
        categoriesList.add(new CategoriesItem("https://cdn.pixabay.com/photo/2014/02/27/16/09/microscope-275984_960_720.jpg", "Science"));
        categoriesList.add(new CategoriesItem("https://cdn.pixabay.com/photo/2014/10/23/10/10/dollar-499481_960_720.jpg", "Business"));
        categoriesList.add(new CategoriesItem("https://cdn.pixabay.com/photo/2015/03/08/17/25/musician-664432_960_720.jpg", "Entertainment"));
        categoriesList.add(new CategoriesItem("https://cdn.pixabay.com/photo/2018/01/01/01/56/yoga-3053488_960_720.jpg", "Health"));
        categoriesList.add(new CategoriesItem("https://cdn.pixabay.com/photo/2012/11/28/11/11/quarterback-67701_960_720.jpg", "Sports"));
        categoriesAdapter.notifyDataSetChanged();
    }

    private void showOfflineState() {
        binding.tvNoInternet.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    private void setClickListeners() {
        final String[] tabTitles = new String[]{"technology", "science", "business", "entertainment", "health", "sports"};
        final String[] tabTitlesFirstLetterCaps = new String[]{"Technology", "Science", "Business", "Entertainment", "Health", "Sports"};
        categoriesAdapter.setCategoryClickListener(position -> {
            for (int i = 0; i < tabTitles.length; i++) {
                if (position == i) {
                    Bundle bundle = new Bundle();
                    bundle.putString("KEY_CATEGORY", tabTitlesFirstLetterCaps[i]);
                    showFragment(bundle, R.id.con_lay_base_activity_root, new HomeTabFragment(tabTitles[i]));
                }
            }
        });
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

    private void setUpSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(this::setCategories);
        binding.swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
    }
}