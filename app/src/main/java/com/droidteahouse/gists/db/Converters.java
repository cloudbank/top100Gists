package com.droidteahouse.gists.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.arch.persistence.room.TypeConverter;

import com.droidteahouse.gists.vo.Gist;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class Converters {
  @TypeConverter
  public static List<Gist> stringToGistList(String data) {
    if (data == null) {
      return Collections.emptyList();
    }
    Type listType = new TypeToken<List<Gist>>() {
    }.getType();
    Gson gson = new Gson();
    return gson.fromJson(data, listType);
  }

  @TypeConverter
  public static String gistListToString(List<Gist> someObjects) {
    Gson gson = new Gson();
    return gson.toJson(someObjects);
  }

  @TypeConverter
  public static List<Gist.AttachedFile> stringToSomeObjectList(String data) {
    if (data == null) {
      return Collections.emptyList();
    }
    Type listType = new TypeToken<List<Gist.AttachedFile>>() {
    }.getType();
    Gson gson = new Gson();
    return gson.fromJson(data, listType);
  }

  @TypeConverter
  public static String someObjectListToString(List<Gist.AttachedFile> someObjects) {
    Gson gson = new Gson();
    return gson.toJson(someObjects);
  }
}