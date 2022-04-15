package com.mednote.cwru.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.IESParameterSpec;

public class Encryption {


    /*
     * Generates a public/private key pair
     * @param	algorithm	algorithm for key generation
     * @param 	keysize 	size of the key
     * @return 	size 2 array where first entry is public key and second private
     * @throws 	NoSuchAlgorithmException 	thrown if an improper algorithm string is passed
     */
    public static Key[] getKeys() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        RSAKeyGenParameterSpec ecSpec = new RSAKeyGenParameterSpec(2048,  new BigInteger(String.valueOf(65537)));
        KeyPairGenerator kf = KeyPairGenerator.getInstance("RSA");
        kf.initialize(ecSpec);
        KeyPair keyPair = kf.generateKeyPair();
        PrivateKey priv = keyPair.getPrivate();
        PublicKey pub = keyPair.getPublic();
        return new Key[] {pub, priv};
    }

    public static SecretKey createSymmetricKey(String algorithm) throws NoSuchAlgorithmException {
        SecureRandom securerandom = new SecureRandom();
        KeyGenerator keygenerator = KeyGenerator.getInstance(algorithm);
        keygenerator.init(256, securerandom);
        SecretKey key = keygenerator.generateKey();
        return key;
    }

    /*
     * Encrypts plaintext
     * @param	pubKey	key for encryption
     * @param	plaintext	text to encrypt
     * @param	transformation	string of the form "algorithm/mode/padding"
     * @return	ciphertext in UTF-8 encoding
     * @throws  Any one of:
     * 				NoSuchAlgorithmException,
     * 				NoSuchPaddingException,
     * 				InvalidKeyException,
     * 				IllegalBlockSizeException,
     * 				BadPaddingException,
     * 				UnsupportedEncodingException
     */
    public static String symmetricEncrypt(Key key, String plaintext, String transformation, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedByte = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encryptedByte);
    }


    /*
     * Decrypts ciphertext
     * @param	privKey	key for decryption
     * @param	ciphertext	text to decrypt
     * @param	transformation	string of the form "algorithm/mode/padding"
     * @return	plaintext
     * @throws	Any one of:
     * 				NoSuchAlgorithmException,
     * 				NoSuchPaddingException,
     * 				InvalidKeyException,
     * 				IllegalBlockSizeException,
     * 				BadPaddingException
     */
    public static String symmetricDecrypt(Key key, String ciphertext, String transformation, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decryptedByte = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decryptedByte);
    }

    public static byte[] encrypt(Key key, String plaintext, OAEPParameterSpec spec) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException, NoSuchProviderException {
        byte[] message = plaintext.getBytes("UTF-8");
        Cipher ecies = Cipher.getInstance("RSA");
        ecies.init(Cipher.ENCRYPT_MODE, key, spec);
        return ecies.doFinal(message);
    }

    public static byte[] decrypt(Key key, byte[] ciphertext, OAEPParameterSpec spec) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
        Cipher ecies = Cipher.getInstance("RSA");
        ecies.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decrypted = ecies.doFinal(ciphertext);
        return decrypted;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}
