package com.droidteahouse.gists.repository;

import android.arch.lifecycle.LiveData;

import com.droidteahouse.gists.resource.Resource;
import com.droidteahouse.gists.vo.Gist;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GistRepository {
  private GistFetcher gistFetcher;

  @Inject
  GistRepository(GistFetcher dataFetcher) {
    this.gistFetcher = dataFetcher;
  }

  public LiveData<Resource<List<Gist>>> loadGists(boolean isStale) {
    //MLD adds source, sets a value observes the LD gists from db/network
    //this is ~ equiv to transformations.switchmap
    gistFetcher.fetch(isStale);  //get LD from db  (and from network if necessary)
    return gistFetcher.asLiveData();   //return the MLD
  }
}

