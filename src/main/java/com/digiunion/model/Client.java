package com.digiunion.model;

import com.digiunion.kick.model.User;

import io.activej.http.IWebSocket;

public record Client(
    String nickName,
    String userName,
    String hostName,
    String realName,
    IWebSocket webSocket){}
