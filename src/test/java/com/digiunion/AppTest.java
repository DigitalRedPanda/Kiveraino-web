package com.digiunion;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alibaba.fastjson2.JSON;
import com.digiunion.env.Dotenv;
import com.digiunion.kick.model.KickChatEvent;
import com.digiunion.model.PKCE;
import com.digiunion.service.SecurityService;
import com.digiunion.servlet.SecureResponses;
import com.digiunion.util.StringUtils;

import io.activej.http.HttpClient;
import io.activej.http.HttpHeaders;
import io.activej.http.IHttpClient;
import io.activej.http.RoutingServlet;
import io.activej.http.HttpMethod;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.activej.http.HttpServer;
import io.activej.bytebuf.ByteBuf;
import io.activej.dns.DnsClient;
import io.activej.inject.annotation.Provides;
import io.activej.promise.Promise;
import io.activej.dns.IDnsClient;
import io.activej.reactor.NioReactive;
import io.activej.reactor.nio.NioReactor;
import io.activej.eventloop.Eventloop;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
//import org.simdjson.SimdJsonParser;

/**
 * Unit test for simple App.
 */
public class AppTest {
    // private static ConcurrentHashMap<String, PKCE> map = new ConcurrentHashMap<>();
    //
    // /**
    //  * Rigorous Test :-)
    //  */
    // @Test
    // public void hashMapTest() {
    //   CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
    //     map.put("1234", new PKCE("sometin'", "s"));
    //   }),CompletableFuture.runAsync(() -> {
    //     map.put("4321", new PKCE("sometin'", "s"));
    //   })).thenRun(() -> {
    //     map.forEach((key,value) -> {
    //       System.out.println(key + ": " + map.remove(key));
    //     });
    //   });
    // }
    //
    // @Test 
    // public void stringUtilTest() {
    //   var string = "code=erwerqwdsa32asdeyhdrhfgzfgFasxdgh&state=y3t%24%40erwer%21%40%23%24%21%40%25%2A%25%5E%26%23%24%25%40%29%3D678456erterdfgsdfher31242%40%23%23~wdfsgde%5E";
    //   System.out.printf("%s == %s?\n", StringUtils.split(string, '&', 2)[1], "state=y3t%24%40erwer%21%40%23%24%21%40%25%2A%25%5E%26%23%24%25%40%29%3D678456erterdfgsdfher31242%40%23%23~wdfsgde%5E");
    // }
    // @Test
    // public void cookieParsingTest() {
    //   var testString = "MZLMYJMYNMYTZJBJZS0ZYTIZLTKZODYTNGVLMZE2NTG0ZWU2|NTRHMDU1MZYTNMZHNS01MJC4LTLHZDGTMTBJOWNMMZFLODM2";
    //   System.out.println("MZLMYJMYNMYTZJBJZS0ZYTIZLTKZODYTNGVLMZE2NTG0ZWU2 == " + StringUtils.split(testString, '|', 1)[0]);
    // }
    // @Test 
    // public void randomTest() throws IOException {
    //   var secureRandom = new ThreadLocal<SecureRandom>();
    //   secureRandom.set(new SecureRandom());
    //   //var secureRandom = new SecureRandom();
    //   var bytes = new byte[32];
    //   var eventloop = Eventloop.create();
    //   var server = HttpServer.builder(eventloop, RoutingServlet.builder(eventloop)
    //       .with("/", request -> {
    //         var b = new byte[64];
    //         //var secureRandom = new SecureRandom();
    //         secureRandom.get().nextBytes(b); 
    //         //secureRandom.nextBytes(b); 
    //         return SecureResponses.secureDynamic(HttpResponse.ok200().withHeader(HttpHeaders.CONTENT_TYPE, "application/json").withBody(new StringBuilder("{\n\"someValue\": ").append(new String(bytes, StandardCharsets.UTF_8)).append("}").toString())).toPromise();
    //       })
    //       .build()).withListenPort(8080).build();
    //   server.listen();
    //   eventloop.run();
    // }
    private static String[] split(String string, char delimiter) {
    if(string.isEmpty()) return new String[0];
    var stringArray = new ArrayList<String>(32);
    var currentI = 0;
    for(var i = 0; i < string.length(); i++) {
      //System.out.printf("i=%d, currentI=%d\n", i, currentI);
      if(string.charAt(i) == delimiter) {
        //System.out.printf("currentIndex=%d: %c\t i=%d: %c\t slice: %s\n", currentI, temp[currentI], i, temp[i], string.substring(currentI, i));
        stringArray.add(string.substring(currentI, i));
        currentI = i + 1;
      }
    }
    if(!stringArray.isEmpty())
      stringArray.add(string.substring(currentI, string.length()));
    return (String[]) stringArray.toArray(new String[0]);
  }

    public static List<ByteBuffer> splitBytes(byte delimiter, ByteBuffer buffer) {
    List<ByteBuffer> parts = new ArrayList<>();
    buffer.flip();
    int start = buffer.position();
    while (buffer.hasRemaining()) {
        if (buffer.get() == delimiter) {
            ByteBuffer slice = buffer.duplicate();
            slice.limit(buffer.position() - 1).position(start);
            parts.add(slice.slice());
            start = buffer.position();
        }
    }
    parts.add(buffer.duplicate().position(start).slice());
    return parts;
}

    @Test
    public void publicKeyParsingTest() {
    var keyTest = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq/+l1WnlRrGSolDMA+A8\n6rAhMbQGmQ2SapVcGM3zq8ANXjnhDWocMqfWcTd95btDydITa10kDvHzw9WQOqp2\nMZI7ZyrfzJuz5nhTPCiJwTwnEtWft7nV14BYRDHvlfqPUaZ+1KR4OCaO/wWIk/rQ\nL/TjY0M70gse8rlBkbo2a8rKhu69RQTRsoaf4DVhDPEeSeI5jVrRDGAMGL3cGuyY\n6CLKGdjVEM78g3JfYOvDU/RvfqD7L89TZ3iN94jrmWdGz34JNlEI5hqK8dd7C5EF\nBEbZ5jgB8s8ReQV8H+MkuffjdAj3ajDDX3DOJMIut1lBrUVD1AaSrGCKHooWoL2e\ntwIDAQAB\n-----END PUBLIC KEY-----";
    // var partiallyParsed = split(keyTest, 'n');
    // var temp = new StringBuilder();
    // System.out.println();
    // for (var i = 1; i < partiallyParsed.length - 1; i++) {
    //   temp.append(partiallyParsed[i]);
    //   System.out.print(partiallyParsed[i]);
    // }
    // System.out.println();
    // var partiallyParsed = split(keyTest, '\n');
    // assert Arrays.equals(partiallyParsed, keyTest.split("\n")) : "failure";
    // for (int i = 1; i < partiallyParsed.length-1; i++) {
    //   System.out.println(partiallyParsed[i]);
    // }

    var start = Instant.now();
    var tres = split(keyTest, '\n');
    var end = Instant.now();
    System.out.print("result1: (");
    for (int i = 0; i < tres.length - 1; i++) { 
      System.out.printf("%s, ", tres[i]);
    }
    
    System.out.printf("%s\n", tres[tres.length - 1]);
    System.out.printf("Took around %fms\n", ((double) Duration.between(start, end).toNanos()) / Math.pow(10, 6));
    start = Instant.now();
    var tress = keyTest.split("\n");
    end = Instant.now();
    System.out.print("result2: (");
    for (int i = 0; i < tress.length - 1; i++) { 
      System.out.printf("%s, ", tress[i]);
    }
    System.out.printf("%s\n", tress[tress.length - 1]);
    System.out.printf("Took around %fms\n", ((double) Duration.between(start, end).toNanos()) / Math.pow(10, 6));
    //System.out.println(temp.toString());

    }
    @Test
    public void actualPublicKeyParsingTestNoJokefr(){
    var keyTest = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq/+l1WnlRrGSolDMA+A8\n6rAhMbQGmQ2SapVcGM3zq8ANXjnhDWocMqfWcTd95btDydITa10kDvHzw9WQOqp2\nMZI7ZyrfzJuz5nhTPCiJwTwnEtWft7nV14BYRDHvlfqPUaZ+1KR4OCaO/wWIk/rQ\nL/TjY0M70gse8rlBkbo2a8rKhu69RQTRsoaf4DVhDPEeSeI5jVrRDGAMGL3cGuyY\n6CLKGdjVEM78g3JfYOvDU/RvfqD7L89TZ3iN94jrmWdGz34JNlEI5hqK8dd7C5EF\nBEbZ5jgB8s8ReQV8H+MkuffjdAj3ajDDX3DOJMIut1lBrUVD1AaSrGCKHooWoL2e\ntwIDAQAB\n-----END PUBLIC KEY-----";
    var service = new SecurityService();
    assert keyTest.indexOf("-----BEGIN PUBLIC KEY-----\n") == service.indexOf(keyTest.getBytes(), "-----BEGIN PUBLIC KEY-----\n".getBytes(), 0);
    var partiallyParsed = split(keyTest, '\n');
    var custom = new StringBuilder();
    
    for (int i = 1; i < partiallyParsed.length -1; i++) {
      custom.append(partiallyParsed[i]);
    }
    var customy = String.valueOf(custom).getBytes();
    System.out.println(new String(Base64.getDecoder().decode(customy), StandardCharsets.UTF_8));
    var key = service.readX509PublicKey(ByteBuf.wrapForReading(keyTest.getBytes(StandardCharsets.UTF_8)));
    System.out.println(key.getResult().toString());
    
      // SimdJsonParser parser = new SimdJsonParser();
      // parser.parse
    }

    // @Test 
    // public void aTestToMakeMeFeelSane() throws IOException{
    //   Map<String, String> env = Dotenv.loadMap("creds/creds.env");
    //   for (int i = 0; i < 1000; i++) {
    //     var a = env.get("client_id");
    //   }
    //   CopyOnWriteArrayList<String> arrayList = Dotenv.loadArrayList("creds/creds.env");
    //   for (int i = 0; i < 1000; i++) {
    //     var a = arrayList.get(0);
    //   }
    //   String[] array = Dotenv.load("creds/creds.env");
    //   for (int i = 0; i < 1000; i++) {
    //     var a = array[0];
    //   }
    //
    // }
    //
    @Test
    public void aTestToMakeMeInsane() {
      var service = new SecurityService();
      var actualExample = "gsdgsdmgklsdjgtasldsdklf;sdkf";
      var actualExpTst = "djgt";
      var example = actualExample.getBytes();
      var exptst = actualExpTst.getBytes();
      System.out.printf("%d == %d ?\n", service.indexOf(example, "djgt".getBytes(), 0), new String(example, StandardCharsets.US_ASCII).indexOf("djgt"));
      assert new String(example, StandardCharsets.US_ASCII).indexOf("djgt") == service.indexOf(example, "djgt".getBytes(), 0);
      assert new String(example, StandardCharsets.US_ASCII).indexOf("dmgklsd") == service.indexOf(example, "dmgklsd".getBytes(), 0);
      var aAvg = 0d;
      var bAvg = 0d;
      var cAvg = 0d;
      var dAvg = 0d;
      var times = 1000000l;
      for (long i = 0; i < times; i++) {
        var start = Instant.now();
        var a = service.indexOf(example, exptst, 0);
        var end = Instant.now();
        a = 0;
        aAvg += ((double) Duration.between(start, end).toNanos()) / Math.pow(10, 6);
        start = Instant.now();
        var b = actualExample.indexOf(actualExpTst);
        end = Instant.now();
        b = 0;
        bAvg += ((double) Duration.between(start, end).toNanos()) / Math.pow(10, 6);
        // start = Instant.now();
        // var c = service.indexOfSimd(example, exptst);
        // end = Instant.now();
        //c = 0;
        cAvg += ((double) Duration.between(start, end).toNanos()) / Math.pow(10, 6);
        start = Instant.now();
        var d = service.indexOfOpt(example, exptst);
        end = Instant.now();
        d = 0;
        dAvg += ((double) Duration.between(start, end).toNanos()) / Math.pow(10, 6);
       
      }
      System.out.printf("a: %fms\nb: %fms\nc: %fms\nd: %fms\n", aAvg / times, bAvg / times, cAvg / times, dAvg / times);
      //assert service.indexOfSimd(example, exptst) == actualExample.indexOf(actualExpTst);
    }

@Test 
public void parseTest() {
  var payload = """
{
  "message_id": "unique_message_id_123",
  "broadcaster": {
    "is_anonymous": false,
    "user_id": 123456789,
    "username": "broadcaster_name",
    "is_verified": true,
    "profile_picture": "https://example.com/broadcaster_avatar.jpg",
    "channel_slug": "broadcaster_channel",
    "identity": null 
  },
  "sender": {
    "is_anonymous": false,
    "user_id": 987654321,
    "username": "sender_name",
    "is_verified": false,
    "profile_picture": "https://example.com/sender_avatar.jpg",
    "channel_slug": "sender_channel",
    "identity": {
      "username_color": "#FF5733",
      "badges": [
        {
          "text": "Moderator",
          "type": "moderator",
        },
        {
          "text": "Sub Gifter",
          "type": "sub_gifter",
          "count": 5,
        },
        {
          "text": "Subscriber",
          "type": "subscriber",
          "count": 3,
        }
      ]
    }
  },
  "content": "This is a test message with emotes!",
  "emotes": [
    {
      "emote_id": "12345",
      "positions": [
        { "s": 0, "e": 7 }
      ]
    },
    {
      "emote_id": "67890",
      "positions": [
        { "s": 20, "e": 25 }
      ]
    }
  ]
}
    """;
    System.out.println(JSON.parseObject(payload,KickChatEvent.class));

}
}

