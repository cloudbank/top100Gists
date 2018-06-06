package com.droidteahouse.gists.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.droidteahouse.gists.vo.Gist;

import java.util.List;
//Room allows you to return any Java-based object from your queries as
// long as the set of result columns can be mapped into the returned object

@Dao
public interface GistDao {
  //runs on bg thread by default
  @Query("SELECT * FROM gist")
  LiveData<List<Gist>> loadGists();

  //https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1
  @Query("DELETE  FROM gist")
  void clear();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public abstract void insertGists(List<Gist> repositories);
}



