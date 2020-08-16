package com.singularitycoder.newstime.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.FragmentHomeBinding;
import com.singularitycoder.newstime.helper.AppSharedPreference;

public final class HomeFragment extends Fragment {

    @NonNull
    private final String TAG = "HomeFragment";

    @NonNull
    private final String[] tabTitles = new String[]{"technology", "science", "business", "entertainment", "health", "sports"};

    @Nullable
    private AppSharedPreference appSharedPreference;

    @Nullable
    private FragmentHomeBinding binding;

    // todo detail view - share, browser, favorite - swipe to go to next article
    // todo audio speech to text
    // todo font and textSize control
    // todo view holder styles
    // todo Intro screens ViewPager2
    // todo dark mode
    // todo translations
    // todo offline mode not working
    // todo hide tabs on scroll
    // todo unit tests
    // todo dagger
    // todo Own image caching mechanism
    // todo material design

    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        final View viewRoot = binding.getRoot();
        initialise();
        setUpToolBar();
        initialiseViewPager();
        return viewRoot;
    }

    private void initialise() {
        appSharedPreference = AppSharedPreference.getInstance(getContext());
    }

    private void setUpToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbarLayout.toolbar);
        if (null != activity) {
            activity.setTitle(getString(R.string.app_name));
            activity.setTitle("Home");
        }
    }

    private void initialiseViewPager() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);
        binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(getActivity()));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }

    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            for (int i = 0; i < tabTitles.length; i++) {
                if (position == i) return new HomeTabFragment(tabTitles[i]);
            }
            return new HomeTabFragment(tabTitles[0]);
        }

        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }
}