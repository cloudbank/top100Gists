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
package com.droidteahouse.gists.db;

import static com.droidteahouse.gists.util.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import android.support.test.runner.AndroidJUnit4;

import com.droidteahouse.gists.util.TestUtil;
import com.droidteahouse.gists.vo.Gist;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class GistDaoTest extends DbTest {
  @Test
  public void insertAndRead() throws InterruptedException {
    List<Gist> gists = TestUtil.createGistList(4);
    db.gistDao().insertGists(gists);
    List<Gist> gistList = getValue(db.gistDao().loadGists());
    assertThat(gistList, notNullValue());
    assertThat(gistList.size(), is(4));
    assertThat(gistList.get(0).id, is("1"));
    assertThat(gistList.get(1).id, notNullValue());
  }

  @Test
  public void insertAndDelete() throws InterruptedException {
    List<Gist> gists = TestUtil.createGistList(4);
    db.gistDao().insertGists(gists);
    List<Gist> gistList = getValue(db.gistDao().loadGists());
    assertThat(gistList, notNullValue());
    assertThat(gistList.size(), is(4));
    db.gistDao().clear();
    gistList = getValue(db.gistDao().loadGists());
    assertThat(gistList.size(), is(0));
  }
}
