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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.droidteahouse.gists.AppExecutors;
import com.droidteahouse.gists.api.ApiResponse;
import com.droidteahouse.gists.api.GithubService;
import com.droidteahouse.gists.db.GistDao;
import com.droidteahouse.gists.resource.Resource;
import com.droidteahouse.gists.util.ApiUtil;
import com.droidteahouse.gists.util.CountingAppExecutors;
import com.droidteahouse.gists.util.InstantAppExecutors;
import com.droidteahouse.gists.util.TestUtil;
import com.droidteahouse.gists.vo.Gist;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(Parameterized.class)
public class GistFetcherTest {
  private final boolean useRealExecutors;
  @Rule
  public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
  private MutableLiveData<List<Gist>> dbData = new MutableLiveData<>();
  private GistDao dao;
  private GithubService service;
  private GistFetcher dataFetcher;
  private CountingAppExecutors countingAppExecutors;
  private List<Gist> gists;

  public GistFetcherTest(boolean useRealExecutors) {
    this.useRealExecutors = useRealExecutors;
    if (useRealExecutors) {
      countingAppExecutors = new CountingAppExecutors();
    }
  }

  @Parameterized.Parameters  //@todo junit5-android
  public static List<Boolean> param() {
    return Arrays.asList(true, false);
  }

  @Before
  public void init() {
    AppExecutors appExecutors = useRealExecutors
        ? countingAppExecutors.getAppExecutors()
        : new InstantAppExecutors();
    gists = new ArrayList<>();
    gists.add(TestUtil.createGist("1"));
    gists.add(TestUtil.createGist("2"));
    gists.add(TestUtil.createGist("3"));
    dbData.setValue(gists);
    dao = mock(GistDao.class);
    when(dao.loadGists()).thenReturn(dbData);
    service = mock(GithubService.class);
    LiveData<ApiResponse<List<Gist>>> call = ApiUtil.successCall(gists);
    when(service.getGists()).thenReturn(call);
    dataFetcher = new GistFetcher(appExecutors, service, dao);
  }

  @Test
  public void shouldFetchTrue() {
    List list = null;
    assertThat(dataFetcher.shouldFetch(list, false), is(true));
    list = new ArrayList();
    assertThat(dataFetcher.shouldFetch(list, false), is(true));
    list.add(new Gist());
    assertThat(dataFetcher.shouldFetch(list, true), is(true));
  }

  @Test
  public void shouldFetchFalse() {
    List list = null;
    list = new ArrayList();
    list.add(new Gist());
    assertThat(dataFetcher.shouldFetch(list, false), is(false));
  }

  @Test
  public void saveCallResult() {
    dataFetcher.saveCallResult(gists);
    verify(dao, times(1)).clear();
    verify(dao, times(1)).insertGists(gists);
    assertThat(dao.loadGists().getValue(), is(gists));
  }

  @Test
  public void loadFromDb() {
    assertThat(dataFetcher.loadFromDb().getValue(), is(dbData.getValue()));
  }

  @Test
  public void createCall() {
    assertThat(dataFetcher.createCall().getValue().body, is((gists)));
  }

  @Test
  public void fetchOnChangedStaleFromNetwork() {
    Observer<Resource<List<Gist>>> observer = Mockito.mock(Observer.class);
    dataFetcher.asLiveData().observeForever(observer);
    dataFetcher.fetch(true);
    verify(dao, times(2)).loadGists();
    verify(service, times(1)).getGists();
    verify(observer, times(2)).onChanged(isA(Resource.class));
  }

  @Test
  public void fetchOnChangedNotStaleFromDb() {
    Observer<Resource<List<Gist>>> observer = Mockito.mock(Observer.class);
    dataFetcher.asLiveData().observeForever(observer);
    dataFetcher.fetch(false);
    verify(dao, times(1)).loadGists();
    verify(service, never()).getGists();
    verify(observer, times(2)).onChanged(isA(Resource.class));
  }
}

