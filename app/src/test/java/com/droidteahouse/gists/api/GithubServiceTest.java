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
package com.droidteahouse.gists.api;

import static com.droidteahouse.gists.util.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.google.gson.GsonBuilder;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.droidteahouse.gists.vo.Gist;
import com.droidteahouse.gists.vo.GistDeserializer;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(JUnit4.class)
public class GithubServiceTest {
  @Rule
  public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
  //A JUnit Test Rule that swaps the background executor used by the Architecture Components with a
  //* different one which executes each task synchronously.
  private GithubService service;
  private MockWebServer mockWebServer;

  @Before
  public void createService() throws IOException {
    mockWebServer = new MockWebServer();
    service = new Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().registerTypeAdapter(Gist.class, new GistDeserializer()).create()))
        .addCallAdapterFactory(new LiveDataCallAdapterFactory())
        .build()
        .create(GithubService.class);
  }

  @After
  public void stopService() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  public void getGists() throws IOException, InterruptedException {
    enqueueResponse("gists.json");
    List<Gist> gists = getValue(service.getGists()).body;
    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(gists.size(), is(2));
    Gist foo = gists.get(0);
    assertThat(foo.getId(), is("d45b4eee455b76c524314d0f6f57447a"));
    assertThat((foo.getAttachedFiles()).size(), is(1));
    assertThat(foo.getOwner().login, is("jjosiano"));
    assertThat(foo.getAttachedFiles().get(0).filename, is("2station"));
  }

  private void enqueueResponse(String fileName) throws IOException {
    enqueueResponse(fileName, Collections.emptyMap());
  }

  private void enqueueResponse(String fileName, Map<String, String> headers) throws IOException {
    InputStream inputStream = getClass().getClassLoader()
        .getResourceAsStream("api-response/" + fileName);
    BufferedSource source = Okio.buffer(Okio.source(inputStream));
    MockResponse mockResponse = new MockResponse();
    for (Map.Entry<String, String> header : headers.entrySet()) {
      mockResponse.addHeader(header.getKey(), header.getValue());
    }
    mockWebServer.enqueue(mockResponse
        .setBody(source.readString(StandardCharsets.UTF_8)));
  }
}
