package com.digiunion.env;

import java.util.concurrent.CopyOnWriteArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;


public interface Dotenv {
  public static CopyOnWriteArrayList<String> load(String path) {
    CopyOnWriteArrayList<String> arrayList = new CopyOnWriteArrayList<>();
    try(final InputStream inputStream = Dotenv.class.getResourceAsStream("creds/creds.env"); 
        final BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream))) {
      String content;
      while((content = buffer.readLine()) != null) {
        arrayList.add(content.split("=")[1]);
      }
    } catch(IOException e) {
      System.err.println("[\033[31mSEVERE\033[0m] could not read/process the file; " + e.getMessage());
    }
        return arrayList;
  };
}
