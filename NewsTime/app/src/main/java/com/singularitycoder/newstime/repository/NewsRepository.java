package com.singularitycoder.newstime.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.singularitycoder.newstime.model.NewsItem;
import com.singularitycoder.newstime.roomdao.NewsDaoRoom;
import com.singularitycoder.newstime.helpers.ApiEndPoints;
import com.singularitycoder.newstime.helpers.NewsTimeRoomDatabase;
import com.singularitycoder.newstime.helpers.RetrofitService;

import java.util.List;

import io.reactivex.Single;

public final class NewsRepository {

    @NonNull
    private static final String TAG = "NewsRepository";

    @Nullable
    private static NewsRepository _instance;

    @Nullable
    private NewsDaoRoom newsDaoRoom;

    @Nullable
    private LiveData<List<NewsItem.NewsArticle>> newsArticleList;

    public NewsRepository() {
    }

    public NewsRepository(Application application) {
        NewsTimeRoomDatabase database = NewsTimeRoomDatabase.getInstance(application);
        newsDaoRoom = database.newsDao();
        newsArticleList = newsDaoRoom.getAllNews();
    }

    public static NewsRepository getInstance() {
        if (_instance == null) {
            _instance = new NewsRepository();
        }
        return _instance;
    }

    // ROOM START______________________________________________________________

    public final void insertIntoRoomDb(NewsItem.NewsArticle newsArticle) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> newsDaoRoom.insertNews(newsArticle));
    }

    public final void updateInRoomDb(NewsItem.NewsArticle newsArticle) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> newsDaoRoom.updateNews(newsArticle));
    }

    public final void deleteFromRoomDb(NewsItem.NewsArticle newsArticle) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> newsDaoRoom.deleteNews(newsArticle));
    }

    public final void deleteAllFromRoomDb() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> newsDaoRoom.deleteAllNews());
    }

    public final LiveData<List<NewsItem.NewsArticle>> getAllFromRoomDb() {
        return newsArticleList;
    }

    // ROOM END______________________________________________________________

    @Nullable
    public Single<NewsItem.NewsResponse> getNewsFromApi(
            @Nullable final String country,
            @NonNull final String category) {
        ApiEndPoints apiService = RetrofitService.getRetrofitInstance().create(ApiEndPoints.class);
//        Single<NewsResponse> observer = apiService.getNewsList(country, category, "YOUR_NEWSAPI.ORG_API_KEY");
        Single<NewsItem.NewsResponse> observer = apiService.getNewsList(country, category, "c39be54286e4427c9f8cd00f97a96398");
        return observer;
    }
}
