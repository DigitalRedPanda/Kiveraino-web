package com.digiunion.controller;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.alibaba.fastjson2.JSON;
import com.digiunion.App;
import com.digiunion.kick.APIURLs;
import com.digiunion.kick.OauthURLs;
import com.digiunion.kick.model.Credentials;
import com.digiunion.servlet.SecureResponses;

import io.activej.http.HttpHeaders;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.activej.promise.Promise;

public final class AuthController { 

  private HttpClient httpClient;

  public AuthController(HttpClient httpClient) { 
    this.httpClient = httpClient;
  }

  public HttpResponse authorize(HttpRequest request) {
    return HttpResponse.ok200().build();
  } 

  public Promise<HttpResponse> token(HttpRequest request) {
    return HttpResponse.ok200().toPromise();
  }

  public Promise<HttpResponse> refresh(HttpRequest request) throws ExecutionException, InterruptedException, TimeoutException {
    var refreshToken = request.getQueryParameter("refresh_token");
    if(refreshToken != null) {
      httpClient.sendAsync(java.net.http.HttpRequest.newBuilder(URI.create(OauthURLs.TOKEN.url))
        .header("Content-Type", "application/x-www-form-urlencoded")  
        .POST(BodyPublishers.ofString(new StringBuilder("grant_type=refresh_token&token=")
            .append(refreshToken)
            .append("&client_id=")
            .append(App.arrayList[0])
            .append("&client_secret=")
            .append(App.arrayList[1])
            .toString()))
        .build(), BodyHandlers.ofString())
        .thenApply(res -> JSON.parseObject(res.body(), Credentials.class))
        .get(5, TimeUnit.SECONDS);
      return SecureResponses.secureDynamic(HttpResponse.ok200()
          .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
          
          )
        .toPromise();
    } else {
      return HttpResponse.ofCode(400).toPromise();
    }
  }


}
