package com.droidteahouse.gists.vo;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

import com.droidteahouse.gists.util.TestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

//using mocking instead of roboelectric
@RunWith(JUnit4.class)
public class GistTest {
  List<Gist> gists = new ArrayList<Gist>();

  //equals and hashcode impls
  @Before
  public void init() {
    gists.add(TestUtil.createGist("1"));
    gists.add(TestUtil.createGist("2"));
    gists.add(TestUtil.createGist("3"));
  }

  @Test
  public void equalsAreEqual() {
    assertFalse(gists.get(0).equals(gists.get(1)));
    assertTrue(gists.get(0).equals(gists.get(0)));
  }

  @Test
  public void hashcodesAreNotEqual() {
    assertNotSame(gists.get(0).hashCode(), gists.get(1).hashCode());
    assertNotSame(gists.get(1).hashCode(), gists.get(2).hashCode());
  }
}