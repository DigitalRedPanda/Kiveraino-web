package com.digiunion;

import io.activej.launchers.http.HttpServerLauncher;
import io.activej.launchers.http.MultithreadedHttpServerLauncher;
import io.activej.promise.Promise;
import io.activej.promise.Promises;
import io.activej.launcher.Launcher;
import io.activej.inject.annotation.Provides;
import io.activej.inject.annotation.Named;
import io.activej.inject.annotation.Eager;
import io.activej.inject.annotation.Inject;
import io.activej.http.HttpResponse;
import io.activej.http.AsyncServlet;
import io.activej.http.RoutingServlet;
import io.activej.http.WebSocket;
import io.activej.http.HttpServer;
import io.activej.http.HttpCookie;
import io.activej.http.HttpHeaders;
import io.activej.http.HttpRequest;
import io.activej.http.IHttpClient;
import io.activej.http.IWebSocket;
import io.activej.http.IWebSocketClient;
import io.activej.dns.IDnsClient;
import io.activej.bytebuf.ByteBuf;
import io.activej.dns.DnsClient;
import io.activej.reactor.nio.NioReactor;
import static io.activej.http.HttpMethod.GET;
import static io.activej.http.HttpMethod.POST;
import io.activej.eventloop.Eventloop;
import io.activej.http.HttpCookie.SameSite;
import io.activej.http.IWebSocket.Message;

import com.alibaba.fastjson2.JSON;

import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.net.URI;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.time.Duration;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.LocalDateTime;


import io.jstach.jstachio.JStachio;

import com.digiunion.env.Dotenv;
import com.digiunion.model.URL;
import com.digiunion.model.URLNotFound;
import com.digiunion.service.SecurityService;
import com.digiunion.model.PKCE;
import com.digiunion.model.Auth;
import com.digiunion.model.Client;
import com.digiunion.kick.APIURLs;
import com.digiunion.kick.KickClient;
import com.digiunion.kick.OauthURLs;
import com.digiunion.controller.AuthController;
import com.digiunion.controller.ResourceController;
import com.digiunion.database.Database;
import com.digiunion.util.StringUtils;
import com.digiunion.kick.model.Channel;
import com.digiunion.kick.model.Credentials;
import com.digiunion.kick.model.KickChatEvent;
import com.digiunion.kick.model.TokenIntrospection;
import com.digiunion.kick.model.User;
import com.digiunion.servlet.SecureResponses;

/**
 * Hello world!
 */

@Inject
public final class App extends MultithreadedHttpServerLauncher {

  static {
    //try {
      database = new Database();
     //}*/ /*catch(IOException e) {
    //   System.err.println("[\033[31mSEVERE\033[0m] could not find/open credentials file; " + e.getMessage());
    // }*/

  }
	
  public static String[] arrayListUnencoded;

  private static KickClient kickClient;

  public static String[] arrayList;

  private static Database database;

  private static RSAPublicKey publicKey;
  private final Map<IWebSocket, Client> connections = new ConcurrentHashMap<>();
  private final Map<String, HashSet<Client>>  channels = new ConcurrentHashMap<>();

  public App() throws IOException {
    try {
      kickClient = new KickClient();
    } catch(NoSuchAlgorithmException e) {
      System.err.println("[\033[31mSEVERE\033[0m] could not start client; " + e.getMessage());
      System.exit(1);
    }
    arrayListUnencoded = Dotenv.load("/creds/creds.env");
    arrayList = new String[arrayListUnencoded.length];
    for (int i = 0; i < arrayList.length; i++) {
      arrayList[i] = URLEncoder.encode(arrayListUnencoded[i], StandardCharsets.US_ASCII);
    }
    publicKey = Promise.of(ByteBuf.wrapForReading("-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq/+l1WnlRrGSolDMA+A8\n6rAhMbQGmQ2SapVcGM3zq8ANXjnhDWocMqfWcTd95btDydITa10kDvHzw9WQOqp2\nMZI7ZyrfzJuz5nhTPCiJwTwnEtWft7nV14BYRDHvlfqPUaZ+1KR4OCaO/wWIk/rQ\nL/TjY0M70gse8rlBkbo2a8rKhu69RQTRsoaf4DVhDPEeSeI5jVrRDGAMGL3cGuyY\n6CLKGdjVEM78g3JfYOvDU/RvfqD7L89TZ3iN94jrmWdGz34JNlEI5hqK8dd7C5EF\nBEbZ5jgB8s8ReQV8H+MkuffjdAj3ajDDX3DOJMIut1lBrUVD1AaSrGCKHooWoL2e\ntwIDAQAB\n-----END PUBLIC KEY-----".getBytes(StandardCharsets.UTF_8))) 
        .then(securityService::readX509PublicKey).whenException(e -> {
          System.err.printf("[\033[31mSEVERE\033[0m] could not parse public key; %s\t%s\n", e.getCause(), e.getMessage());
        }).getResult(); 

  }
  
//  private static ConcurrentHashMap<String, PKCE> omgBruh = new ConcurrentHashMap<>();

  private static final ResourceController resourceController = new ResourceController();
  private static AuthController authController; 
  private IWebSocket webSocketClient;
  private static final SecurityService securityService = new SecurityService();

  public static final Executor EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
  public static final Eventloop REACTOR = Eventloop.create();

  @Provides
  Executor executor() {
    return EXECUTOR;
  }

	@Provides
	Eventloop reactor() {
		return REACTOR;
	}

  @Provides
  HttpClient client(Executor executor) throws NoSuchAlgorithmException {
    var client = HttpClient.newBuilder().executor(executor).sslContext(SSLContext.getDefault()).version(Version.HTTP_2).build();
    authController = new AuthController(client);
    return client;
  }


  @Provides
  IHttpClient httpClient(NioReactor reactor, IDnsClient dnsClient, Executor executor) throws NoSuchAlgorithmException{
   return io.activej.http.HttpClient.builder(reactor, dnsClient).withSslEnabled(SSLContext.getDefault(), executor).withConnectTimeout(Duration.ofSeconds(5)).build();
  }

  IWebSocket wsClient(IHttpClient client) {
    return ((IWebSocketClient) client).webSocketRequest(HttpRequest.get(new StringBuilder("wss://").append(arrayListUnencoded[5]).append("/events").toString()).build()).getResult();
  }

  @Provides 
  IDnsClient dnsClient(NioReactor reactor) {
   return DnsClient.builder(reactor, new InetSocketAddress("1.1.1.1", 853)).withTimeout(Duration.ofSeconds(5)).build();
  }

  @Provides 
  AsyncServlet servlet(Eventloop reactor, IHttpClient client, HttpClient httpClient) throws NoSuchAlgorithmException {
    return RoutingServlet.builder(reactor)
      .with(POST, "/readyup", request -> {
        if(request.getHeader(HttpHeaders.AUTHORIZATION).equals(arrayListUnencoded[6])) {
          try {
            webSocketClient = wsClient(client);
            return SecureResponses.secureDynamic(HttpResponse.ok201()).toPromise();
          } catch(Exception e) {
            System.out.printf("[\033[31mSEVERE\033[0m] could not initiate websocketClient; %s\n", e.getMessage());
            return SecureResponses.secureDynamic(HttpResponse.ofCode(500)).toPromise();
          }
        } else {
          return SecureResponses.secureDynamic(HttpResponse.ofCode(403)).toPromise();
        }


      })
    .with(GET, "/authorize",
        request -> {
          try {
            var cookie = request.getCookie("kt");
            if(cookie != null) {
              var processedCookie = StringUtils.split(cookie, '|', 1);
              return SecureResponses.secureDynamic(HttpResponse.ok200().withHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, arrayListUnencoded[4]).withHtml(JStachio.render(new Auth(processedCookie[0], processedCookie[1])))).build().toPromise();
            } else {
              var st = request.getCookie("st");
              if(st == null) {
                final SecureRandom secureRandom = new SecureRandom();
                var state = new byte[64];
                var codeVerifier = new byte[64];
                secureRandom.nextBytes(codeVerifier);
                secureRandom.nextBytes(state);
                final java.util.Base64.Encoder encoder = Base64.getUrlEncoder();
                final String verifier = encoder.withoutPadding().encodeToString(codeVerifier);
                final byte[] challenge = encoder
                  .withoutPadding()
                  .encode(MessageDigest.getInstance("SHA-256")
                      .digest(verifier.getBytes(StandardCharsets.US_ASCII)));
                final PKCE pkce = new PKCE(verifier, new String(challenge, StandardCharsets.US_ASCII));
                final String stateEncoded = encoder.withoutPadding().encodeToString(state);
                database.setEntry(stateEncoded, pkce);
                //omgBruh.put(stateEncoded, pkce);
                return SecureResponses.secureDynamic(HttpResponse.ok200().withHtml(JStachio.render(new URL(new StringBuilder(OauthURLs.AUTHORIZE.url).append("?response_type=code&client_id=").append(arrayList[0]).append("&redirect_uri=")
                          .append(arrayList[2])
                          .append("&scope=")
                          .append(URLEncoder.encode(
                              "user:read channel:read channel:write chat:write events:subscribe moderation:ban",
                              StandardCharsets.UTF_8))
                          .append("&code_challenge=").append(pkce.challenge())
                          .append("&code_challenge_method=S256").append("&state=")
                          .append(stateEncoded)
                          .toString()))).withCookie(HttpCookie.builder("st").withValue(stateEncoded).withHttpOnly(true).withSecure(true).withSameSite(SameSite.LAX).withPath("/").withDomain(arrayListUnencoded[5]).withExpirationDate(Instant.now().plusSeconds(295)).build())).build().toPromise();
              } else {
                var pkce = database.getEntry(st);
                // var test = omgBruh.get(st);
                // if(test != null) {
                //   System.out.printf("omgBruh -> %s, %s\t%s\n", st, test, test.challenge() == pkce.challenge() ? "I'm gay" : "I'm not straight");
                // }
                return SecureResponses.secureDynamic(HttpResponse.ok200().withHtml(JStachio.render(new URL(new StringBuilder(OauthURLs.AUTHORIZE.url).append("?response_type=code&client_id=").append(arrayList[0]).append("&redirect_uri=")
                          .append(arrayList[2])
                          .append("&scope=")
                          .append(URLEncoder.encode(
                              "user:read channel:read channel:write chat:write events:subscribe moderation:ban",
                              StandardCharsets.UTF_8))
                          .append("&code_challenge=").append(pkce.challenge())
                          .append("&code_challenge_method=S256").append("&state=")
                          .append(st)
                          .toString())))).build().toPromise();
              }

            }

          } catch(Exception e) {
            System.out.printf("[\033[31mSEVERE\033[0m] could not process request; %s\n%s\n", e.getMessage(), Arrays.stream(e.getStackTrace()).map(stackTrace -> String.format("%s\t%s\t%s\n", stackTrace.getClassName(), stackTrace.getMethodName(), stackTrace.getLineNumber())).collect(Collectors.toList()));
            return SecureResponses.secureDynamic(HttpResponse.ofCode(500)).build().toPromise();
          }

        })
    .with(GET, "/js/*", resourceController::js)
      .with(GET, "/refresh", authController::refresh)
      .with(GET, "/callback/auth", request -> {
        try {
          var parameters = StringUtils.split(request.getQuery(), '&', 1);
          //System.out.println("lmao");
          //System.out.println(map.get(URLEncoder.encode(parameters.get("state"), StandardCharsets.US_ASCII)));
          //map.forEach((key, value) -> {
          //  System.out.printf("%s != %s ?\n", key, URLEncoder.encode(parameters.get("state"), StandardCharsets.US_ASCII));
          //  if(key.equals(URLEncoder.encode(parameters.get("state"), StandardCharsets.US_ASCII))) {
          //    System.out.println("أسلم!!!!!");
          //  }
          //});
          var body = new StringBuilder(parameters[0]).append("&client_id=").append(arrayList[0]).append("&client_secret=").append(arrayList[1]).append("&redirect_uri=").append(arrayList[2]).append("&grant_type=authorization_code&code_verifier=").append(database.getDelEntry(StringUtils.split(parameters[1], '=', 1)[1]).verifier()).toString();
          // CompletableFuture<String> future = reactor.submit(() ->
          //     client.request(HttpRequest.builder(HttpMethod.POST, OauthURLs.TOKEN.url).withHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded").withBody(new StringBuilder("client_id=").append(arrayList[0]).append("&client_secret=").append(arrayList[1]).append("&grant_type=client_credentials").toString()).build()).then(response -> response.loadBody()).map(bodyy -> bodyy.getString(StandardCharsets.UTF_8)));
          // System.out.println("اح");        
          // final CompletableFuture<String> resultt = eventloop.submit(()->
          //        client.request(HttpRequest.get("https://kick.com/api/v1/channels/".concat("sadmadladsalman"))
          //                .withHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/117.0")
          //                .withHeader(HttpHeaders.ACCEPT_ENCODING,"").build())
          //            .then(lPlusRatio -> lPlusRatio.loadBody())
          //            .map(body -> body.getString(StandardCharsets.UTF_8))
          //            .whenComplete((result, exceptione) -> System.out.printf("[\033[34mINFO\033[0m] %s has been fetched\n", "aboSalman"))
          //            .whenException(exception -> System.err.printf("\033[31mSEVERE\033[0m] could not fetch %s; %s\n", "aboSalman", exception.getMessage())));
          //eventloop.run();
          /* future.get(); */

          // final CompletableFuture<Credentials> responseBody = reactor.submit(() -> 
          //    client.request(HttpRequest.builder(POST, OauthURLs.TOKEN.url).withHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded").withBody(body).build())
          //     .map(res -> JSON.parseObject(res.getBody().getArray(), Credentials.class)).whenResult(() -> {
          //       System.out.println("token fetched");
          //     }).whenException(e -> e.printStackTrace())
          //     );
          // reactor.run();
          // var response = responseBody.get(10, TimeUnit.SECONDS);
          var response = httpClient.sendAsync(java.net.http.HttpRequest.newBuilder(URI.create(OauthURLs.TOKEN.url)).POST(BodyPublishers.ofString(body)).header("Content-Type", "application/x-www-form-urlencoded").build(), BodyHandlers.ofString())
            .thenApply(res -> JSON.parseObject(res.body(), Credentials.class)).get(10, TimeUnit.SECONDS);

          if (!response.isEmpty()) {
            //System.out.println(responseBody);
            return SecureResponses.secureDynamic(HttpResponse.redirect301(arrayListUnencoded[4] + "/authorize").withCookie(HttpCookie.builder("st").withValue("").withHttpOnly(true).withSecure(true).withSameSite(SameSite.LAX).withPath("/").withDomain(arrayListUnencoded[5]).withExpirationDate(LocalDateTime.of(1970, 1, 1, 0, 0).toInstant(ZoneOffset.ofHoursMinutes(-3, 0))).build())
                .withHtml("""
                  <!DOCTYPE html>
                  <html>
                  <header>
                  <title>Fetching token...</title>
                  </header>
                  <body>
                  <div id="box"></div>
                  </body>
                  <style>
                  :root {
                    --text: #00e701;
                    --background: #0b0e0f; 
                    --border: #474f54;
                  }
                  body {
                    background-color: var(--background);
                  }
#box {
position: absolute;
top: 50%;
left: 50%;
transform: translate(-50%, -50%);
width: 25vw;
height: 25vw;
border: 0.25rem solid var(--border);
        border-radius: 50%;
animation: 1s ease-in-out infinite loading;
}

@keyframes loading {
0% {
width: 25vw;
height: 25vw;
}


50% {
width: 50vw;
height: 50vw;
}

100% {
width: 25vw;
height: 25vw;
}

}

</style>
</html>
""").withHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, arrayListUnencoded[4]).withCookie(HttpCookie.builder("kt").withValue(new StringBuilder(response.accessToken()).append('|').append(response.refreshToken()).toString()).withHttpOnly(true).withSecure(true).withSameSite(SameSite.LAX).withPath("/").withDomain(arrayListUnencoded[5]).withExpirationDate(Instant.now().plusSeconds(response.expiresIn())).build())).build().toPromise();
} else {
  return SecureResponses.secureDynamic(HttpResponse.ofCode(500).withCookie(HttpCookie.builder("st").withValue("").withHttpOnly(true).withSecure(true).withSameSite(SameSite.LAX).withPath("/").withDomain(arrayListUnencoded[5]).withExpirationDate(LocalDateTime.of(1970, 1, 1, 0, 0).toInstant(ZoneOffset.ofHoursMinutes(-3, 0))).build()).withHtml("""
        <!DOCTYPE html>
        <html>
        <header>
        <title>Fetching token...</title>
        </header>
        <body>
        <div id="box"><h1>Auth failed</h1><p>request failed</p></div>
        </body>
        <style>
        :root {
          --text: #00e701;
          --background: #0b0e0f; 
          --border: #474f54;
        }
        body {
          background-color: var(--background);
        }
        p {
          font-family: 'Inter', sans-serif;		
color: rgba(122,122,122,0);
animation: 0.04s ease-in 0.95s forwards opacity-shift;
        }
        h1 {
          font-family: 'Inter', sans-serif;		
color: rgba(122,122,122,0);
animation: 0.10s ease-in 0.90s forwards opacity-shift;

        }
#box {
display: flex;
         flex-direction: column;
         justify-content: center;
         align-items: center;
position: absolute;
top: 50%;
left: 50%;
transform: translate(-50%, -50%);
width: 25vw;
height: 25vw;
border: 0.1vw solid var(--border);
        border-radius: 50%;
animation: 1s ease-in-out forwards loading;
}

@keyframes opacity-shift {
0% {
color: rgba(122,122,122,0);
}
100% {
color: rgba(122,122,122,1);

}
}

@keyframes loading {
0% {
width: 25vw;
height: 25vw;
}
50% {
width: 50vw;
height: 50vw;
}
100% {
width: 25vw;
height: 25vw;
        border-color: darkred;
}

}

</style>
</html>
""")).build().toPromise();

}
//System.out.println(responseBody);
//System.out.println(result);

} catch(Exception e) {
  System.err.printf("[\033[31mSEVERE\033[0m] could not send request; %s\n", e.getMessage());
  return SecureResponses.secureDynamic(HttpResponse.ofCode(500).withCookie(HttpCookie.builder("st").withValue("").withHttpOnly(true).withSecure(true).withSameSite(SameSite.LAX).withPath("/").withDomain(arrayListUnencoded[5]).withExpirationDate(LocalDateTime.of(1970, 1, 1, 0, 0).toInstant(ZoneOffset.ofHoursMinutes(-3, 0))).build()).withHtml("""
        <!DOCYTYPE html>
        <html>
        <header>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta charset="UTF-8">
        <title>fetching token...</title>
        </header>
        <body>
        <div id="box"><h1>auth failed</h1><p>request failed</p></div>
        </body>
        <style>
        :root {
          --text: #00e701;
          --background: #0b0e0f; 
          --border: #474f54;
        }
        body {
          background-color: var(--background);
        }
        p {
          font-family: 'inter', sans-serif;		
color: rgba(122,122,122,0);
animation: 0.04s ease-in 0.95s forwards opacity-shift;
        }
        h1 {
          font-family: 'inter', sans-serif;		
color: rgba(122,122,122,0);
animation: 0.10s ease-in 0.90s forwards opacity-shift;

        }
#box {
display: flex;
         flex-direction: column;
         justify-content: center;
         align-items: center;
position: absolute;
top: 50%;
left: 50%;
transform: translate(-50%, -50%);
width: 25vw;
height: 25vw;
border: 0.1vw solid var(--border);
        border-radius: 50%;
animation: 1s ease-in-out forwards loading;
}

@keyframes opacity-shift {
0% {
color: rgba(122,122,122,0);
}
100% {
color: rgba(122,122,122,1);

}
}

@keyframes loading {
0% {
width: 25vw;
height: 25vw;
}
50% {
width: 50vw;
height: 50vw;
}
100% {
width: 25vw;
height: 25vw;
        border-color: darkred;
}

}

</style>
</html>
""")).build().toPromise();
}

})
.with(GET, "/*", request -> SecureResponses.secureDynamic(HttpResponse.ofCode(404)
      .withHtml(JStachio.render(new URLNotFound(request.getRelativePath())))).build().toPromise())
.with("/css/*", resourceController::css)
// .with(GET, "/", request -> {
//     try {
//       return SecureResponses.secureDynamic(HttpResponse.ok200().withHtml(new String(loader.load("main.html")/*.whenException(e -> System.out.printf("[\033[31mSEVERE\033[0m] could not process request; %s\n", e.getMessage()))*/.getResult().array()))).build().toPromise();
//     } catch(Exception e) {
//       //System.out.printf("[\033[31mSEVERE\033[0m] could not process request; %s\n", e.getMessage());
//       return SecureResponses.secureDynamic(HttpResponse.ofCode(501).withHtml(JStachio.render(new URLNotFound(request.getRelativePath())))).build().toPromise();
//     }
//   }
//         )
.with(POST, "/callback", request -> {

  request.loadBody().map(body -> {
    //
    //   //System.out.println(body.asString(StandardCharsets.UTF_8)); 
    //   System.out.printf("validity: %b\n", securityService.verify(publicKey, request.getHeader(HttpHeaders.of("Kick-Event-Message-Id")), body.asString(StandardCharsets.UTF_8),request.getHeader(io.activej.http.HttpHeaders.of("Kick-Event-Message-Timestamp")), request.getHeader(io.activej.http.HttpHeaders.of("Kick-Event-Signature"))).getResult());
    //   System.out.println("body: " + body.asString(StandardCharsets.UTF_8));
    //   return new String(body.array());
    // }).whenException(e -> System.out.printf("[\033[31mSEVERE\033[0m] could not parse body; %s\n%s\n", e.getMessage(), Arrays.stream(e.getStackTrace()).map(a -> String.format("%s: %s\n", a.getClassName(), a.getLineNumber())).collect(Collectors.toList())));
    // return SecureResponses.secureDynamic(HttpResponse.ok200().withJson("{\"status\": \"OK\"}")).build().toPromise();
    // 
    String messageId = request.getHeader(HttpHeaders.of("Kick-Event-Message-Id"));
    String timestamp = request.getHeader(HttpHeaders.of("Kick-Event-Message-Timestamp"));
    String signature = request.getHeader(HttpHeaders.of("Kick-Event-Signature"));
    String bodyStr = body.asString(StandardCharsets.UTF_8);

    securityService.verify(publicKey, messageId, timestamp, bodyStr, signature)
      .whenResult(valid -> {
        var event = JSON.parseObject(bodyStr, KickChatEvent.class);
        var chatters = channels.get(event.broadcaster().username());
        for (Client chatter: chatters) {
          chatter.webSocket().writeMessage(Message.text(bodyStr))
            .whenException(e -> System.err.printf("[\033[31mSEVERE\033[0m] could not send message to %s; %s\n", chatter.nickName(), e.getMessage()));
        }
        System.out.printf("event: %s\n", event);
        System.out.printf("Validation result: %b\n", valid);
      })
    .whenException(e -> System.err.println("Verification error: " + e.getMessage()));
    return true;
});
return SecureResponses.secureDynamic(HttpResponse.ok200().withJson("{\"status\": \"OK\"}")).build().toPromise();
})
.withWebSocket("/events", webSocket -> {

  var headers = webSocket.getResponse().getHeaders().stream().map(header -> String.format("%s: %s\n", header.getKey(), header.getValue())).reduce("", (first, second) -> first + second);
  System.out.printf("new connection: \n%s", headers);
  webSocket.readMessage().then(msg -> {
    final String[] message = msg.getText().split(" ", 1);
    switch(message[0]) {
      case "PASS" -> {
        final String[] oauth = message[1].split(":", 1);
        if(oauth[0].equals("oauth") && oauth.length == 2) {
          var tokenIntrospection = kickClient.validateToken(oauth[1]).get(5, TimeUnit.SECONDS);
          if(tokenIntrospection.data().active()) {

          }
          return Promise.complete();
        }
      }

      case "NICK" -> {

      }
      default -> {}
    }
    return Promise.complete();
  }).whenComplete(() -> {
    System.out.println("finished lmao");
  });
//   String name = webSocket.readMessage().map(msg -> {
//     final String[] currentMsg = msg.getText().split(" ");
//     Thread.sleep(5);
//     if(currentMsg[0].equals("PASS")) {
//       final String[] tkn = currentMsg[1].split(":");
//       if(tkn[0].equals("oauth")) {
//         final TokenIntrospection tokenIntrospection = httpClient.sendAsync(java.net.http.HttpRequest.newBuilder(URI.create(APIURLs.INTROSPECT_TOKEN.url)).POST(BodyPublishers.noBody()).header("Authorization", "Bearer " + tkn).build(), BodyHandlers.ofString())
//           .thenApply(response -> JSON.parseObject(response.body(), TokenIntrospection.class)).get(5, TimeUnit.SECONDS);
//         if(tokenIntrospection.data().active()) {
//           final String namee = webSocket.readMessage().map(message -> {
//             System.out.println(message.getText());
//             final String[] nickName = message.getText().split(" ");
//             if(nickName[0].equals("NICK")) {
//               return nickName[1];
//             } else {
//               return "";
//             }
//           })
//             .whenException(e -> System.err.printf("[\033[31mSEVERE\033[0m] could not process the nickname; %s\t %s\n",e.getMessage(), e.toString()))
//             .getResult();
//           if(!namee.isBlank()) {
//             connections.put(webSocket, new Client(namee, null, arrayListUnencoded[5], null, webSocket));
//           }
//
//       //var res = httpClient.sendAsync(java.net.http.HttpRequest.newBuilder(URI.create(APIURLs.)).build(), BodyHandlers.ofString()).get(5, TimeUnit.SECONDS);
//             }
//           return webSocket.readMessage().map(message -> {
//
//             System.out.println(message.getText());
//             final String[] nickName = message.getText().split(" ");
//             if(nickName[0].equals("NICK")) {
//               return nickName[1];
//             } else {
//               return "";
//             }
//           }).getResult();
//
//
//       } else {
//
//           return "";
//     }
//     } else {
//       return "";
//     }
//   }).getResult();
//   System.out.println(name);
//   webSocket.messageReadChannel().peek(msg -> {
//     final String[] $ = msg.getText().split(" ");
//     System.out.println($[0]);
//     if($.length == 2) {
//       switch ($[0]) {
//         case "JOIN" -> {
//   if(connections.get(webSocket) != null)
//   try {
//     if($[1].startsWith("#")) {
//       var channelName = $[1].substring(1);
//       var channel = channels.get(channelName);
//       if(channel ==  null) {
//         httpClient.sendAsync(java.net.http.HttpRequest.newBuilder(URI.create(APIURLs.CHANNELS.url)).method("GET", BodyPublishers.ofString("slug=" + channelName)).headers("Content-Type", "application/x-www-form-urlencoded", "Authorization", "Bearer " + arrayListUnencoded[6]).build(), BodyHandlers.ofString()).thenComposeAsync(resul -> {
//           return httpClient.sendAsync(java.net.http.HttpRequest.newBuilder(URI.create(APIURLs.SUBSCRIPTIONS.url)).POST(BodyPublishers.ofString(new StringBuilder("{\"broadcaster_id\": ").append(JSON.parseArray(resul.body(), Channel.class).get(0).broadcasterUserId()).append(",\"events\": [ {\"name\": \"chat.message.sent\", \"version\": 1}, {\"name\": \"channel.subscription.renewal\", \"version\": 1}, {\"name\": \"channel.subscription.new\", \"version\": 1}, {\"name\": \"livestream.status.updated\", \"version\": 1}, {\"name\": \"moderation.banned\", \"version\": 1}], \"method\": \"webhook\"}").toString())).headers("Content-Type", "application/json", "Authorization", "Bearer " + arrayListUnencoded[6]).build(), BodyHandlers.ofString());
//         }).get(5, TimeUnit.SECONDS);
//         final HashSet<Client> set = channels.get(channelName);
//         set.add(connections.get(webSocket));
//         channels.put(channelName, set);
//       } else {
//         final HashSet<Client> set = channels.get(channelName);
//         set.add(connections.get(webSocket));
//         channels.put(channelName, set);
//       }
//     }   
//   } catch(InterruptedException | ExecutionException | TimeoutException | NullPointerException e) {
//     System.out.printf("[\033[31mSEVERE\033[0m] could not join channel; %s\t%s\n", e.getCause(), e.getMessage());
//   }
//         }
// // reactor.submit(() -> client.request(HttpRequest.builder(POST, APIURLs.SUBSCRIPTIONS.url).withHeader(HttpHeaders.AUTHORIZATION, "Bearer " + arrayListUnencoded[6]).build()).whenResult(() -> {
// //
// // }));
//         case "PASS" -> {
//   try {
//     System.out.println($[0] + " " + $[1]);
//     final String tkn = $[1].split(":", 1)[1];
//     final TokenIntrospection tokenIntrospection = httpClient.sendAsync(java.net.http.HttpRequest.newBuilder(URI.create(APIURLs.INTROSPECT_TOKEN.url)).POST(BodyPublishers.noBody()).header("Authorization", "Bearer " + tkn).build(), BodyHandlers.ofString())
//       .thenApply(response -> JSON.parseObject(response.body(), TokenIntrospection.class)).get(5, TimeUnit.SECONDS);
//     if(tokenIntrospection.data().active()) {
//       final String namee = webSocket.messageReadChannel().map(message -> {
//
//         System.out.println(message.getText());
//         final String[] nickName = message.getText().split(" ");
//         if(nickName[0].equals("NICK")) {
//           return nickName[1];
//         } else {
//           return "";
//         }
//       }).get()
//         .whenException(e -> System.err.printf("[\033[31mSEVERE\033[0m] could not process the nickname; %s\t %s\n",e.getMessage(), e.toString()))
//         .getResult();
//       if(!namee.isBlank()) {
//         connections.put(webSocket, new Client(namee, null, arrayListUnencoded[5], null, webSocket));
//       }
//
//       //var res = httpClient.sendAsync(java.net.http.HttpRequest.newBuilder(URI.create(APIURLs.)).build(), BodyHandlers.ofString()).get(5, TimeUnit.SECONDS);
//     }
//   } catch (TimeoutException | ExecutionException | InterruptedException | NullPointerException e) {
//     System.out.printf("[\033[31mSEVERE\033[0m] could not pass token; %s\t%s\n", e.getCause(), e.getMessage());
//   }
//         }
// // case "NICK" -> {
// //   
// // }
// default -> {}
//       }
//       webSocket.close();
//     } else {
//       webSocket.closeEx(new ProtocolException("invalid credentials"));
//     }
//   }
//   );
//
//   //connections.put(webSocket);
//   // webSocket.messageReadChannel()
//   //   .streamTo(ChannelConsumers.ofConsumer(msg -> {
//   //     for (final IWebSocket con : connections) {
//   //       con.writeMessage(msg);
//   //     }
//   //   }));
}
)/*{
// .loop(($, e) -> {
//   if (e == null) {
//     return Promise.complete()
//       .then(() -> webSocket.readMessage())
//       .then(message -> {
//         if (message instanceof Text text) {
//           String msg = text.getText();
//           System.out.println("Received: " + msg);
//
//           for (WebSocket conn : connections) {
//             if (conn != webSocket) {
//               conn.writeMessage(Message.text(msg));
//             }
//           }
//         }
//         return Promise.complete();
//       })
//   } else {
//     // Connection closed or error occurred
//     connections.remove(webSocket);
//     System.out.println("Connection closed. Total: " + connections.size());
//     return Promise.complete();
//   }
// });
//
// while (true) {
//   var promise = webSocket.readMessage().then(msg -> {
//     for (var con : connections) {
//       con.writeMessage(msg);
//     };
//     return Promise.complete();
//   });
//   promise.getResult();
// }
/*

try{
// websocket.readFrame().then($ -> {
//   System.out.printf("content: %s\ntype: %s\n", $.getPayload().asString(StandardCharsets.UTF_8), $.getType().toString());
//
//   return null;
// });
websocket.readMessage().then(msg -> {
if (msg != null) {
var message = msg.getText();
System.out.printf("content: %s\ntype: %s\n", message, msg.getType().toString());
} else {
System.out.println("lmao");
}
return websocket.writeMessage(Message.text("Lmao."));
}).whenResult($ -> {
// switch (msg.getText()) {
//   case "اه":
//     break;
//   default:
//     websocket.writeMessage(Message.text("")).whenResult(a -> {}).whenException();
// }
}).whenComplete(() -> {
System.out.println("sent");
}).whenException(e -> {
switch (e) {
case WebSocketException webSocketException -> {
System.out.printf("[\033[31mSEVERE\033[0m] Signal received; %s", e.getCause(), e.getMessage());
}

default -> {
System.out.printf("[\033[31mSEVERE\033[0m] Unknown signal received; %s\t%s\n", e.getCause(), e.getMessage());
Arrays.stream(e.getStackTrace()).forEach(stackTrace -> System.out.printf("%s\n", stackTrace.toString()));
}
}
});
} catch(Exception e ){
System.out.printf("like, kys; %s; %s\n", e.getCause(), e.getMessage());
}
})*/
.build();

}

        // private Consumer<IWebSocket> messageHandler(IWebSocket ws){
        //   return webSocket -> {
        //     connections.add(webSocket);
        //     webSocket.messageReadChannel()
        //       .streamTo(ChannelConsumers.ofConsumer(msg -> {
        //         for (final IWebSocket con : connections) {
        //           con.writeMessage(msg);
        //         }
        //       }));
        //   };
        // }
	@Provides
	SSLContext sslContext() throws Exception {
		// Load your keystore (PKCS12 format recommended)
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		try (InputStream is = Files.newInputStream(Paths.get("keystore.p12"))) {
			keyStore.load(is, "password".toCharArray());
		}

		// Initialize with TLS 1.3
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(keyStore, "password".toCharArray());

		SSLContext sslContext = SSLContext.getInstance("TLSv1.3"); // Explicit TLS 1.3
		sslContext.init(kmf.getKeyManagers(), null, null);
		return sslContext;
	}

	@Provides
	@Eager
	HttpServer server(NioReactor reactor, AsyncServlet servlet, Executor executor) throws NoSuchAlgorithmException {
		return HttpServer.builder(reactor, servlet)
                        .withListenPort(80)
			.build();
	}

	// @Override
	// protected Module getModule() {
	// 	return ServiceGraphModule.builder().build();
	// }

	@Override
	protected void run() throws Exception {
		logger0.info("HTTP Server is now available at http://localhost: 80");
		awaitShutdown();
	}

	public static void main(String[] args) throws Exception {
		Launcher launcher = new App();
		launcher.launch(args);
	}
}
