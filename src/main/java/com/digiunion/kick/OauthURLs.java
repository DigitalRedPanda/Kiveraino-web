package com.digiunion.kick;

public enum OauthURLs {
  BASE_URL("https://id.kick.com/"),
  OAUTH(BASE_URL.url + "oauth/"),
  AUTHORIZE(OAUTH.url + "authorize"),
  TOKEN(OAUTH.url + "token");
  
  public final String url;

  OauthURLs(String url) {
    this.url = url;
  }

}
