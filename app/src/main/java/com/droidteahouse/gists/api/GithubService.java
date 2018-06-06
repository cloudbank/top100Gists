package com.droidteahouse.gists.api;

import android.arch.lifecycle.LiveData;

import com.droidteahouse.gists.vo.Gist;

import java.util.List;

import retrofit2.http.GET;

public interface GithubService {
  @GET("/gists/public?per_page=100")
  LiveData<ApiResponse<List<Gist>>> getGists();
}
