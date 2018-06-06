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
package com.droidteahouse.gists.ui;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasChildCount;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.droidteahouse.gists.util.RecyclerViewMatcher;
import com.droidteahouse.gists.util.TestUtil;
import com.droidteahouse.gists.viewmodel.GistViewModel;
import com.droidteahouse.gists.vo.Gist;
import com.teahouse.gists.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class GistActivityCustomRuleTest {
  @Rule  //@todo when/how to use this for real vs fake
  public MyCustomRule<GistActivity> activityRule = new MyCustomRule<>(GistActivity.class, false, true);
  private GistViewModel viewModel;
  // private FragmentBindingAdapters fragmentBindingAdapters;

  @Before
  public void init() throws Throwable {
    //want to test the activity here w instrumentation
    //doNothing().when(viewModel).setLogin(anyString());
    //fragmentBindingAdapters = mock(FragmentBindingAdapters.class);
    //activityRule.launchActivity(null);
  }

  @NonNull
  private RecyclerViewMatcher listMatcher() {
    return new RecyclerViewMatcher(R.id.rvGists);
  }

  @Test
  public void recyclerViewItemView() {
  }

  @Test
  public void swipeRefresh() throws Exception {
    onView(withId(R.id.swipeRefresh)).perform(swipeDown()).check(matches(isDisplayed()));
  }

  @Test
  public void recyclerViewListItems() {
    for (int pos = 0; pos < 3; pos++) {
      Gist gist = activityRule.getActivity().gists.getValue().data.get(pos);
      onView(listMatcher().atPosition(pos)).check(
          matches(hasChildCount(5)));
      onView(listMatcher().atPositionOnView(pos, R.id.item_login)).check(
          matches(withText(gist.getOwner().login)));
      onView(listMatcher().atPositionOnView(pos, R.id.item_image)).check(
          matches(isDisplayed()));
      onView(listMatcher().atPositionOnView(pos, R.id.item_file_name)).check(
          matches(withText(gist.getAttachedFiles().get(0).filename)));
    }
  }

  private List<Gist> setGists(int count) {
    List<Gist> gists = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      gists.add(TestUtil.createGist(String.valueOf(i)));
    }
    // testRule.gistsData.postValue(Resource.success(gists));
    //idle?
    return gists;
  }
}