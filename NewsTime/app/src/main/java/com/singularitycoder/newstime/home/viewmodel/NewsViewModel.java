package com.singularitycoder.newstime.home.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.singularitycoder.newstime.helper.espresso.ApiIdlingResource;
import com.singularitycoder.newstime.helper.retrofit.StateMediator;
import com.singularitycoder.newstime.helper.retrofit.UiState;
import com.singularitycoder.newstime.home.model.NewsItem;
import com.singularitycoder.newstime.home.repository.NewsRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public final class NewsViewModel extends AndroidViewModel {

    @NonNull
    private final String TAG = "NewsViewModel";

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @NonNull
    private NewsRepository newsRepository = NewsRepository.getInstance();

    @Nullable
    private LiveData<List<NewsItem.NewsArticle>> newsArticleList;

    public NewsViewModel(@NonNull Application application) {
        super(application);
        newsRepository = new NewsRepository(application);
        newsArticleList = newsRepository.getAllFromRoomDb();
    }

    // ROOM START______________________________________________________________

    public final void insertIntoRoomDbThroughRepository(NewsItem.NewsArticle newsArticle) {
        newsRepository.insertIntoRoomDb(newsArticle);
    }

    public final void updateInRoomDbThroughRepository(NewsItem.NewsArticle newsArticle) {
        newsRepository.updateInRoomDb(newsArticle);
    }

    public final void deleteFromRoomDbThroughRepository(NewsItem.NewsArticle newsArticle) {
        newsRepository.deleteFromRoomDb(newsArticle);
    }

    public final void deleteAllFromRoomDbThroughRepository() {
        newsRepository.deleteAllFromRoomDb();
    }

    public final LiveData<List<NewsItem.NewsArticle>> getAllFromRoomDbThroughRepository() {
        return newsArticleList;
    }

    // ROOM END______________________________________________________________

    public final LiveData<StateMediator<Object, UiState, String, String>> getNewsFromRepository(
            @Nullable final String country,
            @NonNull final String category,
            @Nullable final ApiIdlingResource idlingResource) throws IllegalArgumentException {

        if (null != idlingResource) idlingResource.setIdleState(false);

        final StateMediator<Object, UiState, String, String> stateMediator = new StateMediator<>();
        final MutableLiveData<StateMediator<Object, UiState, String, String>> mutableLiveData = new MutableLiveData<>();

        stateMediator.set(null, UiState.LOADING, "Loading...", null);
        mutableLiveData.postValue(stateMediator);

        final DisposableSingleObserver observer =
                newsRepository.getNewsFromApi(country, category)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d(TAG, "onResponse: resp: " + o);
                                if (null != o) {
                                    stateMediator.set(o, UiState.SUCCESS, "Got Data!", "NEWS");
                                    mutableLiveData.postValue(stateMediator);
                                    if (null != idlingResource) idlingResource.setIdleState(true);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                stateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                                mutableLiveData.postValue(stateMediator);
                                if (null != idlingResource) idlingResource.setIdleState(true);
                            }
                        });
        compositeDisposable.add(observer);
        return mutableLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
