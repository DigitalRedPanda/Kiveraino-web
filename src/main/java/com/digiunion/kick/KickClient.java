package com.digiunion.kick;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.CookieHandler;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.Collections;
import java.util.List;



import javax.net.ssl.SSLContext;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.digiunion.App;
import com.digiunion.kick.model.Channel;
import com.digiunion.kick.model.TokenIntrospection;
import com.digiunion.kick.model.Token;
import com.digiunion.kick.model.User;
import com.digiunion.kick.model.UserData;
import com.digiunion.kick.model.SubscriptionResponse;

public class KickClient {

  private final HttpClient httpClient;
  
  private final String[] creds;
  public KickClient(String[] creds) throws NoSuchAlgorithmException {
    if (creds == null || creds.length < 7 || creds[6] == null || creds[6].isBlank()) {
      throw new IllegalArgumentException("Invalid credentials array - token missing");
    }
    httpClient = HttpClient.newBuilder()
      .executor(App.EXECUTOR)
      .sslContext(SSLContext.getDefault())    
      .cookieHandler(new CookieHandler() {
        @Override
        public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) {
            return Collections.emptyMap(); // No cookies sent
        }
        
        @Override
        public void put(URI uri, Map<String, List<String>> responseHeaders) {
            // Ignore all cookies received
        }
      })
      .build();
    this.creds = creds;
  }
  /**
   * validates the kick api token and returns a {@link TokenIntrospection} 
   * @return CompletableFuture<TokenIntrospection>
   */

  public CompletableFuture<TokenIntrospection> validateToken(String token){
    if(token != null)
      return httpClient.sendAsync(java.net.http.HttpRequest.newBuilder(URI.create(OauthURLs.INTROSPECT_TOKEN.url)).POST(BodyPublishers.noBody()).header("Authorization", new StringBuilder("Bearer ").append(token).toString()).build(), BodyHandlers.ofString())
      .thenApply(response -> JSON.parseObject(response.body(), TokenIntrospection.class));
    else 
      return CompletableFuture.failedFuture(new NullPointerException(""));
    
  }

  public CompletableFuture<Token> refreshToken(String token) {
    if(token != null)
      return httpClient.sendAsync(java.net.http.HttpRequest.newBuilder(URI.create(new StringBuilder(OauthURLs.TOKEN.url).append("?refresh_token=").append(token).append("&client_id=").append(App.arrayList[0]).append("&client_secret=").append(App.arrayList[1]).append("&grant_type=refresh_token").toString())).POST(BodyPublishers.noBody()).build(), BodyHandlers.ofString())
      .thenApply(response -> JSON.parseObject(response.body(), Token.class));
    else 
      return CompletableFuture.failedFuture(new NullPointerException("you know you cannot refresh a null token, right?"));

  }

  /**
   * fetches a {@link User} by token 
   * @return CompletableFuture<User>
   */
  public CompletableFuture<UserData> getUserByToken(String token){
    if(token != null)
      return httpClient.sendAsync(java.net.http.HttpRequest.newBuilder(URI.create(APIURLs.USERS.url)).GET().header("Authorization", new StringBuilder("Bearer ").append(token).toString()).build(), BodyHandlers.ofString())
      .thenApply(response -> {
        //System.out.println(response);
        return JSON.parseObject(response.body(), UserData.class);
    });
    else 
      return CompletableFuture.failedFuture(new NullPointerException(""));
    
  }


  /**
   * Fetches a {@link Channel} by login 
   * @return CompletableFuture<Channel>
   */
  public CompletableFuture<Channel> getChannelByLogin(String slug) {
    if(!(slug == null || slug.isBlank())) {
      //System.out.printf("[\033[34mINFO\033[0m] channel slug: %s\n", slug);
      //System.out.printf("[\033[34mINFO\033[0m] t: %s\n", creds[6]);
      return httpClient.sendAsync(HttpRequest.newBuilder(URI.create(new StringBuilder(APIURLs.CHANNELS.url).append("?slug=").append(slug).toString())).headers("Authorization", "Bearer " + creds[6], "Content-Type", "application/x-www-form-urlencoded", "Accept", "application/json", "User-Agent", "Mozilla/5.0 ( compatible ) ").GET().build(), BodyHandlers.ofString()).thenApply(ah -> {
    var bodyy = ah.body();
    var parsed = JSON.parseObject(bodyy)
      .getJSONArray("data")
      .getJSONObject(0);
    return JSON.parseObject(parsed.toJSONString(), Channel.class); 

      }).exceptionally(throwable -> {
        System.err.printf("[\033[31mERROR\033[0m] Channel not found; mistaken? : %s%n", 
        throwable.getMessage());
        return new Channel(null,0,null,null,null,null,null);
      });
    } else {
      System.out.printf("[\033[31mERROR\033[0m] channel slug: %s\n", slug);
      return CompletableFuture.failedFuture(new IllegalArgumentException("you should at least pass an argument or something"));
    }
  }
  /**
   * Fetches a {@link Channel} by id 
   * @return CompletableFuture<Channel>
   */
  public CompletableFuture<Channel> getChannelById(String id) throws InterruptedException, IOException{
    if(!(id == null || id.isBlank())) {
      return httpClient.sendAsync(HttpRequest.newBuilder(URI.create(APIURLs.CHANNELS.url)).headers("Content-Type","application/x-www-form-urlencoded", "Authorization", creds[6]).POST(BodyPublishers.ofString(new StringBuilder("id=").append(id).toString())).build(), BodyHandlers.ofString()).thenApply(response ->  JSON.parseObject(response.body(), Channel.class));
    } else {
      return CompletableFuture.failedFuture(new IllegalArgumentException("you should at least pass an argument or something"));
    }
  }
  /**
   * Fetches a {@link Channel} by login 
   * @return CompletableFuture<Channel>
   */
  public CompletableFuture<HttpResponse<String>> postEventSubscriptions(String id) {
    if(!(id == null || id.isBlank())) {
      return httpClient.sendAsync(HttpRequest.newBuilder(URI.create(APIURLs.SUBSCRIPTIONS.url)).headers("Content-Type","application/json", "Authorization", "Bearer " + creds[6]).POST(BodyPublishers.ofString(new StringBuilder("{\"broadcaster_user_id\":").append(id).append(",\"events\": [{\"name\": \"chat.message.sent\",\"version\": 1}, {\"name\": \"channel.followed\",\"version\": 1}, {\"name\": \"channel.subscription.renewal\",\"version\": 1}, {\"name\": \"channel.subscription.gifts\",\"version\": 1}, {\"name\": \"channel.subscription.new\",\"version\": 1}, {\"name\": \"livestream.status.updated\",\"version\": 1}, {\"name\": \"livestream.metadata.updated\",\"version\": 1}, {\"name\": \"moderation.banned\",\"version\": 1}, {\"name\": \"kicks.gifted\",\"version\": 1}],\"method\": \"webhook\"}").toString())).build(), BodyHandlers.ofString());
    } else {
      return CompletableFuture.failedFuture(new IllegalArgumentException("you should at least pass an argument or something"));
    }
  }

    public CompletableFuture<HttpResponse<String>> deleteEventSubscriptions(String id) {
    if(!(id == null || id.isBlank())) {
      return httpClient.sendAsync(HttpRequest.newBuilder(URI.create(new StringBuilder(APIURLs.SUBSCRIPTIONS.url).append("?id=").append(id).toString())).headers("Content-Type","application/x-www-form-urlencoded", "Authorization", "Bearer " + creds[6]).DELETE().build(), BodyHandlers.ofString()).thenApply(response -> {
        //System.out.printf("[\033[34mINFO\033[0m] deletio body: %s, status: %d\n", response.body(), response.statusCode());
        return response;
      });
    } else {
      return CompletableFuture.failedFuture(new IllegalArgumentException("you should at least pass an argument or something"));
    }
  }
    public CompletableFuture<HttpResponse<String>> getSubscriptionsFromBroadcaster(String broadcaster_id) {
    if(!(broadcaster_id == null || broadcaster_id.isBlank())) {
      return httpClient.sendAsync(HttpRequest.newBuilder(URI.create(new StringBuilder(APIURLs.SUBSCRIPTIONS.url).append("?broadcaster_user_id=").append(broadcaster_id).toString())).headers("Content-Type","application/x-www-form-urlencoded", "Authorization", "Bearer " + creds[6]).GET().build(), BodyHandlers.ofString()).thenApply(res -> {
          System.out.printf("[\033[34mINFO\033[0m] { \nbody: %s\nstatus: %d\nurl: %s\n}\n", res.body(), res.statusCode(), res.uri().toASCIIString());
        return res;
      });
    } else {
      return CompletableFuture.failedFuture(new IllegalArgumentException("you should at least pass an argument or something"));
    }

    }
  public CompletableFuture<HttpResponse<String>> deleteAllSubscriptionsFromBroadcaster(String broadcaster_id) {
    if(!(broadcaster_id == null || broadcaster_id.isBlank())) {
      return getSubscriptionsFromBroadcaster(broadcaster_id).thenApply(response -> {
        //System.out.printf("[\033[34mINFO\033[0m] body: %s\n", response.body());
        var jsonArray = JSON.parseObject(response.body()).getJSONArray("data");
        if(jsonArray.size() > 0) {
          var sb = new StringBuilder("?");
          JSONObject jsonObj ; 
          for(int i = 0; i < jsonArray.size()-1; i++) {
            jsonObj = jsonArray.getJSONObject(i); 

            if(jsonObj.containsKey("id")) {
              var id = jsonObj.getString("id");
              sb.append("id=")
                .append(id)
                .append('&');
            }
          }
          jsonObj = jsonArray.getJSONObject(jsonArray.size()-1); 

          if(jsonObj.containsKey("id")) {
            var id = jsonObj.getString("id");
            sb.append("id=")
              .append(id);
          }
          return sb;
        } else {
          return new StringBuilder();
        }
        // var jsonObj = jsonArray.getJSONObject(0);
        // while(jsonObj != null) {
        //   final String id = jsonObj.getString("id");
        //   sb.append("id=")
        //     .append(id);
        //   jsonObj = jsonArray.getJSONObject(++idx);
        //   if(jsonObj != null) {
        //     sb.append('&');
        //   }
        // }
      }).thenCompose(ids -> {
        if(!ids.isEmpty() && ids.length() > 2) {        
          System.out.printf("[\033[34mINFO\033[0m] full url %s\n", APIURLs.SUBSCRIPTIONS.url + ids);
        return httpClient.sendAsync(HttpRequest.newBuilder(URI.create(new StringBuilder(APIURLs.SUBSCRIPTIONS.url).append(ids).toString())).headers("Content-Type","application/x-www-form-urlencoded", "Authorization", "Bearer " + creds[6]).DELETE().build(), BodyHandlers.ofString()).thenApply(response -> {
          System.out.printf("[\033[34mINFO\033[0m] deletio body: %s, status: %d\n", response.body(), response.statusCode());
          return response; 
        });
        } else {
          return CompletableFuture.completedStage(null);
        }
      });
    } else {
      return CompletableFuture.failedFuture(new IllegalArgumentException("you should at least pass an argument or something"));
    }

  }

}


