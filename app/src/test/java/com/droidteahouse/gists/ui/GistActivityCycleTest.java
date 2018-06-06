package com.droidteahouse.gists.ui;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowSQLiteConnection;

import java.util.concurrent.TimeUnit;

/**
 * using roboelectric instead of mockito to test lifecycle
 * testing room is not recommended here
 */
@RunWith(RobolectricTestRunner.class)
public class GistActivityCycleTest {
  ActivityController controller;

  @Before
  public void setUp() {
    ShadowSQLiteConnection.reset();
  }

  @Test
  public void isStale() {
    ActivityController controller = Robolectric.buildActivity(GistActivity.class).create().start().resume();
    GistActivity createdActivity = (GistActivity) controller.get();
    createdActivity.lastFetched = 0;
    assertThat(createdActivity.isStale("test"), is(equalTo(true)));
  }

  @Test
  public void isNotStale() {
    ActivityController controller = Robolectric.buildActivity(GistActivity.class).create().start().resume();
    GistActivity createdActivity = (GistActivity) controller.get();
    createdActivity.lastFetched = System.currentTimeMillis();
    assertThat(createdActivity.isStale("test"), is(equalTo(false)));
  }

  @Test
  public void onStopResumeNotStale() {
    ActivityController controller = Robolectric.buildActivity(GistActivity.class).create().start().resume();
    controller.stop();
    controller.start().resume();
    GistActivity activity = (GistActivity) controller.get();
    assertThat(activity.isStale("test"), is(false));
  }

  //stopped state to start
  //livedata should return r network
  @Test
  public void onStopResumeStale() {
    ActivityController controller = Robolectric.buildActivity(GistActivity.class).create().start().resume();
    controller.stop();  //or pause
    controller.start().resume();
    GistActivity activity = (GistActivity) controller.get();
    activity.lastFetched = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10);
    assertTrue(System.currentTimeMillis() - activity.lastFetched >= TimeUnit.MINUTES.toMillis(10));
    assertThat(activity.isStale("test"), is(true));
  }

  // reclaimed  with bundle timestamp for staleness
  //vm should get from network if stale
  @Test
  public void onCreateReclaimedNotStale() {
    Bundle savedInstanceState = new Bundle();
    //reclaimed before 10 mins
    long time = System.currentTimeMillis();
    savedInstanceState.putLong("lastFetched", time);
    ShadowSQLiteConnection.reset();
    GistActivity activity = Robolectric.buildActivity(GistActivity.class)
        .create(savedInstanceState)
        .start()
        .get();
    assertThat(savedInstanceState, notNullValue());
    assertThat(savedInstanceState.get("lastFetched"), is(equalTo(time)));
    assertTrue(activity.lastFetched > 0L);
    assertThat(activity.isStale("test"), is(equalTo(false)));
  }

  @Test
  public void onChangedResetLastFetched() {
    Bundle savedInstanceState = new Bundle();
    //reclaimed after 10 mins
    savedInstanceState.putLong("lastFetched", 0L);
    GistActivity activity = Robolectric.buildActivity(GistActivity.class)
        .create(savedInstanceState)
        .start()
        .get();
    assertTrue(savedInstanceState.getLong("lastFetched") == 0L);
    //onchange has reset it, maybe refactor?
    assertThat(activity.isStale("test"), is(equalTo(false)));
  }

  @Test
  public void onStartGistsHasObserver() {
    ActivityController controller = Robolectric.buildActivity(GistActivity.class).create().start().resume();
    GistActivity createdActivity = (GistActivity) controller.get();
    assertThat(createdActivity.gists.hasActiveObservers(), is(true));
  }

  @Test
  public void onConfigChangeStale() {
    Bundle savedInstanceState = new Bundle();
    //reclaimed after 10 mins, force it here or actually add 10 min to time
    savedInstanceState.putLong("lastFetched", 0);
    GistActivity activity = Robolectric.buildActivity(GistActivity.class)
        .configurationChange()
        .create(savedInstanceState)
        .get();
    assertThat(savedInstanceState, notNullValue());
    assertThat(activity.isStale("test"), is(equalTo(true)));
  }

  @Test
  public void onConfigChangeNotStale() {
    Bundle savedInstanceState = new Bundle();
    //reclaimed before 10 mins
    savedInstanceState.putLong("lastFetched", System.currentTimeMillis());
    GistActivity activity = Robolectric.buildActivity(GistActivity.class)
        .configurationChange()
        .create(savedInstanceState)
        .get();
    assertThat(savedInstanceState, notNullValue());
    assertThat(activity.isStale("test"), is(equalTo(false)));
  }
}
