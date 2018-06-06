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

import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.droidteahouse.gists.api.ApiResponse;
import com.droidteahouse.gists.resource.Resource;
import com.droidteahouse.gists.util.TestUtil;
import com.droidteahouse.gists.vo.Gist;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GistRepositoryTest {
  @Rule
  public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
  MutableLiveData<Resource<List<Gist>>> dbData;
  List<Gist> gistsList;
  LiveData<ApiResponse<List<Gist>>> call;
  private GistRepository repository;
  private GistFetcher dataFetcher;

  @Before
  public void init() {
    //@todo mock or spy and then return mocked behavior
    dataFetcher = mock(GistFetcher.class);
    gistsList = new ArrayList<>();
    gistsList.add(TestUtil.createGist("1"));
    gistsList.add(TestUtil.createGist("2"));
    gistsList.add(TestUtil.createGist("3"));
    dbData = new MutableLiveData<>();
    //when(dataFetcher.loadFromDb()).thenReturn(dbData);
    doAnswer((Answer) invocation -> {
      // Object arg0 = invocation.getArgument(0);
      //assertEquals(dataFetcher.result.);
      dbData.setValue(Resource.success(gistsList));
      return null;
    }).when(dataFetcher).fetch(anyBoolean());
    when(dataFetcher.asLiveData()).thenReturn(dbData);
    repository = new GistRepository(dataFetcher);
  }

  //stale + null data needs network call, (init)
  @Test
  public void loadGistsWhenStaleReturnsList() throws IOException {
    assertTrue(repository.loadGists(true).getValue().data.size() == (3));
  }

  @Test
  public void loadGistsReturnList() throws IOException {
    assertTrue(repository.loadGists(true).getValue().data instanceof List);
  }
  // not stale but no longer cached == db (reclaim,)
}