package com.digiunion.env;

import java.util.concurrent.CopyOnWriteArrayList;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public interface Dotenv {
  public static CopyOnWriteArrayList<String> load(String path) throws IOException {
    final CopyOnWriteArrayList<String> arrayList = new CopyOnWriteArrayList<>();
    try(final InputStream inputStream = Dotenv.class.getResourceAsStream(path);
      final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
  ) {
      String data;
      while((data = reader.readLine()) != null) {
        arrayList.add(data.substring(data.indexOf("=") + 1, data.length()));
        //System.out.println(data.substring(data.indexOf("=") + 1, data.length()));
      }
    } 
    return arrayList;
  }

}
