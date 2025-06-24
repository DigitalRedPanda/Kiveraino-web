package com.digiunion.util;

import java.util.ArrayList;

public interface StringUtils {

  public static String[] split(String string, char delimiter) {
    final char[] temp = string.toCharArray();
    var stringArray = new ArrayList<String>();
    var currentI = 0;
    for(var i = 0; i < string.length(); i++) {
      //System.out.printf("i=%d, currentI=%d\n", i, currentI);
      if(temp[i] == delimiter) {
        stringArray.add(string.substring(currentI, i));
        currentI = i + 1;
      }
    }
    if(!stringArray.isEmpty())
      stringArray.add(string.substring(currentI, string.length()));
    return (String[]) stringArray.toArray(new String[0]);
  }

  public static String[] split(String string, char delimiter, int limit) {
    final char[] temp = string.toCharArray();
    var stringArray = new String[limit+1];
    var count = 0;
    var currentI = 0;
    for(var i = 0; i < temp.length && count < limit; i++) {
      if(temp[i] == delimiter) {
        stringArray[count] = string.substring(currentI, i);
        currentI = i + 1;
        count += 1;
      }
    }
    if(count > 0)
      stringArray[count] = string.substring(currentI, string.length());
    return stringArray;

  }

}
