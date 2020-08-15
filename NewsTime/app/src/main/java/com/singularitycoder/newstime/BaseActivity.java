package com.singularitycoder.newstime;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.singularitycoder.newstime.helper.AppUtils;

public final class BaseActivity extends AppCompatActivity {

    @NonNull
    private final AppUtils helperObject = AppUtils.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helperObject.setStatusBarColor(this, R.color.colorPrimaryDark);
        setContentView(R.layout.activity_base);
        showBaseFragment();
    }

    private void showBaseFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.con_lay_base_activity_root, new BaseFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate(); //Pops one of the added fragments
        }
    }
}
