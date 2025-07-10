package com.digiunion.controller;

import java.nio.charset.StandardCharsets;

import com.digiunion.service.ResourceService;
import com.digiunion.servlet.SecureResponses;

import io.activej.http.HttpHeaders;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.activej.inject.annotation.Inject;
import io.activej.promise.Promise;

@Inject
public final class ResourceController {

  public Promise<HttpResponse> css(HttpRequest request){
    try {
      return ResourceService.readResource(request.getPath()).map(file -> SecureResponses.secureStatic(HttpResponse.ok200().withHeader(HttpHeaders.CONTENT_TYPE, "text/css").withHeader(HttpHeaders.VARY, "Accept-Encoding").withBody(file)).build()).whenException(e -> HttpResponse.ofCode(404).build());
    } catch (Exception e) {
      System.err.printf("[\033[31mSEVERE\033[0m] omgBruh; %s\n", e.getMessage());
      return SecureResponses.secureDynamic(HttpResponse.ofCode(404)).toPromise();
    }
  }
  public Promise<HttpResponse> js(HttpRequest request){
    try {
      return ResourceService.readResource(request.getPath()).map(file -> SecureResponses.secureStatic(HttpResponse.ok200().withHeader(HttpHeaders.CONTENT_TYPE, "text/javascript").withHeader(HttpHeaders.VARY, "Accept-Encoding").withBody(file)).build()).whenException(e -> HttpResponse.ofCode(404).build());
    } catch (Exception e) {
      System.err.printf("[\033[31mSEVERE\033[0m] omgBruh; %s\n", e.getMessage());
      return SecureResponses.secureDynamic(HttpResponse.ofCode(404)).toPromise();
    }
  }

  
}
