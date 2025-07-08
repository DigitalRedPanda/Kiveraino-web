package com.digiunion;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.MessageFormat;

import com.digiunion.servlet.SecureResponses;

import io.activej.eventloop.Eventloop;
import io.activej.http.HttpHeaders;
import io.activej.http.HttpResponse;
import io.activej.http.HttpServer;
import io.activej.http.RoutingServlet;

public final class Main {
    public static void main(String[] args) throws IOException{
      var secureRandom = new ThreadLocal<SecureRandom>();
      secureRandom.set(new SecureRandom());
      //var secureRandom = new SecureRandom();
      var eventloop = Eventloop.create();
      var server = HttpServer.builder(eventloop, RoutingServlet.builder(eventloop)
          .with("/", request -> {
            var b = new byte[64];
            //var secureRandom = new SecureRandom();
            secureRandom.get().nextBytes(b); 
            //secureRandom.nextBytes(b); 
            return SecureResponses.secureDynamic(HttpResponse.ok200().withHeader(HttpHeaders.CONTENT_TYPE, "application/json").withBody(new StringBuilder("{\n\"someValue\": \"").append(URLEncoder.encode(new String(b, StandardCharsets.US_ASCII), StandardCharsets.US_ASCII)).append("\"\n}").toString())).toPromise();
          })
          .with("/a", request -> {
            try {
              var b = new byte[64];
              secureRandom.get().nextBytes(b); 
              var encoded = URLEncoder.encode(new String(b, StandardCharsets.US_ASCII), StandardCharsets.US_ASCII);
              return SecureResponses.secureDynamic(HttpResponse.ok200().withHeader(HttpHeaders.CONTENT_TYPE, "application/json").withBody(StringTemplate.STR."{\n\"someValue\": \"\{encoded}\"\n}")).toPromise();
            } catch (Exception e) {
              System.out.printf("[\033[31mSEVERE\033[0m] could not format message; %s\n", e.getMessage());
              return SecureResponses.secureDynamic(HttpResponse.ofCode(500).withHeader(HttpHeaders.CONTENT_TYPE, "application/json").withBody("{\n\"error\": \"lmao\"\n}")).toPromise();
            }
          })
          .with("/b", request -> {
            var b = new byte[64];
            secureRandom.get().nextBytes(b); 
            return SecureResponses.secureDynamic(HttpResponse.ok200().withHeader(HttpHeaders.CONTENT_TYPE, "application/json").withBody(String.format("{\n\"someValue\": \"%s\"\n}", URLEncoder.encode(new String(b, StandardCharsets.US_ASCII), StandardCharsets.US_ASCII)))).toPromise();
          })
          
          .build()).withListenPort(8080).build();
      server.listen();
      eventloop.run();

	}

}
