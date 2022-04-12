package com.mednote.cwru;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.mednote.cwru.util.Encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.MGF1ParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.xml.bind.DatatypeConverter;

@RunWith(AndroidJUnit4.class)
public class EncryptionTest {

    @Test
    public void testSymmetricEncrypt() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        //symmetric key encryption
        SecretKey symmKey = Encryption.createSymmetricKey("AES");
        Log.i("Encryption", "Symmetric key: " + DatatypeConverter.printHexBinary(symmKey.getEncoded()));
        IvParameterSpec ivParameterSpec = Encryption.generateIv();
        String ciphertext = Encryption.symmetricEncrypt(symmKey, "Hello, world!", "AES/CBC/PKCS5Padding", ivParameterSpec);
        Log.i("Encryption", "Ciphertext: " + ciphertext);
        String plaintext = Encryption.symmetricDecrypt(symmKey, ciphertext, "AES/CBC/PKCS5Padding", ivParameterSpec);
        Log.i("Encryption", "Decrypted plaintext: " + plaintext);
    }

    @Test
    public void testPublicPrivateEncrypt() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, BadPaddingException, InvalidKeyException, NoSuchProviderException {
        Key[] keys = Encryption.getKeys();
        OAEPParameterSpec spec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
        byte[] temp = Encryption.encrypt(keys[0], "Hello, world!", spec);
        Log.i("Encryption", "Decrypted plaintext: " + new String(Encryption.decrypt(keys[1], temp, spec)));
    }
}
