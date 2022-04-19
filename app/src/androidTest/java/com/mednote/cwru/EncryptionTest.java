package com.mednote.cwru;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.rx2.Rx2Apollo;
import com.google.gson.Gson;
import com.mednote.cwru.ethereum.ContractInteraction;
import com.mednote.cwru.util.Encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.xml.bind.DatatypeConverter;

import com.mednote.cwru.RecordByUuidQuery.*;
import com.mednote.cwru.util.EncryptionConstants;

import junit.framework.AssertionFailedError;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;

@RunWith(AndroidJUnit4.class)
public class EncryptionTest {

    @Test
    public void testSymmetricEncrypt() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        //symmetric key encryption
        SecretKey symmKey = Encryption.createSymmetricKey(EncryptionConstants.algorithm);
        Log.i("Encryption", "Symmetric key: " + DatatypeConverter.printHexBinary(symmKey.getEncoded()));
        byte[] ciphertext = Encryption.symmetricEncrypt(symmKey, "Hello, world!", EncryptionConstants.algorithm);
        Log.i("Encryption", "Ciphertext: " + new String(ciphertext));
        String plaintext = new String(Encryption.symmetricDecrypt(symmKey, ciphertext, EncryptionConstants.algorithm));
        Log.i("Encryption", "Decrypted plaintext: " + plaintext);
    }

    @Test
    public void testPublicPrivateEncrypt() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, BadPaddingException, InvalidKeyException, NoSuchProviderException {
        Key[] keys = Encryption.getKeys();
        RSAPublicKey pubKey = (RSAPublicKey) keys[0];
        RSAPrivateCrtKey privKey = (RSAPrivateCrtKey) keys[1];
        Log.i("Encryption", "Public Key modulus: " + pubKey.getModulus());
        Log.i("Encryption", "Public Key exponent: " + pubKey.getPublicExponent());
        Log.i("Encryption", "Private Key p: " + privKey.getPrimeExponentP());
        Log.i("Encryption", "Private Key q: " + privKey.getPrimeExponentQ());
        Log.i("Encryption", "Private Key exponent: " + privKey.getPrivateExponent());
        OAEPParameterSpec spec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
        byte[] temp = Encryption.encrypt(keys[0], "Hello, world!");
        String temp2 = Arrays.toString(temp);
        byte[] temp3 = Encryption.bytesFromString(temp2);
        Log.i("Encryption", "String Ciphertext: " + temp2);
        Log.i("Encryption", "Ciphertext: " + Arrays.toString(temp3));
        Log.i("Encryption", "Ciphertext: " + Arrays.toString(temp));
        Log.i("Encryption", "Decrypted plaintext: " + new String(Encryption.decrypt(keys[1], temp)));
    }

    @Test
    public void testRecordEncryption() throws Exception {
        ApolloClient.Builder l = new ApolloClient.Builder();
        ApolloClient client = l.serverUrl("http://ec2-18-233-36-202.compute-1.amazonaws.com:4000/graphql").build();

        ApolloCall<Data> queryCall = client.query(new RecordByUuidQuery("dalsdfasjdfsdf"));
        Single<ApolloResponse<Data>> queryResponse = Rx2Apollo.single(queryCall);

        queryResponse.subscribe(new DisposableSingleObserver<ApolloResponse<Data>>() {
                                    @Override
                                    public void onSuccess(@NonNull ApolloResponse<RecordByUuidQuery.Data> dataApolloResponse) {
                                        Log.d("minnie",dataApolloResponse.data.toString());

                                        SecretKey symmKey = null;
                                        Record_by_uuid originalRecord = dataApolloResponse.data.record_by_uuid;
                                        Record_by_uuid encryptedRecord = null;
                                        Record_by_uuid decryptedRecord = null;
                                        try {
                                            symmKey = Encryption.createSymmetricKey("AES");
                                        } catch (NoSuchAlgorithmException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            encryptedRecord = Encryption.encryptRecord(originalRecord, symmKey, "AES");
                                            decryptedRecord = Encryption.decryptRecord(encryptedRecord, symmKey, "AES");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        assert(Objects.equals(originalRecord.dob, decryptedRecord.dob));
                                        assert(originalRecord.uuid.equals(decryptedRecord.uuid));
                                        assert(originalRecord.allergies.equals(decryptedRecord.allergies));
                                        assert(originalRecord.immunizations.equals(decryptedRecord.immunizations));
                                        assert(originalRecord.medications.equals(decryptedRecord.medications));
                                        assert(originalRecord.name.equals(decryptedRecord.name));
                                        assert(originalRecord.visit_notes.equals(decryptedRecord.visit_notes));
                                        Log.i("Encryption", "Original Hash: " + originalRecord.hashCode());
                                        Log.i("Encryption", "Unencrypted Hash: " + decryptedRecord.hashCode());
                                    }
                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.e("minnie",e.getMessage());
                                    }
                                }
        );

    }

    @Test
    public void testRecordReencryption() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        ApolloClient.Builder l = new ApolloClient.Builder();
        ApolloClient client = l.serverUrl("http://ec2-18-233-36-202.compute-1.amazonaws.com:4000/graphql").build();

        ApolloCall<Data> queryCall = client.query(new RecordByUuidQuery("dalsdfasjdfsdf"));
        Single<ApolloResponse<Data>> queryResponse = Rx2Apollo.single(queryCall);

        List<PublicKey> publicKeys = new ArrayList<>();
        List<PrivateKey> privateKeys = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Key[] keys = Encryption.getKeys();
            publicKeys.add((PublicKey) keys[0]);
            privateKeys.add((PrivateKey) keys[1]);
        }


        SecretKey oldKey = Encryption.createSymmetricKey(EncryptionConstants.algorithm);

        SecretKey newKey = Encryption.createSymmetricKey(EncryptionConstants.algorithm);


        queryResponse.subscribe(new DisposableSingleObserver<ApolloResponse<Data>>() {
                                    @Override
                                    public void onSuccess(@NonNull ApolloResponse<RecordByUuidQuery.Data> dataApolloResponse) {
                                        Record_by_uuid originalRecord = dataApolloResponse.data.record_by_uuid;
                                        Record_by_uuid encryptedRecord = null;
                                        Record_by_uuid reencryptedRecord = null;
                                        Record_by_uuid decryptedRecord = null;
                                        List<byte[]> encryptedKeys = null;
                                        try {
                                            encryptedRecord = Encryption.encryptRecord(originalRecord, oldKey, "AES");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            Map<Record_by_uuid, List<byte[]>> map = Encryption.reencryptRecord(encryptedRecord, oldKey, newKey, publicKeys);
                                            reencryptedRecord = new ArrayList<Record_by_uuid>(map.keySet()).get(0);
                                            encryptedKeys = (List<byte[]>) map.values().toArray()[0];
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        byte[] decryptedKey = null;
                                        try {
                                            decryptedKey = Encryption.decrypt(privateKeys.get(0), encryptedKeys.get(0));
                                        } catch (Exception e) {
                                            Log.i("Encryption", "Got here");
                                            e.printStackTrace();
                                        }
                                        decryptedKey = Encryption.bytesFromString(new String(decryptedKey));
                                        Log.i("Encryption", Arrays.toString(newKey.getEncoded()));
                                        Log.i("Encryption", Arrays.toString(decryptedKey));
                                        assertEquals(Arrays.toString(decryptedKey), Arrays.toString(newKey.getEncoded()));
                                        try {
                                            decryptedRecord = Encryption.decryptRecord(reencryptedRecord, Encryption.bytesToKey(decryptedKey), EncryptionConstants.algorithm);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.e("minnie",e.getMessage());
                                    }
                                }
        );
    }

    @Test
    public void testEncryptingSymmetricKey() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, BadPaddingException, InvalidKeyException, NoSuchProviderException {
        SecretKey key = Encryption.createSymmetricKey(EncryptionConstants.algorithm);
        Key[] keys = Encryption.getKeys();
        RSAPublicKey pubKey = (RSAPublicKey) keys[0];
        RSAPrivateCrtKey privKey = (RSAPrivateCrtKey) keys[1];
        Log.i("Encryption", "Symmetric key: " + Arrays.toString(key.getEncoded()));
        byte[] encrypted = Encryption.encrypt(pubKey, Arrays.toString(key.getEncoded()));
        byte[] decrypted = Encryption.decrypt(privKey, encrypted);
        Log.i("Encryption", "Symmetric key: " + Arrays.toString(Encryption.bytesFromString(new String(decrypted))));
        assertEquals(Arrays.toString(key.getEncoded()), Arrays.toString(Encryption.bytesFromString(new String(decrypted))));
    }

    @Test
    public void testKeyEncodings() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, InvalidKeySpecException {
        Key[] keys = Encryption.getKeys();
        String pubEnc = Encryption.keyToEncoded(keys[0]);
        String privEnc = Encryption.keyToEncoded(keys[1]);
        Key pubKey = Encryption.encodedToPubKey(pubEnc);
        Key privKey = Encryption.encodedToPrivKey(privEnc);
        assertEquals(keys[0], pubKey);
        assertEquals(keys[1], privKey);
        Log.i("Encryption", String.valueOf(keys[0].hashCode()));
        Log.i("Encryption", String.valueOf(pubKey.hashCode()));
        Log.i("Encryption", String.valueOf(keys[1].hashCode()));
        Log.i("Encryption", String.valueOf(privKey.hashCode()));

    }

}
