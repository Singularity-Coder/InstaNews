package com.singularitycoder.newstime.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.singularitycoder.newstime.databinding.FragmentBaseBinding;
import com.singularitycoder.newstime.databinding.FragmentCategoriesBinding;

public final class CategoriesFragment extends Fragment {

    @NonNull
    private final String TAG = "CategoriesFragment";

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
        return view;
    }
}