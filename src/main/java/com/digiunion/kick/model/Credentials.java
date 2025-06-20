package com.digiunion.kick.model;

import org.simdjson.annotations.JsonFieldName;

public record Credentials( @JsonFieldName("access_token") String accessToken, @JsonFieldName("expires_in") int expiresIn, @JsonFieldName("refresh_token") String refreshToken, @JsonFieldName("scopes") String scopes, @JsonFieldName("token_type") String tokenType) {}
