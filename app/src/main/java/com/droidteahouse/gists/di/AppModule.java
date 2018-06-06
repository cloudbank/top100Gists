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
package com.droidteahouse.gists.di;

import com.google.gson.GsonBuilder;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;

import com.droidteahouse.gists.api.GithubService;
import com.droidteahouse.gists.api.LiveDataCallAdapterFactory;
import com.droidteahouse.gists.db.GistDao;
import com.droidteahouse.gists.db.GithubDatabase;
import com.droidteahouse.gists.vo.Gist;
import com.droidteahouse.gists.vo.GistDeserializer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public class AppModule {
  @Provides
  @Singleton
  Context provideContext(Application application) {
    return application;
  }

  @Provides
  @Singleton
    //mutable
  SharedPreferences provideSharedPrefrences(Context context) {
    return context.getSharedPreferences("GistPrefs", Context.MODE_PRIVATE);
  }

  @Reusable
  @Provides
    //immutable
  GithubService provideGithubService() {
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
        //.//addNetworkInterceptor(new StethoInterceptor())
        .build();
    return new Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().registerTypeAdapter(Gist.class, new GistDeserializer()).create()))
        .addCallAdapterFactory(new LiveDataCallAdapterFactory())
        .client(okHttpClient)
        .build()
        .create(GithubService.class);
  }

  //this is an expensive operation so, we would want a singleton object.
  @Singleton
  @Provides
  GithubDatabase provideDb(Application app) {
    return Room.databaseBuilder(app, GithubDatabase.class, "github.db").build();
  }

  @Singleton
  @Provides
  GistDao provideGistDao(GithubDatabase db) {
    return db.gistDao();
  }
}
