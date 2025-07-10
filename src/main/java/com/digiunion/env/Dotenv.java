package com.digiunion.env;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public interface Dotenv {
  public static CopyOnWriteArrayList<String> loadArrayList(String path) throws IOException {
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

  public static Map<String, String> loadMap(String path) throws IOException {
    final Map<String, String> map = new HashMap<>();
    try(final InputStream inputStream = Dotenv.class.getResourceAsStream(path);
      final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
  ) {
      String data;
      while((data = reader.readLine()) != null) {
        var delimiterIndex = data.indexOf("=");
        map.put(data.substring(0, delimiterIndex), data.substring(delimiterIndex + 1, data.length()));
        //System.out.println(data.substring(data.indexOf("=") + 1, data.length()));
      }
    } 
    return map;

  }

  public static String[] load(String path) throws IOException {
    final ArrayList<String> arrayList = new ArrayList<>();
    try(final InputStream inputStream = Dotenv.class.getResourceAsStream(path);
      final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
  ) {
      String data;
      while((data = reader.readLine()) != null) {
        arrayList.add(data.substring(data.indexOf("=") + 1, data.length()));
        //System.out.println(data.substring(data.indexOf("=") + 1, data.length()));
      }
    } 
    return arrayList.toArray(new String[arrayList.size()]);

  }

}
