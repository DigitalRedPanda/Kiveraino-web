package com.digiunion;

import io.activej.launchers.http.HttpServerLauncher;
import io.activej.launcher.Launcher;
import io.activej.inject.annotation.Provides;
import io.activej.inject.annotation.Named;
import io.activej.inject.annotation.Eager;
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
import io.activej.inject.module.AbstractModule;
import io.activej.reactor.nio.NioReactor;
import static io.activej.http.HttpMethod.GET;
import io.activej.http.HttpMethod;
import io.activej.eventloop.Eventloop;
import io.activej.http.loader.CacheStaticLoader;
import io.activej.http.loader.IStaticLoader;

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
import java.io.InputStream;
import java.io.IOException;

import io.jstach.jstachio.JStachio;

import org.simdjson.SimdJsonParser;

import com.digiunion.env.Dotenv;
import com.digiunion.model.URL;
import com.digiunion.model.URLNotFound;
import com.digiunion.model.PKCE;
import com.digiunion.kick.OauthURLs;

/**
 * Hello world!
 */

public final class App extends Launcher {
  static {
    try {
      arrayList = Dotenv.load("/creds/creds.env");
    } catch(IOException e) {
      System.err.println("[\033[31mSEVERE\033[0m] could not find/open credentials file; " + e.getMessage());
    }

  }
	public static CopyOnWriteArrayList<String> arrayList;

  public static ConcurrentHashMap<String, PKCE> map = new ConcurrentHashMap<>();

  @Provides
  Executor executor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }

	@Provides
	NioReactor reactor() {
		return Eventloop.create();
	}

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
  //  return DnsClient.builder(reactor, new InetSocketAddress("localhost", 8080)).build();
  //}
  //
  @Provides
  IStaticLoader staticLoader(Reactor reactor, Executor executor) {
    return IStaticLoader.ofClassPath(reactor, executor, "template/");
  }

	@Provides
	AsyncServlet servlet(Reactor reactor, HttpClient client/*, IStaticLoader loader*/) throws NoSuchAlgorithmException {
		return RoutingServlet.builder(reactor).with(GET, "/authorize",
		  request -> {
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
        final String stateEncoded = URLEncoder.encode(new String(state, StandardCharsets.US_ASCII), StandardCharsets.US_ASCII);
        map.put(stateEncoded, pkce);
        return HttpResponse.ok200().withHtml(JStachio.render(new URL(new StringBuilder(OauthURLs.AUTHORIZE.url).append("?response_type=code&client_id=").append(arrayList.get(0)).append("&redirect_uri=")
            .append(URLEncoder.encode(arrayList.get(2), StandardCharsets.UTF_8))
            .append("&scope=")
            .append(URLEncoder.encode(
                "user:read channel:read channel:write chat:write events:subscribe moderation:ban",
                StandardCharsets.UTF_8))
            .append("&code_challenge=").append(new String(challenge, StandardCharsets.US_ASCII))
            .append("&code_challenge_method=S256").append("&state=")
            .append(stateEncoded)
            .toString()))).build().toPromise();
      })
      .with(GET, "/callback/auth", request -> {
        try {
        var parameters = request.getQueryParameters();
        //System.out.println("lmao");
        //System.out.println(map.get(URLEncoder.encode(parameters.get("state"), StandardCharsets.US_ASCII)));
        //map.forEach((key, value) -> {
        //  System.out.printf("%s != %s ?\n", key, URLEncoder.encode(parameters.get("state"), StandardCharsets.US_ASCII));
        //  if(key.equals(URLEncoder.encode(parameters.get("state"), StandardCharsets.US_ASCII))) {
        //    System.out.println("أسلم!!!!!");
        //  }
        //});
        var body = new StringBuilder("code=").append(parameters.get("code")).append("&client_id=").append(URLEncoder.encode(arrayList.get(0), StandardCharsets.US_ASCII)).append("&client_secret=").append(URLEncoder.encode(arrayList.get(1), StandardCharsets.US_ASCII)).append("&redirect_uri=").append(URLEncoder.encode(arrayList.get(2), StandardCharsets.US_ASCII)).append("&grant_type=authorization_code&code_verifier=").append(map.remove(URLEncoder.encode(parameters.get("state"), StandardCharsets.US_ASCII)).verifier()).toString();
        //var response = client.request(io.activej.http.HttpRequest.builder(HttpMethod.POST, OauthURLs.TOKEN.url.concat('?' + body)).withHeader(io.activej.http.HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded").build());
        //var result = response.map(res -> new String(res.getBody().getArray(), StandardCharsets.UTF_8)).getResult();
        //var code = response.map(res -> res.getCode()).getResult();
        //System.out.println(result + ",\t" + code);
        var response = client.sendAsync(HttpRequest.newBuilder(URI.create(OauthURLs.TOKEN.url)).headers("Content-Type", "application/x-www-form-urlencoded").POST(BodyPublishers.ofString(body)).build(), BodyHandlers.ofString()).join();
        final String responseBody = response.body();
        System.out.println(responseBody);
        //System.out.println(result);
        } catch(Exception e) {
          System.err.printf("[\033[31mSEVERE\033[0m] could not send request; %s", e.getMessage());
      }
       return HttpResponse.redirect301("http://localhost:8080/authorize")/*.withCookie(HttpCookie.builder("kat").withValue("").withHttpOnly(true).withSecure(true).build())*/.withHtml("""
<!DOCTYPE html>
<html>
  <header>
    <title>Fetching token...</title>
  </header>
  <body>
    <div class="box"></div>
  </body>
  <style>
	:root {
		--text: #00e701;
		--background: #0b0e0f; 
		--border: #474f54
	}
  body {
    background-color: var(--background);
  }
  .box {
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
        """).toPromise();
    })
			.with(GET, "/*", request -> HttpResponse.ofCode(404)
				.withHtml(JStachio.render(new URLNotFound(request.getRelativePath()))).toPromise())
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
      .withListenPort(8080)
			.build();
	}

	@Override
	protected Module getModule() {
		return ServiceGraphModule.create();
	}

	@Override
	protected void run() throws Exception {
		logger0.info("HTTP Server is now available at http://localhost: 8080");
		awaitShutdown();
	}

	public static void main(String[] args) throws Exception {
		Launcher launcher = new App();
		launcher.launch(args);
	}
}
