package com.singularitycoder.newstime.view;

import android.os.Bundle;

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
import com.singularitycoder.newstime.databinding.ActivityMainBinding;
import com.singularitycoder.newstime.helper.AppUtils;
import com.singularitycoder.newstime.helper.CustomDialogFragment;

public final class MainActivity extends AppCompatActivity implements CustomDialogFragment.ListDialogListener {

    @NonNull
    private final String TAG = "MainActivity";

    @NonNull
    private final String[] tabTitles = new String[]{"technology", "science", "business", "entertainment", "health", "sports"};

    @NonNull
    private final AppUtils appUtils = new AppUtils();

    @Nullable
    private LoadNewsOnChangeListener loadNewsOnChangeListener;

    @Nullable
    private ActivityMainBinding binding;

    // todo hide tabs on scroll
    // todo unit tests
    // todo dagger
    // todo Own image caching mechanism
    // todo material design

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appUtils.setStatusBarColor(this, R.color.colorPrimaryDark);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpToolBar();
        setUpListeners();
        initialiseViewPager();
    }

    private void setUpToolBar() {
        setSupportActionBar(binding.toolbarLayout.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }

    private void setUpListeners() {
        binding.tvChooseCountry.setOnClickListener(view -> btnShowCountriesDialog());
    }

    private void initialiseViewPager() {
        getSupportActionBar().setElevation(0);
        binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(this));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }

    private void btnShowCountriesDialog() {
        final Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "list");
        bundle.putString("KEY_LIST_DIALOG_TYPE", "countries");
        bundle.putString("KEY_TITLE", "Choose Country");
        bundle.putString("KEY_CONTEXT_TYPE", "activity");
        bundle.putString("KEY_CONTEXT_OBJECT", "MainActivity");
        bundle.putStringArray("KEY_LIST", new String[]{"India", "Japan", "China", "Russia", "United States", "United Kingdom", "Israel", "Germany", "Brazil", "Australia"});

        final DialogFragment dialogFragment = new CustomDialogFragment();
//        dialogFragment.setTargetFragment(this, 601);
        dialogFragment.setArguments(bundle);
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        final Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
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
                if (position == i) return new MainFragment(tabTitles[i]);
            }
            return new MainFragment(tabTitles[0]);
        }

        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }

    interface LoadNewsOnChangeListener {
        void onChange(String country);
    }

    public final void setLoadNewsOnChangeListener(@NonNull final LoadNewsOnChangeListener loadNewsOnChangeListener) {
        this.loadNewsOnChangeListener = loadNewsOnChangeListener;
    }

    @Override
    public void onListDialogItemClick(String listItemText, String listDialogType) {
        if (("countries").equals(listDialogType)) {
            String[] countriesArray = {"in", "jp", "cn", "ru", "us", "gb", "il", "de", "br", "au"};
            String[] countriesArrayAlias = {"India", "Japan", "China", "Russia", "United States", "United Kingdom", "Israel", "Germany", "Brazil", "Australia"};
            for (int i = 0; i < countriesArrayAlias.length; i++) {
                if ((countriesArrayAlias[i]).equals(listItemText)) {
                    if (null != loadNewsOnChangeListener) this.loadNewsOnChangeListener.onChange(countriesArray[i]);
                }
            }
            binding.tvChooseCountry.setText("Country: " + listItemText);
        }
    }
}