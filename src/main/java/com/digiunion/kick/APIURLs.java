package com.digiunion.kick;

public enum APIURLs {
  BASE_URL("https://api.kick.com/public/v1/"),
  SUBSCRIPTIONS(BASE_URL.url + "events/subscriptions"),
  CHANNELS(BASE_URL.url + "channels"),
  USERS(BASE_URL.url + "users");
  
  public final String url;

  APIURLs(String url) {
    this.url = url;
  }

}
