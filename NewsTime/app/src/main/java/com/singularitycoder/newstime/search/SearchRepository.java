package com.singularitycoder.newstime.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.singularitycoder.newstime.helper.retrofit.ApiEndPoints;
import com.singularitycoder.newstime.helper.AppConstants;
import com.singularitycoder.newstime.helper.retrofit.RetrofitService;
import com.singularitycoder.newstime.home.model.NewsItem;

import io.reactivex.Single;

public final class SearchRepository {

    @NonNull
    private final String TAG = "SearchRepository";

    @Nullable
    private static SearchRepository _instance;

    public SearchRepository() {
    }

    public static synchronized SearchRepository getInstance() {
        if (_instance == null) {
            _instance = new SearchRepository();
        }
        return _instance;
    }

    @Nullable
    public Single<NewsItem.NewsResponse> getSearchResultsFromApi(@Nullable final String searchQuery) {
        ApiEndPoints apiService = RetrofitService.getInstance().create(ApiEndPoints.class);
        Single<NewsItem.NewsResponse> observer = apiService.getSearchResults("everything", searchQuery, AppConstants.NEWS_API_KEY);
        return observer;
    }
}
