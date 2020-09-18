package com.singularitycoder.newstime.sources.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.singularitycoder.newstime.helper.ApiIdlingResource;
import com.singularitycoder.newstime.helper.StateMediator;
import com.singularitycoder.newstime.helper.UiState;
import com.singularitycoder.newstime.sources.repository.SourcesRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public final class SourcesViewModel extends ViewModel {

    @NonNull
    private final String TAG = "SourcesViewModel";

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @NonNull
    private SourcesRepository sourcesRepository = SourcesRepository.getInstance();

    public final LiveData<StateMediator<Object, UiState, String, String>> getSourcesFromRepository(
            @Nullable final ApiIdlingResource idlingResource) throws IllegalArgumentException {

        if (null != idlingResource) idlingResource.setIdleState(false);

        final StateMediator<Object, UiState, String, String> stateMediator = new StateMediator<>();
        final MutableLiveData<StateMediator<Object, UiState, String, String>> mutableLiveData = new MutableLiveData<>();

        stateMediator.set(null, UiState.LOADING, "Loading...", null);
        mutableLiveData.postValue(stateMediator);

        final DisposableSingleObserver observer =
                sourcesRepository.getSourcesFromApi()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d(TAG, "onResponse: resp: " + o);
                                if (null != o) {
                                    stateMediator.set(o, UiState.SUCCESS, "Got Data!", "SOURCES");
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
