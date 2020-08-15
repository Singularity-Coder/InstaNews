package com.singularitycoder.newstime.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.singularitycoder.newstime.databinding.FragmentMoreBinding;

public final class MoreFragment extends Fragment {

    @NonNull
    private final String TAG = "MoreFragment";

    @Nullable
    private FragmentMoreBinding binding;

    public MoreFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        final View view = binding.getRoot();
        return view;
    }
}