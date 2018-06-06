package com.droidteahouse.gists.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.droidteahouse.gists.repository.GistRepository;
import com.droidteahouse.gists.resource.Resource;
import com.droidteahouse.gists.vo.Gist;

import java.util.List;

import javax.inject.Inject;

public class GistViewModel extends ViewModel {
  @Inject
  GistRepository repository;
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  LiveData<Resource<List<Gist>>> gists;

  @Inject
  public GistViewModel(GistRepository gistRepository) {
    this.repository = gistRepository;
  }

  public LiveData<Resource<List<Gist>>> getGists(boolean isStale) {
    if (gists == null || gists.getValue().data == null || gists.getValue().data.size() == 0 || isStale) {
      gists = loadGists(isStale);  //go and get from db / network
    } else {
      Log.d("VM", "cached livedata gists being returned!:" + gists);
    }
    return gists;  //they should exist as cached if not re-fetched
  }

  LiveData<Resource<List<Gist>>> loadGists(boolean isStale) {
    return repository.loadGists(isStale);
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    //do we need to clear out repo here?
  }
}
