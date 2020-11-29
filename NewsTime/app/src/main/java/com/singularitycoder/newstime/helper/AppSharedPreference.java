package com.singularitycoder.newstime.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class AppSharedPreference {

    @NonNull
    private static final String KEY_COUNTRY = "KEY_COUNTRY";

    @NonNull
    private static final String KEY_NEWS_LAYOUT = "KEY_NEWS_LAYOUT";

    @NonNull
    private static final String KEY_APP_LANGUAGE = "KEY_APP_LANGUAGE";

    @NonNull
    private static final String KEY_APP_THEME = "KEY_APP_THEME";

    @NonNull
    private static final String KEY_INTRO_FINISHED = "KEY_INTRO_FINISHED";

    @NonNull
    private static final String KEY_FCM_TOKEN = "KEY_FCM_TOKEN";

    @Nullable
    private static AppSharedPreference _instance;

    @Nullable
    private SharedPreferences sharedPref;

    @Nullable
    private SharedPreferences.Editor sharedPrefEditor;

    private AppSharedPreference() {
    }

    @NonNull
    public static synchronized AppSharedPreference getInstance(Context context) {
        if (null == _instance) {
            _instance = new AppSharedPreference();
            _instance.configSessionUtils(context);
        }
        return _instance;
    }

    private void configSessionUtils(Context context) {
        sharedPref = context.getSharedPreferences("AppPreferences", Activity.MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.apply();
    }

    public final void setCountry(String country) {
        sharedPrefEditor.putString(KEY_COUNTRY, country);
        sharedPrefEditor.commit();
    }

    public final String getCountry() {
        return sharedPref.getString(KEY_COUNTRY, "");
    }

    public final void setNewsLayout(String newsLayout) {
        sharedPrefEditor.putString(KEY_NEWS_LAYOUT, newsLayout);
        sharedPrefEditor.commit();
    }

    public final String getNewsLayout() {
        return sharedPref.getString(KEY_NEWS_LAYOUT, "");
    }

    public final void setAppLanguage(String appLanguage) {
        sharedPrefEditor.putString(KEY_APP_LANGUAGE, appLanguage);
        sharedPrefEditor.commit();
    }

    public final String getAppLanguage() {
        return sharedPref.getString(KEY_APP_LANGUAGE, "");
    }

    public final void setAppTheme(String appTheme) {
        sharedPrefEditor.putString(KEY_APP_THEME, appTheme);
        sharedPrefEditor.commit();
    }

    public final String getAppTheme() {
        return sharedPref.getString(KEY_APP_THEME, "");
    }

    public final void setIntroFinished(String introFinished) {
        sharedPrefEditor.putString(KEY_INTRO_FINISHED, introFinished);
        sharedPrefEditor.commit();
    }

    public final String getIntroFinished() {
        return sharedPref.getString(KEY_INTRO_FINISHED, "no");
    }

    public final void setFcmToken(String fcmToken) {
        sharedPrefEditor.putString(KEY_FCM_TOKEN, fcmToken);
        sharedPrefEditor.commit();
    }

    public final String getFcmToken() {
        return sharedPref.getString(KEY_FCM_TOKEN, "no");
    }
}
