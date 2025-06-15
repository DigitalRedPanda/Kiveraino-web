package com.digiunion.keygen;

import java.security.KeyStore;
import java.security.KeyStoreException;

public interface KeyGenerator {
	public static String generateKey() throws KeyStoreException {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");

	}
}
