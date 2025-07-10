package com.digiunion.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.digiunion.App;
import com.digiunion.Main;

import io.activej.bytebuf.ByteBuf;
import io.activej.bytebuf.ByteBufPool;
import io.activej.promise.Promise;

public interface ResourceService {
  // public static Promise<ByteBuf> readResource(String path) {
  //   try (final InputStream inputStream = ResourceService.class.getResourceAsStream(path)) {
  //     return Promise.of(ByteBuf.wrapForReading(.readAllBytes()));
  //   } catch (Exception e) {
  //     return Promise.ofException(e); 
  //   }
  // }
  //
  public static Promise<ByteBuf> readResource(String path) {
        return Promise.ofBlocking(App.EXECUTOR, () -> {
            try (InputStream is = ResourceService.class.getResourceAsStream(path)) {
            ByteBuf buf = ByteBufPool.allocate(8192);  // Pre-allocated buffer
            byte[] temp = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(temp)) != -1) {
                buf.put(temp, 0, bytesRead);  // Appends data
            }
            return buf;
            } catch(Exception e) {
                return null;
            }
        });

}
}

