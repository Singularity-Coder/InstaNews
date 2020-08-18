package com.singularitycoder.newstime.splash;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.singularitycoder.newstime.BaseActivity;
import com.singularitycoder.newstime.R;
import com.singularitycoder.newstime.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    @Nullable
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        animateAppIcon();
        delaySplashFor2Sec();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void animateAppIcon() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(binding.ivLogo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setStartDelay(600);  // milli seconds
        alphaAnimation.setDuration(1000);   // milli seconds
        alphaAnimation.start();
        binding.ivLogo.setVisibility(View.VISIBLE);
    }

    private void delaySplashFor2Sec() {
        int SHOW_SPLASH_FOR = 2000; // milli seconds
        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashActivity.this, BaseActivity.class);
            SplashActivity.this.startActivity(mainIntent);
            SplashActivity.this.finish();
        }, SHOW_SPLASH_FOR);
    }

    private void startZoomInAnimation() {
        Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_animation);
        binding.ivLogo.startAnimation(animZoomIn);
    }

    private void startZoomOutAnimation() {
        Animation animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out_animation);
        binding.ivLogo.startAnimation(animZoomOut);
    }
}