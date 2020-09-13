package com.singularitycoder.newstime.helper;

import com.singularitycoder.newstime.home.model.NewsItem;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiEndPoints {

    @GET("top-headlines")
    Single<NewsItem.NewsResponse> getNewsList(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

    @GET("{type}")
    Single<NewsItem.NewsResponse> getSearchResults(
            @Path("type") String newsType,
            @Query("q") String searchQuery,
            @Query("apiKey") String apiKey
    );

    @GET("{type}")
    Single<NewsItem.NewsResponse> getSources(
            @Path("type") String newsType,
            @Query("apiKey") String apiKey
    );

    default void printStuff() {
        System.out.print("Hello");
    }
}