package com.droidteahouse.gists.vo;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import android.util.Log;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class GistDeserializer
    implements JsonDeserializer<Gist> {
  @Override
  public Gist deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
      throws JsonParseException {
    final JsonObject jsonObject = json.getAsJsonObject();
    final String id = jsonObject.get("id").getAsString();
    final String created = jsonObject.get("created_at").getAsString();
    final String url = jsonObject.get("html_url").getAsString();
    final JsonObject ownerObject = jsonObject.has("owner") ? jsonObject.get("owner").getAsJsonObject() : new JsonObject();
    Gist.Owner owner = new Gist.Owner();
    if (ownerObject.has("login"))
      owner.login = ownerObject.get("login").getAsString();
    if (ownerObject.has("avatar_url"))
      owner.avatarUrl = ownerObject.get("avatar_url").getAsString();
    if (ownerObject.has("id"))
      owner.owner_id = ownerObject.get("id").getAsInt();
    final JsonObject filesObject = jsonObject.has("files") ? jsonObject.get("files").getAsJsonObject() : new JsonObject();
    List<Gist.AttachedFile> attachedFiles = new ArrayList<>();
    for (Map.Entry<String, JsonElement> en : filesObject.entrySet()) {
      Gist.AttachedFile attachedFile = new Gist.AttachedFile();
      JsonObject attachedFileObject = en.getValue().getAsJsonObject();
      String filename = attachedFileObject.get("filename").getAsString();
      attachedFile.setFilename(filename);
      String type = attachedFileObject.get("type") instanceof JsonNull ? "type not set" : attachedFileObject.get("type").getAsString();
      attachedFile.setType(type);
      String language = (String) (attachedFileObject.get("language") instanceof JsonNull ? "language not set" : attachedFileObject.get("language").getAsString());
      attachedFile.setLanguage(language);
      attachedFile.setSize(attachedFileObject.get("size").toString() + " bytes");
      attachedFiles.add(attachedFile);
    }
    final Gist gist = new Gist();
    gist.setId(id);
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      Date d = sdf.parse(created);
      gist.created = d.toString();
    } catch (Exception e) {
      Log.d("DEBUG", "serializer parse error" + e);
    }
    gist.url = url;
    gist.setOwner(owner);
    gist.setAttachedFiles(attachedFiles);
    return gist;
  }
}



