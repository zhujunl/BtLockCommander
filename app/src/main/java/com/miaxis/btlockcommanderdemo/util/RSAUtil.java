package com.miaxis.btlockcommanderdemo.util;

import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtil {

    private static final String SERVER_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCat" +
            "jbJ9aqWsIgF+4lYkXX0JvVAzmF3cCKewqybI8d68LfJXxaa2X1GdHI8ZcaEqZyfnUQmDNjKGFXx" +
            "bMPi1rt9iI8oZrO8Nucu/UkAE2rSI1Gmsiu2IqqSZehtIWQv65DshZVvp6oJ73BA3wBv2kpemWiQ" +
            "xLpVQnYFKNYekxiu8QIDAQAB";

    public static String getHostCertificate(String hostCertificate) throws Exception {
        return encrypt(hostCertificate + "-" + System.currentTimeMillis());
    }

    private static String encrypt(String key) throws Exception {
        byte[] encrypt = publicEncrypt(key.getBytes(StandardCharsets.UTF_8), RSAUtil.string2PublicKey(SERVER_PUBLIC_KEY));
        return RSAUtil.byte2Base64(encrypt);
    }

    private static byte[] publicEncrypt(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(content);
        return bytes;
    }

    private static String byte2Base64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private static byte[] base642Byte(String base64String) throws IOException {
        return Base64.decode(base64String, Base64.NO_WRAP);
    }

    private static PublicKey string2PublicKey(String pubStr) throws Exception {
        byte[] keyBytes = base642Byte(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static String encryptPassword(String username, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.update(password.getBytes(StandardCharsets.UTF_8));
        byte[] hash = digest.digest(username.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
