package com.droidteahouse.gists.di;

import com.droidteahouse.gists.ui.GistActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Binds all activity sub-components within the app.
 */
@Module
abstract class AndroidBindingModule {
  //@todo custom activity scope
  @ContributesAndroidInjector
  abstract GistActivity contributesGistActivity();
}