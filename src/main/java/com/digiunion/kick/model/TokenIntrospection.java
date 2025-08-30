package com.digiunion.kick.model;

import com.alibaba.fastjson2.annotation.JSONField;

public record TokenIntrospection(
    @JSONField(name = "data")
    TokenData data,
    @JSONField(name = "message")
    String message
) {
    public record TokenData(
        @JSONField(name = "active")
        boolean active,
        @JSONField(name = "client_id")
        String clientId,
        @JSONField(name = "exp")
        long expirationTime,
        @JSONField(name = "scope")
        String scope,
        @JSONField(name = "token_type")
        String tokenType
    ) {}
}
