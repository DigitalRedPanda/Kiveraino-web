package com.digiunion;

import io.activej.http.HttpClient;
import io.activej.http.IHttpClient;
import io.activej.http.HttpMethod;
import io.activej.http.HttpRequest;
import io.activej.dns.DnsClient;
import io.activej.inject.annotation.Provides;
import io.activej.dns.IDnsClient;
import io.activej.reactor.NioReactive;
import io.activej.reactor.nio.NioReactor;
import io.activej.eventloop.Eventloop;

import java.net.InetSocketAddress;

public class ExampleHttpClient {

  @Provides 
  IDnsClient dnsClient(NioReactor reactor) {
    return DnsClient.create(reactor, new InetSocketAddress(8080));
  }

  @Provides
  IHttpClient client(NioReactor reactor, IDnsClient dnsClient) {
    return HttpClient.builder(reactor, dnsClient).build();
  }
  @Provides
  NioReactor reactor() {
    return Eventloop.create();
  }

  public String get(String url) {
    return "";
  }





}
