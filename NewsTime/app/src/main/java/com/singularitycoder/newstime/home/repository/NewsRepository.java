package com.singularitycoder.newstime.home.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.singularitycoder.newstime.helper.AppConstants;
import com.singularitycoder.newstime.helper.retrofit.ApiEndPoints;
import com.singularitycoder.newstime.helper.retrofit.RetrofitService;
import com.singularitycoder.newstime.helper.room.NewsRoomDatabase;
import com.singularitycoder.newstime.home.dao.NewsDao;
import com.singularitycoder.newstime.home.model.NewsItem;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Response;

public final class NewsRepository {

    @NonNull
    private final String TAG = "NewsRepository";

    @Nullable
    private static NewsRepository _instance;

    @Nullable
    private NewsDao newsDao;

    @Nullable
    private LiveData<List<NewsItem.NewsArticle>> newsArticleList;

    public NewsRepository() {
    }

    public NewsRepository(Application application) {
        NewsRoomDatabase database = NewsRoomDatabase.getInstance(application);
        newsDao = database.newsDao();
        newsArticleList = newsDao.getAllNewsArticles();
    }

    public static synchronized NewsRepository getInstance() {
        if (_instance == null) {
            _instance = new NewsRepository();
        }
        return _instance;
    }

    // ROOM START______________________________________________________________

    public final void insertIntoRoomDb(NewsItem.NewsArticle newsArticle) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> newsDao.insertNewsArticle(newsArticle));
    }

    public final void updateInRoomDb(NewsItem.NewsArticle newsArticle) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> newsDao.updateNewsArticle(newsArticle));
    }

    public final void deleteFromRoomDb(NewsItem.NewsArticle newsArticle) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> newsDao.deleteNewsArticle(newsArticle));
    }

    public final void deleteAllFromRoomDb() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> newsDao.deleteAllNewsArticles());
    }

    public final LiveData<List<NewsItem.NewsArticle>> getAllFromRoomDb() {
        return newsArticleList;
    }

    // ROOM END______________________________________________________________

    @Nullable
    public Single<Response<NewsItem.NewsResponse>> getNewsFromApi(
            @Nullable final String country,
            @NonNull final String category) {
        final ApiEndPoints apiService = RetrofitService.getInstance().create(ApiEndPoints.class);
        return apiService.getNewsList(country, category, AppConstants.NEWS_API_KEY);
    }
}
