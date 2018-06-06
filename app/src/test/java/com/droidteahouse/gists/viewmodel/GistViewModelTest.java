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
package com.droidteahouse.gists.viewmodel;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;

import com.droidteahouse.gists.repository.GistRepository;
import com.droidteahouse.gists.resource.Resource;
import com.droidteahouse.gists.util.TestUtil;
import com.droidteahouse.gists.vo.Gist;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

//using mocking instead of roboelectric
@RunWith(JUnit4.class)
public class GistViewModelTest {
  @Rule
  public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
  private GistRepository repository;
  private GistViewModel gistViewModel;
  private MutableLiveData<Resource<List<Gist>>> networkGists = new MutableLiveData<>();
  private MutableLiveData<Resource<List<Gist>>> dbGists = new MutableLiveData<>();

  @Before
  public void setup() {
    repository = mock(GistRepository.class);
    gistViewModel = new GistViewModel(repository);
    //at the repo level, db/network
    when(repository.loadGists(true)).thenReturn(networkGists);
    when(repository.loadGists(false)).thenReturn(dbGists);
    Gist gist = TestUtil.createGist("1");
    Gist gist2 = TestUtil.createGist("2");
    Gist gist3 = TestUtil.createGist("3");
    List<Gist> gists = new ArrayList();
    gists.add(gist);
    gists.add(gist2);
    networkGists.setValue(Resource.success(gists));
    gists.add(gist3);
    dbGists.setValue(Resource.success(gists));
  }

  @Test  //2 factors art VM level: staleness and gists null, empty
  //3 ways to get data:  cached, network, or database
  public void getGistsNull() {
    gistViewModel.gists = null;
    assertThat(gistViewModel.getGists(true), is(equalTo(networkGists)));
    assertThat(gistViewModel.getGists(false), is(equalTo(networkGists)));
    verify(repository, atLeastOnce()).loadGists(true);
  }

  @Test  //cached not null/empty stale
  public void getGistsCached() {
    gistViewModel.gists = dbGists;
    assertThat(gistViewModel.getGists(false), is(equalTo(dbGists)));
    verify(repository, never()).loadGists(false);
  }

  @Test  //dbNetworkGists  stale
  public void getGistsStaleNonNullEmpty() {
    gistViewModel.gists = dbGists;
    assertThat(gistViewModel.getGists(true), is(equalTo(networkGists)));
    verify(repository, atLeastOnce()).loadGists(true);
  }
}