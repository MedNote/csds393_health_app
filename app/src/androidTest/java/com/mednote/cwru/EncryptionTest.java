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
import com.mednote.cwru.ethereum.ContractInteraction;
import com.mednote.cwru.util.Encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
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
                                        Log.i("Encryption", "Original DOB: " + originalRecord.dob);
                                        Log.i("Encryption", "Original UUID: " + originalRecord.uuid);
                                        Log.i("Encryption", "Original Allergies: " + originalRecord.allergies.toString());
                                        Log.i("Encryption", "Original Immunizations: " + originalRecord.immunizations.toString());
                                        Log.i("Encryption", "Original Medications: " + originalRecord.medications.toString());
                                        Log.i("Encryption", "Original Name: " + originalRecord.name.toString());
                                        Log.i("Encryption", "Original Visit Notes: " + originalRecord.visit_notes.toString());
                                        Log.i("Encryption", "Encrypted DOB: " + encryptedRecord.dob);
                                        Log.i("Encryption", "Encrypted UUID: " + encryptedRecord.uuid);
                                        Log.i("Encryption", "Encrypted Allergies: " + encryptedRecord.allergies.toString());
                                        Log.i("Encryption", "Encrypted Immunizations: " + encryptedRecord.immunizations.toString());
                                        Log.i("Encryption", "Encrypted Medications: " + encryptedRecord.medications.toString());
                                        Log.i("Encryption", "Encrypted Name: " + encryptedRecord.name.toString());
                                        Log.i("Encryption", "Encrypted Visit Notes: " + encryptedRecord.visit_notes.toString());
                                        Log.i("Encryption", "Decrypted DOB: " + decryptedRecord.dob);
                                        Log.i("Encryption", "Decrypted UUID: " + decryptedRecord.uuid);
                                        Log.i("Encryption", "Decrypted Allergies: " + decryptedRecord.allergies.toString());
                                        Log.i("Encryption", "Decrypted Immunizations: " + decryptedRecord.immunizations.toString());
                                        Log.i("Encryption", "Decrypted Medications: " + decryptedRecord.medications.toString());
                                        Log.i("Encryption", "Decrypted Name: " + decryptedRecord.name.toString());
                                        Log.i("Encryption", "Decrypted Visit Notes: " + decryptedRecord.visit_notes.toString());
                                        assert(Objects.equals(originalRecord.dob, decryptedRecord.dob));
                                        assert(originalRecord.uuid.equals(decryptedRecord.uuid));
                                        assert(originalRecord.allergies.equals(decryptedRecord.allergies));
                                        assert(originalRecord.immunizations.equals(decryptedRecord.immunizations));
                                        assert(originalRecord.medications.equals(decryptedRecord.medications));
                                        assert(originalRecord.name.equals(decryptedRecord.name));
                                        assert(originalRecord.visit_notes.equals(decryptedRecord.visit_notes));
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
                                        //TODO: Not done



                                    }
                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.e("minnie",e.getMessage());
                                    }
                                }
        );
    }
}
