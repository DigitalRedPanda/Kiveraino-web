package com.digiunion.kick;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.SSLContext;

import com.alibaba.fastjson2.JSON;
import com.digiunion.App;
import com.digiunion.kick.model.Channel;
import com.digiunion.kick.model.TokenIntrospection;
import com.digiunion.kick.model.User;

public class KickClient {

  private final HttpClient httpClient;
  
  public KickClient() throws NoSuchAlgorithmException {
    httpClient = HttpClient.newBuilder()
      .executor(App.EXECUTOR)
      .sslContext(SSLContext.getDefault())
      .version(Version.HTTP_2)
      .build();
  }

  public CompletableFuture<TokenIntrospection> validateToken(String token){
    if(token != null)
      return httpClient.sendAsync(java.net.http.HttpRequest.newBuilder(URI.create(APIURLs.INTROSPECT_TOKEN.url)).POST(BodyPublishers.noBody()).header("Authorization", new StringBuilder("Bearer ").append(token).toString()).build(), BodyHandlers.ofString())
      .thenApply(response -> JSON.parseObject(response.body(), TokenIntrospection.class));
    else 
      return CompletableFuture.failedFuture(new NullPointerException(""));
    
  }

  public CompletableFuture<Channel> getChannelByLogin(String login) {
    if(!(login == null || login.isBlank())) {
      return httpClient.sendAsync(HttpRequest.newBuilder(URI.create(APIURLs.CHANNELS.url)).headers("Content-Type","application/x-www-form-urlencoded", "Authorization", App.arrayListUnencoded[6]).POST(BodyPublishers.ofString(new StringBuilder("login=").append(login).toString())).build(), BodyHandlers.ofString()).thenApply(response ->  JSON.parseObject(response.body(), Channel.class));     
    } else {
      return CompletableFuture.failedFuture(new IllegalArgumentException("you should at least pass an argument or something"));
    }
  }

  public CompletableFuture<Channel> getChannelById(String id) throws InterruptedException, IOException{
    if(!(id == null || id.isBlank())) {
      return httpClient.sendAsync(HttpRequest.newBuilder(URI.create(APIURLs.CHANNELS.url)).headers("Content-Type","application/x-www-form-urlencoded", "Authorization", App.arrayListUnencoded[6]).POST(BodyPublishers.ofString(new StringBuilder("id=").append(id).toString())).build(), BodyHandlers.ofString()).thenApply(response ->  JSON.parseObject(response.body(), Channel.class));
    } else {
      return CompletableFuture.failedFuture(new IllegalArgumentException("you should at least pass an argument or something"));
    }
  }

}


