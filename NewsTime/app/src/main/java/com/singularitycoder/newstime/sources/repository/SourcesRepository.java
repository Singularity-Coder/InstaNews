package com.singularitycoder.newstime.sources.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.singularitycoder.newstime.helper.retrofit.ApiEndPoints;
import com.singularitycoder.newstime.helper.retrofit.RetrofitService;
import com.singularitycoder.newstime.home.model.NewsItem;
import com.singularitycoder.newstime.sources.model.SourceItem;

import io.reactivex.Single;
import retrofit2.Response;

public final class SourcesRepository {

    @NonNull
    private final String TAG = "SourcesRepository";

    @NonNull
    private final ApiEndPoints apiService = RetrofitService.getInstance().create(ApiEndPoints.class);

    @Nullable
    private static SourcesRepository _instance;

    private SourcesRepository() {
    }

    public static synchronized SourcesRepository getInstance() {
        if (_instance == null) _instance = new SourcesRepository();
        return _instance;
    }

    @Nullable
    public Single<Response<SourceItem.SourceResponse>> getSourceListWithRetrofit(
            @NonNull final String newsType,
            @NonNull final String apiKey) {
        return apiService.getSourceList(newsType, apiKey);
    }

    @Nullable
    public final Single<Response<NewsItem.NewsResponse>> getNewsListFromNewsSourcesWithRetrofit(
            @NonNull final String sourceId,
            @NonNull final String apiKey) {
        return apiService.getNewsListFromSources(sourceId, apiKey);
    }
}
