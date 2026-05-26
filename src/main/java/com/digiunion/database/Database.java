package com.digiunion.database;

import io.valkey.JedisPool;
import io.valkey.Jedis;
import io.valkey.JedisPoolConfig;

import java.util.Optional;

import io.activej.http.WebSocket;
import io.activej.http.HttpHeaders;

import com.digiunion.model.PKCE;
import com.digiunion.util.StringUtils;

public final class Database implements AutoCloseable{
  private static JedisPool jedisPool;

  public Database() {
    var config = new JedisPoolConfig();
    config.setMaxIdle(16);
    config.setMaxTotal(16);
    config.setMinIdle(8);
    jedisPool = new JedisPool(config);

  }

  public PKCE getDelEntry(String state){
    try(final Jedis jedis = jedisPool.getResource()) {
      final String[] temp = StringUtils.split(jedis.getDel(state), ' ', 1);
      final PKCE entry = new PKCE(temp[0], temp[1]);
      // if(!entry.isEmpty())
      //   System.out.printf("[\033[34mINFO\033[0m] the entry %s has been retrieved\n", state);
      // else 
      //   System.out.printf("[\033[34mINFO\033[0m] the entry %s has not been retrieved; %s\n", state, entry);
      return entry;
    }
  }
  public void storeToken(long id,String token){
    try(final Jedis jedis = jedisPool.getResource()) {
      // if(!entry.isEmpty())
      //   System.out.printf("[\033[34mINFO\033[0m] the entry %s has been retrieved\n", state);
      // else 
      //   System.out.printf("[\033[34mINFO\033[0m] the entry %s has not been retrieved; %s\n", state, entry);
      System.out.printf("[\033[34mINFO\033[0m] the entry (%d, %s) has been stored\n", id, token);
      jedis.setex(Long.toString(id), 60,token);
    }
  }
  public void getToken(WebSocket webSocket){
    try(final Jedis jedis = jedisPool.getResource()) {
      // if(!entry.isEmpty())
      //   System.out.printf("[\033[34mINFO\033[0m] the entry %s has been retrieved\n", state);
      // else 
      //   System.out.printf("[\033[34mINFO\033[0m] the entry %s has not been retrieved; %s\n", state, entry);
      var id = webSocket.getResponse().getHeader(HttpHeaders.of("Sec-WebSocket-Accept"));
      System.out.printf("[\033[34mINFO\033[0m] the entry (%s, %s) has been retrieved\n", id, jedis.getDel(id));
    }
  }

  public PKCE getEntry(String state){
    try(final Jedis jedis = jedisPool.getResource()) {
      final String entryTemp = jedis.get(state);
      if(entryTemp != null) {
        final String[] temp = StringUtils.split(jedis.get(state),' ', 1);
        final PKCE entry = new PKCE(temp[0], temp[1]);
        return entry;
      } else {
        return new PKCE(null, null);
      }
      // if(!entry.isEmpty())
      //   System.out.printf("[\033[34mINFO\033[0m] the entry %s has been retrieved\n", state);
      // else 
      //   System.out.printf("[\033[34mINFO\033[0m] the entry %s has not been retrieved; %s\n", state, entry);
    }
  } 

  public void setEntry(String state, PKCE pkce){
    try(final Jedis jedis = jedisPool.getResource()){
      final String entryState = jedis.setex(state, 900, pkce.toString());
      // if(entryState.equals("OK"))
      //   System.out.printf("[\033[34mINFO\033[0m] the entry (%s, %s) has been stored\n", state, pkce.verifier());
      // else 
      //   System.out.printf("[\033[34mINFO\033[0m] the entry (%s, %s) has not been stored; %s\n", state, pkce.verifier(), entryState);
    }
  }

  @Override 
  public void close() {
    jedisPool.close();
  }
}
