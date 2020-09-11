package com.singularitycoder.newstime;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;

public final class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        if (null == instance) instance = this;

        // Initializing Fresco
        Fresco.initialize(this);

        // Initializing Leaky Canary
        if (LeakCanary.isInAnalyzerProcess(this)) return;
        LeakCanary.install(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static synchronized BaseApplication getInstance() {
        return instance;
    }
}
