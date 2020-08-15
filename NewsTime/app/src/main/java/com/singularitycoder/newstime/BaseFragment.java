package com.singularitycoder.newstime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.singularitycoder.newstime.categories.CategoriesFragment;
import com.singularitycoder.newstime.databinding.FragmentBaseBinding;
import com.singularitycoder.newstime.favorites.FavoritesFragment;
import com.singularitycoder.newstime.home.view.HomeFragment;
import com.singularitycoder.newstime.more.MoreFragment;

public final class BaseFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    @NonNull
    private final String TAG = "BaseFragment";

    @Nullable
    private FragmentBaseBinding binding;

    public BaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBaseBinding.inflate(inflater, container, false);
        final View view = binding.getRoot();
        setUpBottomNavigation();
        loadFragment(new HomeFragment());   // Loading the default fragment
        return view;
    }

    private void setUpBottomNavigation() {
        binding.bottomNavigationBase.setOnNavigationItemSelectedListener(this);
//        bottomNavigationView.setItemIconTintList(null);
    }

    private boolean loadFragment(Fragment fragment) {
        // Home or Base fragments should not contain addToBackStack. But if u want to navigate to home frag then add HomeFrag
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                binding.conLayBaseFragRoot.setBackgroundColor(getResources().getColor(android.R.color.white));
                break;

            case R.id.nav_categories:
                fragment = new CategoriesFragment();
                binding.conLayBaseFragRoot.setBackgroundColor(getResources().getColor(android.R.color.white));
                break;

            case R.id.nav_favorites:
                fragment = new FavoritesFragment();
                binding.conLayBaseFragRoot.setBackgroundColor(getResources().getColor(android.R.color.white));
                break;

            case R.id.nav_more:
                fragment = new MoreFragment();
                binding.conLayBaseFragRoot.setBackgroundColor(getResources().getColor(android.R.color.white));
                break;
        }

        return loadFragment(fragment);
    }
}