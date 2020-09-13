package com.singularitycoder.newstime.home.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.iid.FirebaseInstanceId;
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
        AsyncTask.SERIAL_EXECUTOR.execute(() -> getDeviceToken());
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

    private void getDeviceToken() {
        try {
            FirebaseInstanceId
                    .getInstance()
                    .getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            if (!("").equals(token)) {
                                appSharedPreference.setFcmToken(token);
                            }
                        } else {
                            Toast.makeText(getContext(), "Something went wrong with FCM!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception ignored) {
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