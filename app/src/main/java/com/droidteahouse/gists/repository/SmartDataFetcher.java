/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.droidteahouse.gists.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.droidteahouse.gists.AppExecutors;
import com.droidteahouse.gists.api.ApiResponse;
import com.droidteahouse.gists.api.GithubService;
import com.droidteahouse.gists.resource.Resource;

import java.util.Objects;

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 * Template method; could be extended to a factory
 */
public abstract class SmartDataFetcher<ResultType, RequestType> {
  protected final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
  protected AppExecutors appExecutors;
  protected GithubService githubService;
  Observer successObserver = new Observer<ResultType>() {
    @Override
    public void onChanged(@Nullable ResultType newData) {
      Log.d("SmartDataFetcher", "onChanged for addSource" + newData);
      setValue(Resource.success(newData));
    }
  };

  @MainThread
  public void fetch(boolean isStale) {
    result.setValue(Resource.loading(null));
    LiveData<ResultType> dbSource = loadFromDb();
    result.addSource(dbSource, data -> {
      result.removeSource(dbSource);
      Log.d("SmartDataFetcher", "shouldfetch" + shouldFetch(data, isStale));
      if (shouldFetch(data, isStale)) {
        fetchFromNetwork(dbSource);
      } else {
        result.addSource(dbSource, newData -> setValue(Resource.success(newData)));
        //result.removeSource(dbSource);
      }
    });
  }

  @MainThread
  private void setValue(Resource<ResultType> newValue) {
    if (!Objects.equals(result.getValue(), newValue)) {
      result.setValue(newValue);
    }
  }

  @VisibleForTesting
  protected void fetchFromNetwork(final LiveData<ResultType> dbSource) {
    Log.d("SmartDataFetcher", "fetching from network");
    LiveData<ApiResponse<RequestType>> apiResponse = createCall();
    Log.d("SmartDataFetcher", "the result" + apiResponse.getValue());
    // we re-attach dbSource as a new source, it will dispatch its latest value quickly
    //changing the source calls onchange
    result.addSource(dbSource, newData -> setValue(Resource.loading(newData)));
    result.addSource(apiResponse, (ApiResponse<RequestType> response) -> {
      result.removeSource(apiResponse);
      result.removeSource(dbSource);
      //noinspection ConstantConditions
      Log.d("SmartDataFetcher", "the response.isSuccessful()" + response.isSuccessful() + "" + response);
      if (response.isSuccessful()) {
        appExecutors.diskIO().execute(() -> {
          saveCallResult(processResponse(response));
          appExecutors.mainThread().execute(() -> {
            // we specially request a new live data,
            // otherwise we will get immediately last cached value,
            // which may not be updated with latest results received from network.
            result.addSource(loadFromDb(), successObserver);
            //@todo does this call the method
            //result.removeSource(loadFromDb());
          });
        });
      } else {
        onFetchFailed(response.errorMessage);
        result.addSource(dbSource,
            newData -> setValue(Resource.error(response.errorMessage, newData)));
        // result.removeSource(dbSource);
      }
    });
  }

  protected void onFetchFailed(String message) {
  }

  public LiveData<Resource<ResultType>> asLiveData() {
    return result;
  }

  @WorkerThread
  protected RequestType processResponse(ApiResponse<RequestType> response) {
    return response.body;
  }

  @MainThread
  protected abstract boolean shouldFetch(@Nullable ResultType data, boolean isStale);

  @WorkerThread
  protected abstract void saveCallResult(@NonNull RequestType item);

  @NonNull
  @MainThread
  protected abstract LiveData<ResultType> loadFromDb();

  @NonNull
  @MainThread
  protected abstract LiveData<ApiResponse<RequestType>> createCall();
}
