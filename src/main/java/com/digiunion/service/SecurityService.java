package com.digiunion.service;

import java.security.interfaces.RSAPublicKey;

import io.activej.promise.Promise;

public interface SecurityService {
    public static Promise<RSAPublicKey> readX509PublicKey(byte[] publicKey) {

        return Promise.of(null);
    }
}
