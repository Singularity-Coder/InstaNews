package com.singularitycoder.newstime.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.singularitycoder.newstime.helpers.ApiEndPoints;
import com.singularitycoder.newstime.helpers.RetrofitService;
import com.singularitycoder.newstime.model.NewsResponse;

import io.reactivex.Single;

public final class NewsRepository {

    @NonNull
    private static final String TAG = "NewsRepository";

    @Nullable
    private static NewsRepository _instance;

    public NewsRepository() {
    }

    public static NewsRepository getInstance() {
        if (_instance == null) {
            _instance = new NewsRepository();
        }
        return _instance;
    }

    @Nullable
    public Single<NewsResponse> getNewsFromApi(
            @Nullable final String country,
            @NonNull final String category) {
        ApiEndPoints apiService = RetrofitService.getRetrofitInstance().create(ApiEndPoints.class);
        Single<NewsResponse> observer = apiService.getNewsList(country, category, "YOUR_NEWSAPI.ORG_API_KEY");
        return observer;
    }
}
