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
package com.droidteahouse.gists.util;

import com.droidteahouse.gists.vo.Gist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestUtil {
  public static List<Gist> createGistList(int count) {
    List<Gist> gists = new ArrayList<>();
    for (int i = 1; i <= count; i++) {
      gists.add(createGist(String.valueOf(i)));
    }
    return gists;
  }

  public static Gist createGist(String id) {
    Gist g = new Gist();
    g.id = id;
    g.url = "url" + id;
    g.created = new Date().toString();
    Gist.Owner owner = new Gist.Owner();
    g.owner = owner;
    g.owner.login = "Foo:" + id;
    Gist.AttachedFile af = new Gist.AttachedFile();
    af.size = "6000";
    (g.attachedFiles = new ArrayList()).add(af);
    //nonnull constraint
    return g;
  }
}
