package com.singularitycoder.newstime.weather.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.singularitycoder.newstime.helper.retrofit.ApiEndPoints;
import com.singularitycoder.newstime.helper.retrofit.RetrofitService;
import com.singularitycoder.newstime.weather.model.WeatherItem;

import io.reactivex.Single;
import retrofit2.Response;

public final class WeatherRepository {

    @NonNull
    private final String TAG = "WeatherRepository";

    @Nullable
    private static WeatherRepository _instance;

    private WeatherRepository() {
    }

    public static synchronized WeatherRepository getInstance() {
        if (_instance == null) _instance = new WeatherRepository();
        return _instance;
    }

    @Nullable
    public Single<Response<WeatherItem>> getWeatherFromApi(
            @NonNull final String url,
            final long latitude,
            final long longitude,
            @NonNull final String apiKey) {
        final ApiEndPoints apiService = RetrofitService.getInstance().create(ApiEndPoints.class);
        return apiService.getWeather(url, latitude, longitude, apiKey);
    }
}
