package com.digiunion.kick.model;

import com.alibaba.fastjson2.annotation.JSONField;

public record Credentials(@JSONField(name = "access_token") String accessToken, @JSONField(name = "expires_in") int expiresIn, @JSONField(name = "refresh_token") String refreshToken,@JSONField(name = "scope") String scope, @JSONField(name = "token_type")String tokenType) {}
