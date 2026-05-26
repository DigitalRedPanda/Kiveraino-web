package com.digiunion.model;

import com.digiunion.kick.model.User;

import io.activej.http.IWebSocket;

public class Client{
  public String nickName;
  public String userName;
  public String hostName;
  public String realName;
  public IWebSocket webSocket;

  public Client(
    String nickName,
    String userName,
    String hostName,
    String realName,
    IWebSocket webSocket) {
    this.nickName = nickName;
    this.userName = userName;
    this.hostName = hostName;
    this.realName = realName;
    this.webSocket = webSocket;
  }

}
