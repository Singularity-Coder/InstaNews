package com.singularitycoder.newstime.more;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.FragmentMoreBinding;
import com.singularitycoder.newstime.helper.AppSharedPreference;
import com.singularitycoder.newstime.helper.CustomDialogFragment;

import java.util.ArrayList;
import java.util.List;

public final class MoreFragment extends Fragment implements CustomDialogFragment.ListDialogListener {

    @NonNull
    private final String TAG = "MoreFragment";

    @NonNull
    private final List<MoreItem> moreList = new ArrayList<>();

    @Nullable
    private MoreAdapter moreAdapter;

    @Nullable
    private AppSharedPreference appSharedPreference;

    @Nullable
    private TextView tvMoreSubtitle;

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
        initialise();
        setUpToolBar();
        setUpRecyclerView();
        loadData();
        setClickListeners();
        return view;
    }

    private void initialise() {
        appSharedPreference = AppSharedPreference.getInstance(getContext());
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
        moreList.add(new MoreItem("News Layout", "Customise News Layout", R.drawable.ic_layout_24, android.R.color.black, android.R.color.darker_gray, R.color.colorPrimary));
        moreList.add(new MoreItem("App Language", "Choose App Language", R.drawable.ic_baseline_language_24, android.R.color.black, android.R.color.darker_gray, R.color.colorPrimary));
        moreList.add(new MoreItem("App Theme", "Choose an App Theme", R.drawable.ic_theme_24, android.R.color.black, android.R.color.darker_gray, R.color.colorPrimary));
        moreList.add(new MoreItem("Recently Viewed", "Recently Viewed News", R.drawable.ic_recently_viewed_24, android.R.color.black, android.R.color.darker_gray, R.color.colorPrimary));
        moreList.add(new MoreItem("Text-To-Speech", "Change TTS Settings", R.drawable.ic_text_to_speech_24, android.R.color.black, android.R.color.darker_gray, R.color.colorPrimary));
        moreList.add(new MoreItem("Change Font", "Change Text Style of News", R.drawable.ic_font_24, android.R.color.black, android.R.color.darker_gray, R.color.colorPrimary));
        moreList.add(new MoreItem("Change Text Size", "Change Text Size of News", R.drawable.ic_text_size_24, android.R.color.black, android.R.color.darker_gray, R.color.colorPrimary));
        moreAdapter.notifyDataSetChanged();
    }

    private void setClickListeners() {
        moreAdapter.setOnItemClickListener((position, tvMoreSubtitle) -> {

            if (1 == position) {
                btnShowCountriesDialog();
                this.tvMoreSubtitle = tvMoreSubtitle;
            }

            if (2 == position) {
                btnShowNewsLayoutsDialog();
                this.tvMoreSubtitle = tvMoreSubtitle;
            }

            if (3 == position) {
                btnShowAppLanguageDialog();
                this.tvMoreSubtitle = tvMoreSubtitle;
            }

            if (4 == position) {
                btnShowAppThemesDialog();
                this.tvMoreSubtitle = tvMoreSubtitle;
            }
        });
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

    private void btnShowCountriesDialog() {
        final Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "list");
        bundle.putString("KEY_LIST_DIALOG_TYPE", "countries");
        bundle.putString("KEY_TITLE", "Choose Country");
        bundle.putString("KEY_CONTEXT_TYPE", "fragment");
        bundle.putString("KEY_CONTEXT_OBJECT", "MoreFragment");
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

    private void btnShowNewsLayoutsDialog() {
        final Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "list");
        bundle.putString("KEY_LIST_DIALOG_TYPE", "layouts");
        bundle.putString("KEY_TITLE", "Choose Layouts");
        bundle.putString("KEY_CONTEXT_TYPE", "fragment");
        bundle.putString("KEY_CONTEXT_OBJECT", "MoreFragment");
        // compact - only one line each without body and author
        // standard - fancy at every 5th position
        // Details - full details
        // Fancy - Image with text on it
        bundle.putStringArray("KEY_LIST", new String[]{"Standard", "All Details", "Compact", "Fancy", "Only Text", "Only Image", "Only Headlines"});

        final DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setTargetFragment(this, 602);
        dialogFragment.setArguments(bundle);
        final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        final Fragment previousFragment = getActivity().getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) fragmentTransaction.remove(previousFragment);
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    private void btnShowAppLanguageDialog() {
        final Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "list");
        bundle.putString("KEY_LIST_DIALOG_TYPE", "languages");
        bundle.putString("KEY_TITLE", "Choose App Language");
        bundle.putString("KEY_CONTEXT_TYPE", "fragment");
        bundle.putString("KEY_CONTEXT_OBJECT", "MoreFragment");
        bundle.putStringArray("KEY_LIST", new String[]{"English", "Hindi", "Japanese", "Chinese"});

        final DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setTargetFragment(this, 603);
        dialogFragment.setArguments(bundle);
        final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        final Fragment previousFragment = getActivity().getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) fragmentTransaction.remove(previousFragment);
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    private void btnShowAppThemesDialog() {
        final Bundle bundle = new Bundle();
        bundle.putString("DIALOG_TYPE", "list");
        bundle.putString("KEY_LIST_DIALOG_TYPE", "themes");
        bundle.putString("KEY_TITLE", "Choose App Theme");
        bundle.putString("KEY_CONTEXT_TYPE", "fragment");
        bundle.putString("KEY_CONTEXT_OBJECT", "MoreFragment");
        bundle.putStringArray("KEY_LIST", new String[]{"Light Mode", "Dark Mode"});

        final DialogFragment dialogFragment = new CustomDialogFragment();
        dialogFragment.setTargetFragment(this, 604);
        dialogFragment.setArguments(bundle);
        final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        final Fragment previousFragment = getActivity().getSupportFragmentManager().findFragmentByTag("TAG_CustomDialogFragment");
        if (previousFragment != null) fragmentTransaction.remove(previousFragment);
        fragmentTransaction.addToBackStack(null);
        dialogFragment.show(fragmentTransaction, "TAG_CustomDialogFragment");
    }

    @Override
    public void onListDialogItemClick(String listItemText, String listDialogType) {
        if (("countries").equals(listDialogType)) {
            String[] countriesArray = {"in", "jp", "cn", "ru", "us", "gb", "il", "de", "br", "au"};
            String[] countriesArrayAlias = {"India", "Japan", "China", "Russia", "United States", "United Kingdom", "Israel", "Germany", "Brazil", "Australia"};
            for (int i = 0; i < countriesArrayAlias.length; i++) {
                if ((countriesArrayAlias[i]).equals(listItemText)) {
                    appSharedPreference.setCountry(countriesArray[i]);
                    tvMoreSubtitle.setText("Country: " + countriesArrayAlias[i]);
                }
            }
        }

        if (("layouts").equals(listDialogType)) {
            appSharedPreference.setNewsLayout(listItemText);
            tvMoreSubtitle.setText("Layout: " + listItemText);
        }

        if (("languages").equals(listDialogType)) {
            appSharedPreference.setAppLanguage(listItemText);
            tvMoreSubtitle.setText("App Language: " + listItemText);
        }

        if (("themes").equals(listDialogType)) {
            appSharedPreference.setAppTheme(listItemText);
            tvMoreSubtitle.setText("App Theme: " + listItemText);
        }
    }
}