package com.digiunion.kick.model;

import com.alibaba.fastjson2.annotation.JSONField;

public record Token(
    @JSONField(name = "access_token")
    String accessToken,
    @JSONField(name = "token_type")
    String tokenType,
    @JSONField(name = "refresh_token")
    String refreshToken,
    @JSONField(name = "expires_in")
    long expiresIn,
    @JSONField (name = "scope")
    String scope
) {}
