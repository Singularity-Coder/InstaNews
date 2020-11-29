package com.singularitycoder.newstime;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

public final class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    private static BaseApplication _instance;

    @Override
    public void onCreate() {
        super.onCreate();

        if (null == _instance) _instance = this;

        // Initializing Fresco
        Fresco.initialize(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static synchronized BaseApplication get_instance() {
        return _instance;
    }
}
