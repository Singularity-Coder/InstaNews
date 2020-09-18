package com.singularitycoder.newstime.weather.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.singularitycoder.newstime.databinding.FragmentFavoritesBinding;

public final class WeatherFragment extends Fragment {

    @NonNull
    private final String TAG = "WeatherFragment";

    @Nullable
    private FragmentFavoritesBinding binding;

    public WeatherFragment() {
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