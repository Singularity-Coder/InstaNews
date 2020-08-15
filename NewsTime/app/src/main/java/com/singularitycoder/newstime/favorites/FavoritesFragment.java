package com.singularitycoder.newstime.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.singularitycoder.newstime.databinding.FragmentCategoriesBinding;
import com.singularitycoder.newstime.databinding.FragmentFavoritesBinding;

public final class FavoritesFragment extends Fragment {

    @NonNull
    private final String TAG = "FavoritesFragment";

    @Nullable
    private FragmentFavoritesBinding binding;

    public FavoritesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        final View view = binding.getRoot();
        return view;
    }
}