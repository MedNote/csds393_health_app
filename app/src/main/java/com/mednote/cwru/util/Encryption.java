package com.mednote.cwru.util;

import android.util.Log;

import com.mednote.cwru.RecordByUuidQuery.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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
    public static byte[] symmetricEncrypt(Key key, String plaintext, String transformation) throws Exception {
        if(plaintext == null) {
            return null;
        }
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext.getBytes());
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
    public static byte[] symmetricDecrypt(Key key, byte[] ciphertext, String transformation) throws Exception {
        if(ciphertext == null) {
            return null;
        }
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ciphertext);
    }

    public static byte[] encrypt(Key key, String plaintext) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException, NoSuchProviderException {
        if(plaintext == null) {
            return null;
        }
        byte[] message = plaintext.getBytes("UTF-8");
        Cipher ecies = Cipher.getInstance("RSA");
        ecies.init(Cipher.ENCRYPT_MODE, key);
        return ecies.doFinal(message);
    }

    public static byte[] decrypt(Key key, byte[] ciphertext) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
        if(ciphertext == null) {
            return null;
        }
        Cipher ecies = Cipher.getInstance("RSA");
        ecies.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = ecies.doFinal(ciphertext);
        return decrypted;
    }

    public static Record_by_uuid encryptRecord(Record_by_uuid record, Key key, String transformation) throws Exception {
        String uuid = null;
        Name name = null;
        String dob = null;
        List<String> allergies = new ArrayList<String>();
        List<String> medications = new ArrayList<String>();
        List<Immunization> immunizations = new ArrayList<Immunization>();
        List<Visit_note> visit_notes = new ArrayList<Visit_note>();
        for (Field f : record.getClass().getDeclaredFields()) {
            String fieldName = f.getName();
            switch(fieldName) {
                case "uuid": {
                    uuid = Arrays.toString(Encryption.symmetricEncrypt(key, ((String) f.get(record)), transformation));
                    break;
                }
                case "name": {
                    Name temp = (Name) f.get(record);
                    String firstName = Arrays.toString(Encryption.symmetricEncrypt(key, temp.first_name, transformation));
                    String lastName = Arrays.toString(Encryption.symmetricEncrypt(key, temp.last_name, transformation));
                    name = new Name(firstName, lastName);
                    break;
                }
                case "dob": {
                    dob = Arrays.toString(Encryption.symmetricEncrypt(key, ((String) f.get(record)), transformation));
                    break;
                }
                case "allergies": {
                    allergies = new ArrayList<String>();
                    List<String> temp = (List<String>) f.get(record);
                    for (String str : temp) {
                        allergies.add(Arrays.toString(Encryption.symmetricEncrypt(key, str, transformation)));
                    }
                    break;
                }
                case "medications": {
                    medications = new ArrayList<String>();
                    List<String> temp = (List<String>) f.get(record);
                    for (String str : temp) {
                        medications.add(Arrays.toString(Encryption.symmetricEncrypt(key, str, transformation)));
                    }
                    break;
                }
                case "immunizations": {
                    List<Immunization> temp = (List<Immunization>) f.get(record);
                    for(Immunization imm : temp) {
                        immunizations.add(new Immunization(Arrays.toString(Encryption.symmetricEncrypt(key, imm.immunization, transformation)),
                                Arrays.toString(Encryption.symmetricEncrypt(key, (String) imm.date, transformation))));
                    }
                    break;
                }
                case "visit_notes": {
                    List<Visit_note> temp = (List<Visit_note>) f.get(record);
                    for(Visit_note vis : temp) {
                        visit_notes.add(new Visit_note(Arrays.toString(Encryption.symmetricEncrypt(key, vis.note, transformation)),
                                Arrays.toString(Encryption.symmetricEncrypt(key, (String) vis.date, transformation))));
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
        Log.i("Encryption", "UUID: " + uuid);
        return new Record_by_uuid(uuid, name, dob, allergies, medications, immunizations, visit_notes);
    }

    public static Record_by_uuid decryptRecord(Record_by_uuid record, Key key, String transformation) {
        return null;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * Parses a String into the byte array that it represents. It does NOT fetch the byte values
     * of the individual characters. For instance, the string "[6, -108, 112]" will result in a
     * byte array with first byte 6, not 91 (byte value of '[').
     *
     * @param str String representation of a byte array.
     * @return byte array
     */
    public static byte[] bytesFromString(String str) {
        String[] temp = str.substring(1, str.length() - 1).split(",");
        byte[] bytes = new byte[temp.length];
        for (int i = 0, len=bytes.length; i<len; i++) {
            bytes[i] = Byte.parseByte(temp[i].trim());
        }
        return bytes;
    }
}
