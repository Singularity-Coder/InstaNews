package com.singularitycoder.newstime.helper.retrofit;

import com.singularitycoder.newstime.home.model.NewsItem;
import com.singularitycoder.newstime.sources.model.SourceItem;
import com.singularitycoder.newstime.weather.model.WeatherItem;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

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
    Single<Response<SourceItem.SourceResponse>> getSourceList(
            @Path("type") String newsType,
            @Query("apiKey") String apiKey
    );

    @GET("top-headlines")
    Single<Response<NewsItem.NewsResponse>> getNewsListFromSources(
            @Query("sources") String sourceId,
            @Query("apiKey") String apiKey
    );

    @GET
    Single<WeatherItem> getWeather(
            @Url String url,
            @Query("lat") long latitude,
            @Query("lon") long longitude,
            @Query("appid") String apiKey
    );

    default void printStuff() {
        System.out.print("Hello");
    }
}