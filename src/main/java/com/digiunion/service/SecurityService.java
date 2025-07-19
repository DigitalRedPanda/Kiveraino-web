package com.digiunion.service;

import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import com.digiunion.App;

import io.activej.bytebuf.ByteBuf;
import io.activej.promise.Promise;
//import jdk.incubator.vector.*;

public final class SecurityService {

    private static final String PEM_HEADER = "-----BEGIN PUBLIC KEY-----";

    private static final String PEM_FOOTER = "-----END PUBLIC KEY-----";

    //private static final VectorSpecies<Byte> SPECIES = ByteVector.SPECIES_PREFERRED;

    // public static void readX509PublicKeyy(final String publicKey) {
    //     final int headerIndex = publicKey.indexOf(PEM_BEGIN) + PEM_BEGIN.length();
    //     final int footerIndex = publicKey.indexOf(PEM_FOOTER, headerIndex);
    //     var temp = replace(publicKey.substring(headerIndex, footerIndex), '\n', '\0');
    //     System.out.println(temp);
    // }

 public Promise<RSAPublicKey> readX509PublicKey(final ByteBuf publicKey) {
        try {
            String pubKey = publicKey.asString(StandardCharsets.UTF_8);
            final int headerIndex = pubKey.indexOf(PEM_HEADER);
            final int footerIndex = pubKey.indexOf(PEM_FOOTER);
            pubKey = pubKey.replace(PEM_HEADER, "")
                .replace(PEM_FOOTER, "")
                .replaceAll("[\n\r]", "");
;
            final byte[] decodedPub = Base64.getDecoder().decode(pubKey);
            return Promise.of((RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decodedPub)));
        } catch(Exception e) {
            return Promise.ofException(new InvalidKeySpecException("Key parsing failed", e));
        }
    }

    public Promise<Boolean> verify(RSAPublicKey publicKey, String messageId, 
                                 String timestamp, String body, String signatureHeader) {
        return Promise.ofBlocking(App.EXECUTOR, () -> {
            try {
                String signedPayload = String.format("%s.%s.%s",messageId, timestamp, body);
                byte[] payloadBytes = signedPayload.getBytes(StandardCharsets.UTF_8);
                
                byte[] signatureBytes = Base64.getDecoder().decode(signatureHeader);
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashed = digest.digest(payloadBytes);
                Signature verifier = Signature.getInstance("SHA256withRSA");
                verifier.initVerify(publicKey);
                System.out.printf("""
public key: %s

hashedSignature: %s

signature: %s

                        """, publicKey.toString(), new String(hashed, StandardCharsets.UTF_8), new String(signatureBytes, StandardCharsets.UTF_8));
                verifier.update(hashed);
                return verifier.verify(signatureBytes);
            } catch (Exception e) {
                throw new RuntimeException("Verification failed", e);
            }
        });
    }    public int indexOf(byte[] first, byte[] second, int index) {
        var beginningIndex = index;
        for (int i = beginningIndex; i < first.length; i++) {
            boolean found = true;
            for (int j = 0; j < second.length; j++) {
                if (first[i+j] != second[j]) {
                    found = false;
                    break;
                    //System.out.printf("اه\t%c\t%d\t%d\n", (char) first[i+j], (j+i)-beginningIndex, beginningIndex);
                    // if(((j+i) - beginningIndex) == (second.length-1)) {
                    //     return beginningIndex;
                    // }
                }
            }
            if(found) return i;
        }         
        return -1;
    }


    // public int indexOfSimd(byte[] outerArray, byte[] smallerArray) {
    //     // Edge case handling
    //     if (smallerArray.length == 0) return 0;
    //     if (outerArray.length < smallerArray.length) return -1;
    //
    //     final int vectorWidth = SPECIES.length();
    //     final byte firstByte = smallerArray[0];
    //     final int maxOffset = outerArray.length - smallerArray.length;
    //
    //     // First phase: Find potential matches using SIMD
    //     for (int i = 0; i <= maxOffset; i += vectorWidth) {
    //         ByteVector chunk = ByteVector.fromArray(SPECIES, outerArray, i);
    //         var matchMask = chunk.eq(firstByte);
    //
    //         // Check each potential match position
    //         for (int j = 0; j < vectorWidth; j++) {
    //             if (matchMask.laneIsSet(j)) {
    //                 int pos = i + j;
    //                 if (pos > maxOffset) continue;
    //
    //                 // Second phase: Verify full match
    //                 if (verifyMatch(outerArray, smallerArray, pos)) {
    //                     return pos;
    //                 }
    //             }
    //         }
    //     }
    //     return -1;
    // }
    private boolean verifyMatch(byte[] outer, byte[] smaller, int offset) {
        // Manual verification for remaining bytes
        for (int i = 1; i < smaller.length; i++) {
            if (outer[offset + i] != smaller[i]) {
                return false;
            }
        }
        return true;
    }

    public int indexOfOpt(byte[] source, byte[] target) {
        // Extreme performance shortcut
        if (target.length == 0) return 0;
        if (source.length < target.length) return -1;

        byte first = target[0];
        int max = source.length - target.length;

        // Main loop with manual unrolling
        for (int i = 0; i <= max; i++) {
            // Fast-fail first byte check
            if (source[i] != first) {
                while (++i <= max && source[i] != first);
                if (i > max) return -1;
            }

            // Check remaining bytes
            int j = 1;
            int end = i + target.length;
            while (j < target.length && source[i + j] == target[j]) {
                j++;
            }

            if (j == target.length) {
                return i;
            }
        }
        return -1;
    }

    public static String replace(String string, char found, char replacement) {
        var temp = string.toCharArray();
        for (int i = 0; i < temp.length; i++) {
            if(temp[i] == found)
                temp[i] = replacement;
        }
        return new String(temp);
    }

}
