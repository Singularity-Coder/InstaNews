package com.singularitycoder.newstime.search;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.singularitycoder.newstime.helper.espresso.ApiIdlingResource;
import com.singularitycoder.newstime.helper.retrofit.StateMediator;
import com.singularitycoder.newstime.helper.retrofit.UiState;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public final class SearchViewModel extends ViewModel {

    @NonNull
    private final String TAG = "SearchViewModel";

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @NonNull
    private SearchRepository searchRepository = SearchRepository.getInstance();

    public final LiveData<StateMediator<Object, UiState, String, String>> getSearchResultsFromRepository(
            @Nullable final String searchQuery,
            @Nullable final ApiIdlingResource idlingResource) throws IllegalArgumentException {

        if (null != idlingResource) idlingResource.setIdleState(false);

        final StateMediator<Object, UiState, String, String> stateMediator = new StateMediator<>();
        final MutableLiveData<StateMediator<Object, UiState, String, String>> mutableLiveData = new MutableLiveData<>();

        stateMediator.set(null, UiState.LOADING, "Loading...", null);
        mutableLiveData.postValue(stateMediator);

        compositeDisposable.add(
                searchRepository.getSearchResultsFromApi(searchQuery)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d(TAG, "onResponse: resp: " + o);
                                if (null != o) {
                                    stateMediator.set(o, UiState.SUCCESS, "Got Data!", "SEARCH");
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
                        })
        );
        return mutableLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
