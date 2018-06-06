package com.droidteahouse.gists;

import com.droidteahouse.gists.di.DaggerAppComponent;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class GistApplication extends DaggerApplication {
  @Override
  public void onCreate() {
    super.onCreate();
    Picasso.Builder builder = new Picasso.Builder(this);
    builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
    Picasso picasso = builder.build();
    picasso.setLoggingEnabled(false);
    try {
      Picasso.setSingletonInstance(picasso);
    } catch (IllegalStateException e) {
      // for roboelectric tests
    }
    //Stetho.initializeWithDefaults(this);
  }

  @Override
  protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
    return DaggerAppComponent.builder().application(this).build();
  }
}

