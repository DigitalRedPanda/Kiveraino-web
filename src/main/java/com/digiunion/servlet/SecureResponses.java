package com.digiunion.servlet;

import io.activej.http.HttpResponse;
import io.activej.http.HttpHeaders;

public interface SecureResponses {
  /**
   * @return returns a builder for a static response that has {@link HttpHeaders} with proper and common security headers alongside some performance best practices for browsers
   * @param {@link HttpResponse.Builder} static response you want to secure
   * @see HttpResponse
   */
  public static HttpResponse.Builder secureStatic(HttpResponse.Builder response) {
    return response.withHeader(HttpHeaders.REFERRER_POLICY, "Referrer-Policy: origin-when-cross-origin")
        .withHeader(HttpHeaders.CONTENT_SECURITY_POLICY, "default-src 'self'; script-src 'self'; object-src 'none'")
        .withHeader(HttpHeaders.X_CONTENT_TYPE_OPTIONS, "nosniff")
        .withHeader(HttpHeaders.X_FRAME_OPTIONS, "DENY")        
        .withHeader(HttpHeaders.PERMISSIONS_POLICY, "geolocation=(), microphone=(), camera=()")
        .withHeader(HttpHeaders.CROSS_ORIGIN_OPENER_POLICY, "same-origin")
        .withHeader(HttpHeaders.CROSS_ORIGIN_EMBEDDER_POLICY, "require-corp")
        .withHeader(HttpHeaders.CROSS_ORIGIN_RESOURCE_POLICY, "same-site")
        .withHeader(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable") 
        .withHeader(HttpHeaders.CONTENT_ENCODING, "gzip")
        .withHeader(HttpHeaders.X_XSS_PROTECTION, "1; mode=block")
        .withHeader(HttpHeaders.X_FRAME_OPTIONS, "DENY");
  }
  /**
   * @return a builder for a dynamic response that has a response builder with proper and common security headers alongside some performance best practices for browsers
   * @param {@link HttpResponse.Builder} static response you want to secure
   * @see {@link HttpResponse} response
   */

  public static HttpResponse.Builder secureDynamic(HttpResponse.Builder response) {
    return response.withHeader(HttpHeaders.REFERRER_POLICY, "Referrer-Policy: origin-when-cross-origin")
        .withHeader(HttpHeaders.CONTENT_SECURITY_POLICY, "default-src 'self'; script-src 'self'; object-src 'none'")
        .withHeader(HttpHeaders.X_CONTENT_TYPE_OPTIONS, "nosniff")
        .withHeader(HttpHeaders.X_FRAME_OPTIONS, "DENY")        
        .withHeader(HttpHeaders.PERMISSIONS_POLICY, "geolocation=(), microphone=(), camera=()")
        .withHeader(HttpHeaders.CROSS_ORIGIN_OPENER_POLICY, "same-origin")
        .withHeader(HttpHeaders.CROSS_ORIGIN_EMBEDDER_POLICY, "require-corp")
        .withHeader(HttpHeaders.CROSS_ORIGIN_RESOURCE_POLICY, "same-site")
        .withHeader(HttpHeaders.CACHE_CONTROL, "no-store") 
        .withHeader(HttpHeaders.CONTENT_ENCODING, "gzip")
        .withHeader(HttpHeaders.X_XSS_PROTECTION, "1; mode=block")
        .withHeader(HttpHeaders.X_FRAME_OPTIONS, "DENY");
  }

}
