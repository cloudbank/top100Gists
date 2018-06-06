package com.droidteahouse.gists.viewmodel;

import android.databinding.BaseObservable;

import com.droidteahouse.gists.vo.Gist;

import java.util.List;

/**
 */
public class ItemViewModel extends BaseObservable {
  private Gist gist;

  public ItemViewModel(Gist gist) {
    setGist(gist);
  }

  public Gist getGist() {
    return gist;
  }

  public void setGist(Gist gist) {
    this.gist = gist;
  }

  public List<Gist.AttachedFile> getAttachedFiles() {
    return this.gist.attachedFiles;
  }

  public void setAttachedFiles(List<Gist.AttachedFile> files) {
    this.gist.attachedFiles = files;
  }

  public String getOwnerAvatarUrl() {
    return gist.owner != null && gist.owner.avatarUrl != null && gist.owner.avatarUrl.trim().length() > 0 ? gist.owner.avatarUrl : "noimage";
  }

  public void setOwnerAvatarUrl(String url) {
    gist.owner.avatarUrl = url;
  }

  public String getOwnerLogin() {
    return gist.owner != null && gist.owner.login != null && gist.owner.login.trim().length() > 0 ? gist.owner.login : "nologin";
  }

  public void setOwnerLogin(String login) {
    gist.owner.login = login;
  }

  public String getCreated() {
    return gist.created;
  }

  public String getUrl() {
    return "<html><a href=\"" + gist.url + "\">view on github</a></html>";
  }
}
