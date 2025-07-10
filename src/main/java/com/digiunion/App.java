package com.digiunion;

import io.activej.launchers.http.HttpServerLauncher;
import io.activej.launchers.http.MultithreadedHttpServerLauncher;
import io.activej.launcher.Launcher;
import io.activej.inject.annotation.Provides;
import io.activej.inject.annotation.Named;
import io.activej.inject.annotation.Eager;
import io.activej.inject.annotation.Inject;
import io.activej.http.HttpResponse;
import io.activej.http.AsyncServlet;
import io.activej.http.StaticServlet;
import io.activej.http.RoutingServlet;
import io.activej.http.HttpServer;
import io.activej.http.HttpCookie;
import io.activej.http.IHttpClient;
import io.activej.dns.IDnsClient;
import io.activej.dns.DnsClient;
import io.activej.reactor.Reactor;
import io.activej.inject.Injector;
import io.activej.inject.module.ModuleBuilder;
import io.activej.inject.module.Module;
import io.activej.service.ServiceGraphModule;
import io.activej.worker.annotation.Worker;
import io.activej.inject.module.AbstractModule;
import io.activej.reactor.nio.NioReactor;
import static io.activej.http.HttpMethod.GET;
import static io.activej.http.HttpMethod.POST;
import io.activej.eventloop.Eventloop;
import io.activej.http.loader.CacheStaticLoader;
import io.activej.http.loader.IStaticLoader;
import io.activej.http.HttpCookie.SameSite;


import com.alibaba.fastjson2.JSON;

import java.security.SecureRandom;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLContextSpi;
import javax.net.ssl.KeyManagerFactory;
import java.util.Base64;
import java.net.URI;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import java.time.Duration;
import java.io.InputStream;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.OffsetDateTime;
import java.time.LocalDateTime;


import io.jstach.jstachio.JStachio;
import org.simdjson.SimdJsonParser;

import com.digiunion.env.Dotenv;
import com.digiunion.model.URL;
import com.digiunion.model.URLNotFound;
import com.digiunion.model.PKCE;
import com.digiunion.model.Auth;
import com.digiunion.kick.OauthURLs;
import com.digiunion.controller.ResourceController;
import com.digiunion.database.Database;
import com.digiunion.util.StringUtils;
import com.digiunion.kick.model.Credentials;
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
	
  private static String[] arrayListUnencoded;

  private static String[] arrayList;

  private static Database database;


  public App() throws IOException {
    arrayListUnencoded = Dotenv.load("/creds/creds.env");
    arrayList = new String[arrayListUnencoded.length];
    for (int i = 0; i < arrayList.length; i++) {
      arrayList[i] = URLEncoder.encode(arrayListUnencoded[i], StandardCharsets.US_ASCII);
    }
  }
  
//  private static ConcurrentHashMap<String, PKCE> omgBruh = new ConcurrentHashMap<>();

  private ResourceController resourceController = new ResourceController();

  public static final Executor EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

  @Provides
  Executor executor() {
    return EXECUTOR;
  }

	// @Provides
	// NioReactor reactor() {
	// 	return Eventloop.create();
	// }

  @Provides
  HttpClient client(Executor executor) throws NoSuchAlgorithmException {
    return HttpClient.newBuilder().executor(executor).sslContext(SSLContext.getDefault()).version(Version.HTTP_2).build();
  }
  //@Provides
  //IHttpClient httpClient(NioReactor reactor, IDnsClient dnsClient, Executor executor) throws NoSuchAlgorithmException{
  //  return io.activej.http.HttpClient.builder(reactor, dnsClient).withSslEnabled(SSLContext.getDefault(), executor).build();
  //}
  ////
  //
  //@Provides 
  //IDnsClient dnsClient(NioReactor reactor) {
  //  return DnsClient.builder(reactor, new InetSocketAddress("localhost", 80)).withTimeout(Duration.ofSeconds(5)).build();
  //}
  //
	@Provides 
	AsyncServlet servlet(Reactor reactor, HttpClient client) throws NoSuchAlgorithmException {
		return RoutingServlet.builder(reactor)
          .with(GET, "/authorize",
	    request -> {
              try {
                var cookie = request.getCookie("kt");
                if(cookie != null) {
                  var processedCookie = StringUtils.split(cookie, '|', 1);
                  return SecureResponses.secureDynamic(HttpResponse.ok200().withHeader(io.activej.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, arrayListUnencoded[4]).withHtml(JStachio.render(new Auth(processedCookie[0], processedCookie[1])))).build().toPromise();
                } else {
                  var st = request.getCookie("st");
                  if(st == null) {
                    final SecureRandom secureRandom = new SecureRandom();
                    var state = new byte[64];
                    var codeVerifier = new byte[64];
                    secureRandom.nextBytes(codeVerifier);
                    secureRandom.nextBytes(state);
                    final java.util.Base64.Encoder encoder = Base64.getUrlEncoder();
                    final String verifier = Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
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
                  System.out.printf("[\033[31mSEVERE\033[0m] could not process request; %s\n", e.getMessage());
                  return SecureResponses.secureDynamic(HttpResponse.ofCode(500)).build().toPromise();
                }

              })
                .with(GET, "/js/*", resourceController::js)
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
        //var response = client.request(io.activej.http.HttpRequest.builder(HttpMethod.POST, OauthURLs.TOKEN.url.concat('?' + body)).withHeader(io.activej.http.HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded").build());
        //var result = response.map(res -> new String(res.getBody().getArray(), StandardCharsets.UTF_8)).getResult();
        //var code = response.map(res -> res.getCode()).getResult();
        //System.out.println(result + ",\t" + code);
        //var response = client.sendAsync(HttpRequest.newBuilder(URI.create(OauthURLs.TOKEN.url)).headers("Content-Type", "application/x-www-form-urlencoded").POST(BodyPublishers.ofString(body)).build(), BodyHandlers.ofString()).join();
        //final HttpResponse response = (HttpResponse) reactor.submit(a -> 
        //  client.request(HttpRequest.builder(HttpMethod.POST, OauthURLs.TOKEN.url).withHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded").withBody(body).build()).whenException(e -> System.out.println(e.getMessage())).getResult()
        //).get();
        final Credentials responseBody = client.sendAsync(HttpRequest.newBuilder(URI.create(OauthURLs.TOKEN.url)).headers("Content-Type", "application/x-www-form-urlencoded").POST(BodyPublishers.ofString(body)).build(), BodyHandlers.ofString()).thenApply(res -> {
          return JSON.parseObject(res.body(), Credentials.class);
        }).join();
        if (!responseBody.isEmpty()) {
          System.out.println(responseBody);
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
        """).withHeader(io.activej.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, arrayListUnencoded[4]).withCookie(HttpCookie.builder("kt").withValue(new StringBuilder(responseBody.accessToken()).append('|').append(responseBody.refreshToken()).toString()).withHttpOnly(true).withSecure(true).withSameSite(SameSite.LAX).withPath("/").withDomain(arrayListUnencoded[5]).withExpirationDate(Instant.now().plusSeconds(responseBody.expiresIn())).build())).build().toPromise();
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
          //System.err.printf("[\033[31mSEVERE\033[0m] could not send request; %s\n", e.getMessage());
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
                          var result = request.loadBody().map(body -> {
                            var signiture = new StringBuilder(request.getHeader(io.activej.http.HttpHeaders.of("Kick-Event-Message-Id"))).append('.').append(request.getHeader(io.activej.http.HttpHeaders.of("Kick-Event-Message-Timestamp"))).append('.').append(body.asString(StandardCharsets.UTF_8));
                            System.out.println(signiture);
                            return new String(body.array());
                          }).whenException(e -> System.out.printf("[\033[31mSEVERE\033[0m] could not parse body; %s\n", e.getMessage()));
                          System.out.println("body: " + result.getResult());
                          return SecureResponses.secureDynamic(HttpResponse.ok200().withJson("{\"status\": \"OK\"}")).build().toPromise();
                        })
			  .build();

	}

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
