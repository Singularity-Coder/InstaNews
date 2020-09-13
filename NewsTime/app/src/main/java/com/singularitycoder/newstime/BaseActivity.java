package com.singularitycoder.newstime;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.singularitycoder.newstime.helper.AppUtils;

public final class BaseActivity extends AppCompatActivity {

    private boolean backPress = false;

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @Nullable
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appUtils.setStatusBarColor(this, R.color.colorPrimaryDark);
        showBaseFragment();
        setContentView(R.layout.activity_base);
        initialise();
    }

    private void showBaseFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.con_lay_base_activity_root, new BaseFragment())
                .commit();
    }

    private void initialise() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Fresco.initialize(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onBackPressed() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate(); // Pops one of the added fragments
        } else {
            if (backPress) super.onBackPressed();
            backPress = true;
            Toast.makeText(this, "Press back again to exit!", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> backPress = false, 2000);
        }
    }
}
