package com.singularitycoder.newstime.intro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.singularitycoder.newstime.BaseActivity;
import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.ActivityIntroBinding;
import com.singularitycoder.newstime.helper.AppSharedPreference;
import com.singularitycoder.newstime.helper.AppUtils;

import org.jetbrains.annotations.NotNull;

public final class IntroActivity extends AppCompatActivity {

    private int[] layouts = new int[]{
            R.layout.fragment_intro,
            R.layout.fragment_intro,
            R.layout.fragment_intro,
            R.layout.fragment_intro};

    @NotNull
    AppUtils appUtils = AppUtils.getInstance();

    @Nullable
    private AppSharedPreference appSharedPreference;

    @Nullable
    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSharedPreference = AppSharedPreference.getInstance(this);
        if (("yes").equals(appSharedPreference.getIntroFinished())) showHome();
        setTransparentStatusBar();
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        addBottomDots(0);
        setUpViewPager();
        setUpListeners();
    }

    private void setTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void addBottomDots(int currentPage) {
        final TextView[] tvDotsArray = new TextView[layouts.length];

        binding.linLayDots.removeAllViews();
        for (int i = 0; i < tvDotsArray.length; i++) {
            tvDotsArray[i] = new TextView(this);
            tvDotsArray[i].setText(Html.fromHtml("&#8226;"));
            tvDotsArray[i].setTextSize(35);
            tvDotsArray[i].setTextColor(getResources().getColor(android.R.color.holo_purple));
            binding.linLayDots.addView(tvDotsArray[i]);
        }

        if (tvDotsArray.length > 0) {
            tvDotsArray[currentPage].setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void setUpViewPager() {
        binding.viewPager.setAdapter(new ViewPager2Adapter(layouts, this));
        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
//        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
//        binding.viewPager.setCurrentItem(0);
//        binding.viewPager.getAdapter().notifyDataSetChanged();
        binding.viewPager.setPageTransformer(new DepthPageTransformer());
        binding.viewPager.registerOnPageChangeCallback(getOnPageChangeCallback());
    }

    private void setUpListeners() {
        binding.btnSkip.setOnClickListener(v -> showHome());
        binding.btnNext.setOnClickListener(v -> btnNext());
    }

    private void btnNext() {
        final int current = binding.viewPager.getCurrentItem() + 1; // checking for last page if last page home screen will be launched
        if (current < layouts.length) binding.viewPager.setCurrentItem(current); // Show next screen
        else showHome();
    }

    private void showHome() {
        appSharedPreference.setIntroFinished("yes");
        appUtils.setupWindowAnimations(IntroActivity.this);
        startActivity(new Intent(IntroActivity.this, BaseActivity.class));
        finish();
        overridePendingTransition(R.transition.enter, R.transition.exit);
    }

    @NotNull
    private ViewPager2.OnPageChangeCallback getOnPageChangeCallback() {
        return new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                addBottomDots(position);

                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == layouts.length - 1) {
                    // last page. make button text to GOT IT
                    binding.btnNext.setText(getString(R.string.start));
                    binding.btnSkip.setVisibility(View.GONE);
                } else {
                    // still pages are left
                    binding.btnNext.setText(getString(R.string.next));
                    binding.btnSkip.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.left_to_right, R.transition.right_to_left);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}