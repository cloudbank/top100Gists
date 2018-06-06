package com.droidteahouse.gists.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.droidteahouse.gists.resource.Resource;
import com.droidteahouse.gists.viewmodel.GistViewModel;
import com.droidteahouse.gists.vo.Gist;
import com.teahouse.gists.R;
import com.teahouse.gists.databinding.ActivityGistBinding;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class GistActivity extends DaggerAppCompatActivity {
  public static final String LAST_FETCHED = "lastFetched";
  private final static String TAG = "MainActivity";
  /*@VisibleForTesting
  public LiveData<Resource<List<Gist>>> getGists() {
    return gists;
  }*/
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  LiveData<Resource<List<Gist>>> gists;
  GistViewModel gistViewModel;
  RecyclerView recyclerView;
  SwipeRefreshLayout swipeRefreshLayout;
  LinearLayoutManager mLayoutManager;
  ListViewAdapter rvAdapter;
  @Inject
  ViewModelProvider.Factory viewModelFactory;
  @Inject
  SharedPreferences prefs;
  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  long lastFetched;
  private ActivityGistBinding binding;
  private Observer listObserver = new Observer<Resource<List<Gist>>>() {
    @Override
    public void onChanged(@Nullable Resource<List<Gist>> listResource) {
      setAdapter(listResource.data);
      Log.d("OnChanged", "---->onChanged called for list");
      if (swipeRefreshLayout.isRefreshing()) {
        swipeRefreshLayout.setRefreshing(false);
      }
      lastFetched = System.currentTimeMillis();
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);//bundle for restart items
    Log.d("LC", "oncreate:");
    //@todo \check the internet conn
    if (savedInstanceState != null) {
      //coming back from a reclaim state
      lastFetched = savedInstanceState.getLong(LAST_FETCHED);
      Log.d(TAG, "oncreate reclaim savedinstancestate" + lastFetched);
      // if (isStale("on reclaim")) {
      //   Log.i(TAG, "oncreate reclaim is stale:" + gists);
      // }
      //destroyed without reclaim
    } else {
      lastFetched = prefs.getLong(LAST_FETCHED, 0);
    }
    initDataBinding();
    gistViewModel = ViewModelProviders.of(this, viewModelFactory).get(GistViewModel.class);
    gists = gistViewModel.getGists(isStale("oncreate"));
    //register the livedata to observe the lifecycle
    gists.observe(this, listObserver);
    swipeRefreshLayout.setOnRefreshListener(
        new SwipeRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
            Log.d(TAG, "onRefresh called from SwipeRefreshLayout");
            if (!isStale("onrefresh")) {
              swipeRefreshLayout.setRefreshing(false);
              //notify user via snackbar or something
            } else {
              gists = gistViewModel.getGists(true);
              swipeRefreshLayout.setRefreshing(false);
            }
          }
        }
    );
  }

  @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
  boolean isStale(String caller) {
    Log.d(TAG, "isStale " + caller + " " + lastFetched + " " + (System.currentTimeMillis() - lastFetched) + " " + (System.currentTimeMillis() - lastFetched > TimeUnit.MINUTES.toMillis(10)));
    return (System.currentTimeMillis() - lastFetched) >= TimeUnit.MINUTES.toMillis(10);
  }

  //If called, this method will occur before {#onStop}.
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(LAST_FETCHED, lastFetched);
    Log.d("LC", "triggered onSaveInstanceState:" + lastFetched);
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    //navigating back from stopped state
    //maybe we check here too for staleness just for correctness
    Log.d("LC", "onRestart:" + lastFetched);
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.d("LC", "onStart:");
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d("LC", "onStop: ");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // @todo persist the lastfetched in SharedPreferences for case when leave app without reclaim
    //which begs the question, why not just use sharedprefs period? what is the purpose of also having the bundle
    prefs.edit().putLong(LAST_FETCHED, lastFetched).commit();
    Log.d("LC", "onDestroy: ");
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d("LC", "onPause: ");
  }

  //init/reclaim/stop/start still in started; onPause
  @Override
  protected void onResume() {
    super.onResume();
    if (isStale("onresume")) {
      //coming back after long stop or pause
      gists = gistViewModel.getGists(true);
    }
    Log.d("LC", "onResume: " + lastFetched);
  }

  void setAdapter(List<Gist> gists) {
    rvAdapter = new ListViewAdapter(gists);
    recyclerView.setAdapter(rvAdapter);
  }

  private void initDataBinding() {
    binding = DataBindingUtil.setContentView(this, R.layout.activity_gist);
    createViews(binding);
    binding.executePendingBindings();
  }

  private void createViews(ActivityGistBinding binding) {
    swipeRefreshLayout = binding.swipeRefresh;
    swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
    recyclerView = binding.rvGists;
    recyclerView.setHasFixedSize(true);
    recyclerView.setDrawingCacheEnabled(true);
    recyclerView.setItemViewCacheSize(10);
    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(mLayoutManager);
  }
}
