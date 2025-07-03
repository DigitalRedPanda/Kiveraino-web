package com.digiunion.database;

import io.valkey.JedisPool;
import io.valkey.Jedis;
import io.valkey.JedisPoolConfig;

import java.util.Optional;

import com.digiunion.model.PKCE;

public final class Database implements AutoCloseable{
  private static JedisPool jedisPool;

  public Database() {
    var config = new JedisPoolConfig();
    config.setMaxIdle(16);
    config.setMaxTotal(16);
    config.setMinIdle(8);
    jedisPool = new JedisPool(config);

  }

  public String getDelEntry(String state){
    try(final Jedis jedis = jedisPool.getResource()) {
      final String entry = jedis.getDel(state);
      if(entry != null)
        System.out.printf("[\033[34mINFO\033[0m] the entry %s has been retrieved\n", state);
      else 
        System.out.printf("[\033[34mINFO\033[0m] the entry %s has not been retrieved; %s\n", state, entry);
      return entry;
    }
  }

  public void setEntry(String state, PKCE pkce){
    try(final Jedis jedis = jedisPool.getResource()){
      final String entryState = jedis.setex(state, 900, pkce.verifier());
      if(entryState.equals("OK"))
        System.out.printf("[\033[34mINFO\033[0m] the entry (%s, %s) has been stored\n", state, pkce.verifier());
      else 
        System.out.printf("[\033[34mINFO\033[0m] the entry (%s, %s) has not been stored; %s\n", state, pkce.verifier(), entryState);

    }
  }

  @Override 
  public void close() {
    jedisPool.close();
  }
}
