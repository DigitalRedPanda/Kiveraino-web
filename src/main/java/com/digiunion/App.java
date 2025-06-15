package com.digiunion;

import io.activej.launchers.http.HttpServerLauncher;
import io.activej.launcher.Launcher;
import io.activej.inject.annotation.Provides;
import io.activej.inject.annotation.Named;
import io.activej.inject.annotation.Eager;
import io.activej.http.HttpResponse;
import io.activej.http.AsyncServlet;
import io.activej.http.RoutingServlet;
import io.activej.http.HttpServer;
import io.activej.reactor.Reactor;
import io.activej.inject.Injector;
import io.activej.inject.module.ModuleBuilder;
import io.activej.inject.module.Module;
import io.activej.service.ServiceGraphModule;
import io.activej.inject.module.AbstractModule;
import io.activej.reactor.nio.NioReactor;
import static io.activej.http.HttpMethod.GET;
import static io.activej.http.HttpMethod.POST;
import io.activej.eventloop.Eventloop;
import io.activej.http.HttpClient;
import io.activej.http.HttpRequest;
import io.activej.http.HttpHeaders;
import io.activej.http.IHttpClient;
import io.activej.dns.IDnsClient;
import io.activej.dns.DnsClient;
import io.activej.config.Config;

import java.security.SecureRandom;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLContextSpi;
import javax.net.ssl.KeyManagerFactory;
import java.util.Base64;
import java.net.URI;
import java.net.URLEncoder;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.InputStream;
import io.jstach.jstachio.JStachio;

import com.digiunion.env.Dotenv;
import com.digiunion.model.URL;
import com.digiunion.model.URLNotFound;
import com.digiunion.model.Credentials;

/**
 * Hello world!
 */
public final class App extends Launcher {
	
  public static final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
  public static final CopyOnWriteArrayList<String> arrayList = Dotenv.load("creds/creds.env");

	@Provides
	NioReactor reactor() {
		return Eventloop.create();
	}

  @Provides
  IDnsClient dnsClient(NioReactor reactor, Config config) throws UnknownHostException {
  return DnsClient.create(reactor, InetAddress.getLocalHost());
}
  @Provides
  Config config() {
    return Config.create()
      .with("dns.address", "8.8.8.8")
      .with("dns.timeout", "5 seconds")
      .overrideWith(Config.ofSystemProperties("config"));
  }
  @Provides
  IHttpClient client(NioReactor reactor, IDnsClient dnsClient) {
    return HttpClient.create(reactor, dnsClient);
  }

	@Provides
	AsyncServlet servlet(Reactor reactor, HttpClient client) throws NoSuchAlgorithmException {
	  return RoutingServlet.builder(reactor).with(GET, "/authorize",
				request -> {
          final SecureRandom secureRandom = new SecureRandom();
          var state = new byte[64];
          var codeVerifier = new byte[64];
          secureRandom.nextBytes(codeVerifier);
          secureRandom.nextBytes(state);
          final String verifier = Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
          final byte[] challenge = Base64.getUrlEncoder()
              .withoutPadding()
              .encode(MessageDigest.getInstance("SHA-256")
                  .digest(verifier.getBytes(StandardCharsets.US_ASCII)));
          var builder = new StringBuilder();
          final URL uri = new URL(URI.create(builder.append(
              "https://id.kick.com/oauth/authorize?response_type=code&client_id=01JWSQDDS511NH61T75TB4V89M&redirect_uri=")
              .append(URLEncoder.encode("http://localhost:8080/token", StandardCharsets.UTF_8))
              .append("&scope=")
              .append(URLEncoder.encode(
                  "user:read channel:read channel:write chat:write events:subscribe moderation:ban",
                  StandardCharsets.UTF_8))
              .append("&code_challenge=").append(new String(challenge, StandardCharsets.US_ASCII))
              .append("&code_challenge_method=S256").append("&state=")
              .append(URLEncoder.encode(new String(state, StandardCharsets.US_ASCII), StandardCharsets.UTF_8))
              .toString()).toString());


              return HttpResponse.ok200().withHtml(JStachio.render(uri)).build().toPromise(); 
      })
        .with(GET, "/token", request -> {
        var parameters = request.getQueryParameters();
        client.request(HttpRequest.post("https://id.kick.com/oauth/token").withHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded").withBody(new StringBuilder("{\"code\": \"").append(parameters.get("code")).append("\",\"client_id\": \"").append(arrayList.get(0)).append("\", \"client_secret\":\"").append(arrayList.get(1)).append("\", \"redirect_uri\": \"").append(arrayList.get(2)).append("\",\"grant_type\": \"Authorization Code\", \"").append("}").toString()).build());
        return HttpResponse.ok200().withHtml(JStachio.render(new Credentials(parameters.get("code"), parameters.get("state")))).build().toPromise(); })
				.with(GET, "/*", request -> HttpResponse.ofCode(404)
						.withHtml(JStachio.render(new URLNotFound(request.getRelativePath()))).toPromise())
				.build();

	}

	//@Provides
	//SSLContext sslContext() throws Exception {
	//	// Load your keystore (PKCS12 format recommended)
	//	KeyStore keyStore = KeyStore.getInstance("PKCS12");
	//	try (InputStream is = Files.newInputStream(Paths.get("keystore.p12"))) {
	//		keyStore.load(is, "password".toCharArray());
	//	}
	//
	//	// Initialize with TLS 1.3
	//	KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	//	kmf.init(keyStore, "password".toCharArray());
	//
	//	SSLContext sslContext = SSLContext.getInstance("TLSv1.3"); // Explicit TLS 1.3
	//	sslContext.init(kmf.getKeyManagers(), null, null);
	//	return sslContext;
	//}
	//
	@Provides
	@Eager
	HttpServer server(NioReactor reactor, AsyncServlet servlet) throws NoSuchAlgorithmException {
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
		logger.info("HTTP Server is now available at http://localhost: 8080");
		awaitShutdown();
	}

	public static void main(String[] args) throws Exception {
		Launcher launcher = new App();
		launcher.launch(args);
	}
}
