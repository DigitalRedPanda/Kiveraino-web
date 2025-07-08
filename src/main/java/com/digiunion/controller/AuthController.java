package com.digiunion.controller;


import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.activej.promise.Promise;

public final class AuthController { 
  public HttpResponse authorize(HttpRequest request) {
    return HttpResponse.ok200().build();
  } 

  public HttpResponse token(HttpRequest request) {
    return HttpResponse.ok200().build();
  }


}
