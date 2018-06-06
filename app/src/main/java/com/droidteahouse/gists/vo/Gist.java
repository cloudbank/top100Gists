package com.droidteahouse.gists.vo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.droidteahouse.gists.db.Converters;

import java.math.BigInteger;
import java.util.List;

@Entity(primaryKeys = "id")
public class Gist {
  @NonNull
  @SerializedName("id")
  @Expose
  public String id;
  @NonNull
  @SerializedName("created_at")
  @Expose
  //"created_at": "2010-04-14T02:15:15Z",
  public String created;
  @NonNull
  @SerializedName("html_url")
  @Expose
  public String url;
  @SerializedName("owner")
  @Expose
  @Embedded(prefix = "owner_")
  public Owner owner;
  @SerializedName("files")
  @Expose
  @TypeConverters(Converters.class)
  public List<AttachedFile> attachedFiles;

  //https://stackoverflow.com/questions/18645050/is-default-no-args-constructor-mandatory-for-gson
  public Gist() {
  }

  @NonNull
  public String getId() {
    return id;
  }

  public void setId(@NonNull String id) {
    this.id = id;
  }

  public Owner getOwner() {
    return owner;
  }

  public void setOwner(Owner owner) {
    this.owner = owner;
  }

  public List<AttachedFile> getAttachedFiles() {
    return attachedFiles;
  }

  public void setAttachedFiles(List<AttachedFile> attachedFiles) {
    this.attachedFiles = attachedFiles;
  }

  @Override  //@todo review
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (this == o) {
      return true;
    }
    Gist g = (Gist) o;
    return this.id.equals(g.id);
  }

  @Override //(from EJ)
  public int hashCode() {
    int result = 17;
    BigInteger c = new BigInteger(id, 16);
    result = (int) 31 * result + c.intValue();
    return result;
  }

  public static class Owner {
    @NonNull
    @SerializedName("id")
    @Expose
    public Integer owner_id;
    @SerializedName("avatar_url")
    @Expose
    public String avatarUrl;
    @SerializedName("login")
    @Expose
    public String login;

    public Owner() {
    }
  }

  public static class AttachedFile {
    @NonNull
    @SerializedName("filename")
    @Expose
    public String filename;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("language")
    @Expose
    public String language;
    @SerializedName("size")
    @Expose
    public String size;

    public AttachedFile() {
    }

    public String getFilename() {
      return filename;
    }

    public void setFilename(@NonNull String filename) {
      this.filename = filename;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getLanguage() {
      return language;
    }

    public void setLanguage(String language) {
      this.language = language;
    }

    public String getSize() {
      return size;
    }

    public void setSize(String size) {
      this.size = size;
    }
  }
}