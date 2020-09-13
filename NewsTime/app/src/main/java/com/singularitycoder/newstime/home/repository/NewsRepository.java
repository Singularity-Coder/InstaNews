package com.singularitycoder.newstime.home.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.singularitycoder.newstime.home.model.NewsItem;
import com.singularitycoder.newstime.home.roomdao.NewsDaoRoom;
import com.singularitycoder.newstime.helper.ApiEndPoints;
import com.singularitycoder.newstime.helper.NewsTimeRoomDatabase;
import com.singularitycoder.newstime.helper.RetrofitService;

import java.util.List;

import io.reactivex.Single;

public final class NewsRepository {

    @NonNull
    private final String TAG = "NewsRepository";

    @NonNull
    private final String NEWS_API_KEY = "c39be54286e4427c9f8cd00f97a96398";
//    private final String API_KEY = "YOUR_NEWSAPI.ORG_API_KEY";

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

    public static synchronized NewsRepository getInstance() {
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
        Single<NewsItem.NewsResponse> observer = apiService.getNewsList(country, category, NEWS_API_KEY);
        return observer;
    }

    @Nullable
    public Single<NewsItem.NewsResponse> getSearchResultsFromApi(@Nullable final String searchQuery) {
        ApiEndPoints apiService = RetrofitService.getRetrofitInstance().create(ApiEndPoints.class);
        Single<NewsItem.NewsResponse> observer = apiService.getSearchResults("everything", searchQuery, NEWS_API_KEY);
        return observer;
    }

    @Nullable
    public Single<NewsItem.NewsResponse> getSourcesFromApi() {
        ApiEndPoints apiService = RetrofitService.getRetrofitInstance().create(ApiEndPoints.class);
        Single<NewsItem.NewsResponse> observer = apiService.getSources("sources", NEWS_API_KEY);
        return observer;
    }
}
