package com.droidteahouse.gists.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.droidteahouse.gists.vo.Gist;

@Database(entities = {Gist.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class GithubDatabase extends RoomDatabase {
  public abstract GistDao gistDao();
}

