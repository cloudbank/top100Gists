package com.droidteahouse.gists.di;

import android.app.Application;

import com.droidteahouse.gists.GistApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton  //@todo research
@Component(modules = {
    AndroidSupportInjectionModule.class,
    AppModule.class,
    AndroidBindingModule.class})
public interface AppComponent extends AndroidInjector<GistApplication> {
  @Override
  void inject(GistApplication githubApp);

  @Component.Builder
  interface Builder {
    @BindsInstance
    AppComponent.Builder application(Application application);

    AppComponent build();
  }
}



