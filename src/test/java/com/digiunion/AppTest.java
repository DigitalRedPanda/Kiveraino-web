package com.digiunion;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.digiunion.model.PKCE;
import com.digiunion.util.StringUtils;

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

import java.util.concurrent.ConcurrentHashMap;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    private static ConcurrentHashMap<String, PKCE> map = new ConcurrentHashMap<>();

    /**
     * Rigorous Test :-)
     */
    @Test
    public void hashMapTest() {
      CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
        map.put("1234", new PKCE("sometin'", "s"));
      }),CompletableFuture.runAsync(() -> {
        map.put("4321", new PKCE("sometin'", "s"));
      })).thenRun(() -> {
        map.forEach((key,value) -> {
          System.out.println(key + ": " + map.remove(key));
        });
      });
    }

    @Test 
    public void stringUtilTest() {
      var string = "code=erwerqwdsa32asdeyhdrhfgzfgFasxdgh&state=y3t%24%40erwer%21%40%23%24%21%40%25%2A%25%5E%26%23%24%25%40%29%3D678456erterdfgsdfher31242%40%23%23~wdfsgde%5E";
      System.out.printf("%s == %s?\n", StringUtils.split(string, '&', 2)[1], "state=y3t%24%40erwer%21%40%23%24%21%40%25%2A%25%5E%26%23%24%25%40%29%3D678456erterdfgsdfher31242%40%23%23~wdfsgde%5E");
    }

}
