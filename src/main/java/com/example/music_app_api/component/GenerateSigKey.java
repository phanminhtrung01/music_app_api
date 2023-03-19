package com.example.music_app_api.component;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Component
public class GenerateSigKey {

    private static @NotNull String concatParaRequest(
            @NotNull Map<String, String> mapKey) {
        StringBuilder stringBuilder = new StringBuilder();

        mapKey.forEach((key, value) -> stringBuilder
                .append(key)
                .append("=")
                .append(value));

        return stringBuilder.toString();
    }

    private static @NotNull String getHash256(
            @NotNull String data)
            throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest
                .getInstance("SHA-256");
        final byte[] hash = digest
                .digest(data.getBytes(StandardCharsets.UTF_8));

        return String.format("%064x", new BigInteger(1, hash));
    }

    @Contract(value = "_, null, _ -> null; _, !null, _ -> !null")
    public static String getHmac512(
            @NotNull String key,
            String pathApi,
            Map<String, String> mapKey)
            throws NoSuchAlgorithmException,
            InvalidKeyException {

        final String concatString = concatParaRequest(mapKey);
        final String data = pathApi + getHash256(concatString);
        final Mac sha512Hmac = Mac.getInstance("HmacSHA512");
        final byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
        final SecretKeySpec keySpec =
                new SecretKeySpec(byteKey, "HmacSHA512");
        sha512Hmac.init(keySpec);
        final byte[] macData = sha512Hmac
                .doFinal(data.getBytes(StandardCharsets.UTF_8));

        return String.format("%128x", new BigInteger(1, macData));
    }
}
