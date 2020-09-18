package com.singularitycoder.newstime.weather.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.singularitycoder.newstime.helper.ApiEndPoints;
import com.singularitycoder.newstime.helper.AppConstants;
import com.singularitycoder.newstime.helper.RetrofitService;
import com.singularitycoder.newstime.home.model.NewsItem;
import com.singularitycoder.newstime.weather.model.WeatherItem;

import io.reactivex.Single;

public final class WeatherRepository {

    @NonNull
    private final String TAG = "WeatherRepository";

    @Nullable
    private static WeatherRepository _instance;

    public WeatherRepository() {
    }

    public static synchronized WeatherRepository getInstance() {
        if (_instance == null) {
            _instance = new WeatherRepository();
        }
        return _instance;
    }

    @Nullable
    public Single<WeatherItem> getWeatherFromApi(
            @NonNull final String url,
            final long latitude,
            final long longitude,
            @NonNull final String apiKey) {
        final ApiEndPoints apiService = RetrofitService.getRetrofitInstance().create(ApiEndPoints.class);
        final Single<WeatherItem> observer = apiService.getWeather(url, latitude, longitude, apiKey);
        return observer;
    }
}
