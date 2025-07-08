package com.digiunion.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import io.activej.bytebuf.ByteBuf;
import io.activej.promise.Promise;

public interface ResourceService {
  public static Promise<byte[]> readResource(String path) {
    try (final InputStream inputStream = ResourceService.class.getResourceAsStream(path);
        /*           var bufferedReader = new BufferedReader(new InputStreamReader(inputStream)) */
        ) {

      // String line;
      // StringBuilder builder = new StringBuilder();
      // while((line = bufferedReader.readLine()) != null){
      //    builder.append(line);
      // }
      return Promise.of(inputStream.readAllBytes());
    } catch (Exception e) {
      return Promise.ofException(e); 
    }
  }



}
