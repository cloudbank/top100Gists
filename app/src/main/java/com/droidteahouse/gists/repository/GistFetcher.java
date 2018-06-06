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

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.droidteahouse.gists.AppExecutors;
import com.droidteahouse.gists.api.ApiResponse;
import com.droidteahouse.gists.api.GithubService;
import com.droidteahouse.gists.db.GistDao;
import com.droidteahouse.gists.vo.Gist;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 */
@Singleton
public class GistFetcher extends SmartDataFetcher<List<Gist>, List<Gist>> {
  @Inject  //@todo remove-
  public Application application;
  private GistDao gistDao;

  @Inject
  public GistFetcher(AppExecutors appExecutors, GithubService service, GistDao gistDao) {
    this.appExecutors = appExecutors;
    this.githubService = service;
    this.gistDao = gistDao;
  }

  @Override
  protected void saveCallResult(@NonNull List<Gist> item) {
    gistDao.clear();
    Log.d("Repo", "cleared db:");
    gistDao.insertGists(item);
    Log.d("Repo", "saved to db:");
  }

  @Override
  protected boolean shouldFetch(@Nullable List<Gist> data, boolean isStale) {
    return data == null || data.isEmpty() || isStale;
  }

  @NonNull
  @Override
  protected LiveData<List<Gist>> loadFromDb() {
    //set the owner as top 100 and maybe do search for separate fn-ality
    Log.d("DB", "loading from db: ");
    return gistDao.loadGists();
  }

  @NonNull
  @Override
  protected LiveData<ApiResponse<List<Gist>>> createCall() {
    return githubService.getGists();
  }

  //@todo make a snackbar
  @Override
  protected void onFetchFailed(String message) {
    Toast.makeText(application, message, Toast.LENGTH_LONG).show();
  }
}
