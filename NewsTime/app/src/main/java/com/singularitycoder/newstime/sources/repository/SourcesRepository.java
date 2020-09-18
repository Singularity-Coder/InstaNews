package com.singularitycoder.newstime.sources.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.singularitycoder.newstime.helper.ApiEndPoints;
import com.singularitycoder.newstime.helper.AppConstants;
import com.singularitycoder.newstime.helper.RetrofitService;
import com.singularitycoder.newstime.sources.model.SourceItem;

import io.reactivex.Single;

public final class SourcesRepository {

    @NonNull
    private final String TAG = "SourcesRepository";

    @Nullable
    private static SourcesRepository _instance;

    public SourcesRepository() {
    }

    public static synchronized SourcesRepository getInstance() {
        if (_instance == null) {
            _instance = new SourcesRepository();
        }
        return _instance;
    }

    @Nullable
    public Single<SourceItem> getSourcesFromApi() {
        ApiEndPoints apiService = RetrofitService.getRetrofitInstance().create(ApiEndPoints.class);
        Single<SourceItem> observer = apiService.getSources("sources", AppConstants.NEWS_API_KEY);
        return observer;
    }
}
