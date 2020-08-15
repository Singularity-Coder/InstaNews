package com.singularitycoder.newstime.home.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayoutMediator;
import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.FragmentHomeBinding;
import com.singularitycoder.newstime.helper.ApiEndPoints;
import com.singularitycoder.newstime.helper.AppSharedPreference;
import com.singularitycoder.newstime.helper.CustomDialogFragment;

public final class HomeFragment extends Fragment implements CustomDialogFragment.ListDialogListener {

    @NonNull
    private final String TAG = "HomeFragment";

    @NonNull
    private final String[] tabTitles = new String[]{"technology", "science", "business", "entertainment", "health", "sports"};

    @Nullable
    private LoadNewsListener loadNewsListener;

    @Nullable
    private AppSharedPreference appSharedPreference;

    @Nullable
    private FragmentHomeBinding binding;

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
        setUpListeners();
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

    private void setUpListeners() {
        binding.tvChooseCountry.setOnClickListener(view -> btnShowCountriesDialog());
    }

    private void initialiseViewPager() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setElevation(0);
        binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(getActivity()));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }

    private void btnShowCountriesDialog() {
        final Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "list");
        bundle.putString("KEY_LIST_DIALOG_TYPE", "countries");
        bundle.putString("KEY_TITLE", "Choose Country");
        bundle.putString("KEY_CONTEXT_TYPE", "fragment");
        bundle.putString("KEY_CONTEXT_OBJECT", "HomeFragment");
        bundle.putStringArray("KEY_LIST", new String[]{"India", "Japan", "China", "Russia", "United States", "United Kingdom", "Israel", "Germany", "Brazil", "Australia"});

        final DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setTargetFragment(this, 601);
        dialogFragment.setArguments(bundle);
        final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        final Fragment previousFragment = getActivity().getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) fragmentTransaction.remove(previousFragment);
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
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

    interface LoadNewsListener {
        void onChange(String country);
    }

    public final void setLoadNewsListener(@NonNull final LoadNewsListener loadNewsListener) {
        this.loadNewsListener = loadNewsListener;
    }

    @Override
    public void onListDialogItemClick(String listItemText, String listDialogType) {
        if (("countries").equals(listDialogType)) {
            String[] countriesArray = {"in", "jp", "cn", "ru", "us", "gb", "il", "de", "br", "au"};
            String[] countriesArrayAlias = {"India", "Japan", "China", "Russia", "United States", "United Kingdom", "Israel", "Germany", "Brazil", "Australia"};
            for (int i = 0; i < countriesArrayAlias.length; i++) {
                if ((countriesArrayAlias[i]).equals(listItemText)) {
                    if (null != loadNewsListener) this.loadNewsListener.onChange(countriesArray[i]);
                    appSharedPreference.setCountry(countriesArray[i]);
                }
            }
            binding.tvChooseCountry.setText("Country: " + listItemText);
        }
    }
}