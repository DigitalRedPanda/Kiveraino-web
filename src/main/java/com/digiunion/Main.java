package com.digiunion;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;

import com.digiunion.env.Dotenv;
import com.digiunion.kick.OauthURLs;
import com.digiunion.servlet.SecureResponses;

import io.activej.bytebuf.ByteBuf;
import io.activej.config.Config;
import io.activej.config.converter.ConfigConverters;
import io.activej.dns.DnsClient;
import io.activej.eventloop.Eventloop;
import io.activej.http.HttpClient;
import io.activej.http.HttpHeaders;
import io.activej.http.HttpMethod;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.activej.http.HttpServer;
import io.activej.http.RoutingServlet;
import io.activej.inject.annotation.Provides;
import io.activej.reactor.nio.NioReactor;

public final class Main {
  //    public static void main(String[] args) throws IOException{
  //      var secureRandom = new ThreadLocal<SecureRandom>();
  //      secureRandom.set(new SecureRandom());
  //      //var secureRandom = new SecureRandom();
  //      var eventloop = Eventloop.create();
  //      var server = HttpServer.builder(eventloop, RoutingServlet.builder(eventloop)
  //          .with("/", request -> {
  //            var b = new byte[64];
  //            //var secureRandom = new SecureRandom();
  //            secureRandom.get().nextBytes(b); 
  //            //secureRandom.nextBytes(b); 
  //            return SecureResponses.secureDynamic(HttpResponse.ok200().withHeader(HttpHeaders.CONTENT_TYPE, "application/json").withBody(new StringBuilder("{\n\"someValue\": \"").append(URLEncoder.encode(new String(b, StandardCharsets.US_ASCII), StandardCharsets.US_ASCII)).append("\"\n}").toString())).toPromise();
  //          })
  //          .with("/a", request -> {
  //            try {
  //              var b = new byte[64];
  //              secureRandom.get().nextBytes(b); 
  //              var encoded = URLEncoder.encode(new String(b, StandardCharsets.US_ASCII), StandardCharsets.US_ASCII);
  //              return SecureResponses.secureDynamic(HttpResponse.ok200().withHeader(HttpHeaders.CONTENT_TYPE, "application/json").withBody(StringTemplate.STR."{\n\"someValue\": \"\{encoded}\"\n}")).toPromise();
  //            } catch (Exception e) {
  //              System.out.printf("[\033[31mSEVERE\033[0m] could not format message; %s\n", e.getMessage());
  //              return SecureResponses.secureDynamic(HttpResponse.ofCode(500).withHeader(HttpHeaders.CONTENT_TYPE, "application/json").withBody("{\n\"error\": \"lmao\"\n}")).toPromise();
  //            }
  //          })
  //          .with("/b", request -> {
  //            var b = new byte[64];
  //            secureRandom.get().nextBytes(b); 
  //            return SecureResponses.secureDynamic(HttpResponse.ok200().withHeader(HttpHeaders.CONTENT_TYPE, "application/json").withBody(String.format("{\n\"someValue\": \"%s\"\n}", URLEncoder.encode(new String(b, StandardCharsets.US_ASCII), StandardCharsets.US_ASCII)))).toPromise();
  //          })
  //          
  //          .build()).withListenPort(8080).build();
  //      server.listen();
  //      eventloop.run();
  //
  // }
  static {
    try {
      arrayList = Dotenv.load("/creds/creds.env");
    } catch(IOException e) {
      System.err.println("[\033[31mSEVERE\033[0m] could not find/open credentials file; " + e.getMessage());
      System.exit(1);
    }
  }

  private static String[] arrayList;
  private static Executor executor = Executors.newCachedThreadPool();

  public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, NoSuchAlgorithmException {
    //NioReactor reactor = Eventloop.create(); 
     var reactor = Eventloop.create(); 
    var client = HttpClient.builder(reactor, DnsClient.create(reactor, new InetSocketAddress("8.8.8.8", 53))).withSslEnabled(SSLContext.getInstance("TLS1.3"), executor).build();
    // 
    // var client = HttpClient.builder(eventloop, DnsClient.create(eventloop, new InetSocketAddress("localhost", 443))).withSslEnabled(SSLContext.getDefault(), executor).build();
    CompletableFuture<String> future = reactor.submit(() ->
        client.request(HttpRequest.builder(HttpMethod.POST, OauthURLs.TOKEN.url).withHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded").withBody(new StringBuilder("client_id=").append(arrayList[0]).append("&client_secret=").append(arrayList[1]).append("&grant_type=client_credentials").toString()).build()).then(response -> response.loadBody()).map(body -> body.getString(StandardCharsets.UTF_8)));
    System.out.println("اح");        
    // final CompletableFuture<String> resultt = eventloop.submit(()->
    //        client.request(HttpRequest.get("https://kick.com/api/v1/channels/".concat("sadmadladsalman"))
    //                .withHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/117.0")
    //                .withHeader(HttpHeaders.ACCEPT_ENCODING,"").build())
    //            .then(lPlusRatio -> lPlusRatio.loadBody())
    //            .map(body -> body.getString(StandardCharsets.UTF_8))
    //            .whenComplete((result, exceptione) -> System.out.printf("[\033[34mINFO\033[0m] %s has been fetched\n", "aboSalman"))
    //            .whenException(exception -> System.err.printf("\033[31mSEVERE\033[0m] could not fetch %s; %s\n", "aboSalman", exception.getMessage())));
    //eventloop.run();
    reactor.run();
    System.out.println(future.get());

  }
}
