package com.singularitycoder.newstime.more;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.FragmentMoreBinding;

import java.util.ArrayList;
import java.util.List;

public final class MoreFragment extends Fragment {

    @NonNull
    private final String TAG = "MoreFragment";

    @NonNull
    private final List<MoreItem> moreList = new ArrayList<>();

    @Nullable
    private MoreAdapter moreAdapter;

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
        setUpToolBar();
        setUpRecyclerView();
        loadData();
        setClickListeners();
        return view;
    }

    private void setUpToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(binding.toolbarLayout.toolbar);
        if (null != activity) {
            activity.setTitle(getString(R.string.app_name));
            activity.setTitle("More...");
        }
        binding.toolbarLayout.toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
        binding.toolbarLayout.toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
    }

    private void setUpRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        moreAdapter = new MoreAdapter(moreList, getContext());
        binding.recyclerView.setAdapter(moreAdapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void loadData() {
        moreList.add(new MoreItem(getAppVersion()));
        moreList.add(new MoreItem("News Location", "Choose a Country", R.drawable.ic_baseline_location_on_24, android.R.color.black, android.R.color.darker_gray, R.color.colorPrimary));
        moreList.add(new MoreItem("App Language", "Choose App Language", R.drawable.ic_baseline_language_24, android.R.color.black, android.R.color.darker_gray, R.color.colorPrimary));
        moreList.add(new MoreItem("News Layout", "Customise News Layout", R.drawable.ic_layout_24, android.R.color.black, android.R.color.darker_gray, R.color.colorPrimary));
        moreList.add(new MoreItem("App Theme", "Choose an App Theme", R.drawable.ic_theme_24, android.R.color.black, android.R.color.darker_gray, R.color.colorPrimary));
        moreAdapter.notifyDataSetChanged();
    }

    private void setClickListeners() {
        final String[] tabTitles = new String[]{"technology", "science", "business", "entertainment", "health", "sports"};
        final String[] tabTitlesFirstLetterCaps = new String[]{"Technology", "Science", "Business", "Entertainment", "Health", "Sports"};
//        moreAdapter.setCategoryClickListener(position -> {
//            for (int i = 0; i < tabTitles.length; i++) {
//                if (position == i) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("KEY_CATEGORY", tabTitlesFirstLetterCaps[i]);
//                    showFragment(bundle, R.id.con_lay_base_activity_root, new HomeTabFragment(tabTitles[i]));
//                }
//            }
//        });
    }

    @Nullable
    private String getAppVersion() {
        String version = null;
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version = "v " + packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
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
}