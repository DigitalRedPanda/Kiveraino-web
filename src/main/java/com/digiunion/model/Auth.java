package com.digiunion.model;

import io.jstach.jstache.JStache;

@JStache(path = "template/authorize_token.mustache")
public record Auth(String authToken, String refreshToken) {
}
