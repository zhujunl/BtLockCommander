package com.miaxis.btlockcommander.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1Util {

    public static final String TEMPORARY_PASSWORD = "123456";

    private static byte[] eccrypt(String info, String shaType) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance(shaType);
        byte[] srcBytes = info.getBytes();
        // 使用srcBytes更新摘要
        sha.update(srcBytes);
        // 完成哈希计算，得到result
        byte[] resultBytes = sha.digest();
        return resultBytes;
    }

    private static String flip(String sha1Str) {
        String result = "";
        String[] groupArr = new String[5];
        for(int i=0 ; i< groupArr.length; i++){
            groupArr[i] = sha1Str.substring(i * 8 , i * 8 + 8);
        }
        for(int i=0; i< groupArr.length; i++){
            String item = groupArr[i];
            for(int j = 0; j < 4; j++){
                result += item.substring(6 - (j * 2),  8 - (j * 2));
            }
        }
        return result;
    }

    public static byte[] flip(byte[] sha1) {
        byte[] flip = new byte[sha1.length];
        for (int i = 0; i < sha1.length; i += 4) {
            byte[] cache = new byte[4];
            cache[3] = sha1[i];
            cache[2] = sha1[i + 1];
            cache[1] = sha1[i + 2];
            cache[0] = sha1[i + 3];
            System.arraycopy(cache, 0, flip, i, cache.length);
        }
        return flip;
    }

    public static byte[] eccryptSHA1(String info) {
        try {
            return eccrypt(info, "SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String hexString(byte[] bytes){
        StringBuilder hexValue = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int val = ((int) bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

}
