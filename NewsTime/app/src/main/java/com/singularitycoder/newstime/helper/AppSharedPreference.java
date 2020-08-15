package com.singularitycoder.newstime.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class AppSharedPreference {

    @NonNull
    private static final String KEY_MEMBER_TYPE = "memberType";

    @NonNull
    private static final String KEY_FCM_TOKEN = "fcmToken";

    @Nullable
    private static AppSharedPreference _instance;

    @Nullable
    private SharedPreferences sharedPref;

    @Nullable
    private SharedPreferences.Editor sharedPrefEditor;

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

    public final void setMemberType(String memberType) {
        sharedPrefEditor.putString(KEY_MEMBER_TYPE, memberType);
        sharedPrefEditor.commit();
    }

    public final String getMemberType() {
        return sharedPref.getString(KEY_MEMBER_TYPE, "");
    }
}
