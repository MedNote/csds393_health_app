package com.mednote.cwru.util;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Created by jboggess on 9/28/16.
 */

public class SecureKeyGenerator {

    public static void main(String[] args) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            int flags = Base64.NO_WRAP | Base64.URL_SAFE;
            System.out.println(android.util.Base64.encodeToString(secretKey.getEncoded(), flags));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
