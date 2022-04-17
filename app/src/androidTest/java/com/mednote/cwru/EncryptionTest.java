package com.mednote.cwru;

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
import java.security.Security;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.xml.bind.DatatypeConverter;

import com.mednote.cwru.RecordByUuidQuery.*;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;

@RunWith(AndroidJUnit4.class)
public class EncryptionTest {

    @Test
    public void testSymmetricEncrypt() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        //symmetric key encryption
        SecretKey symmKey = Encryption.createSymmetricKey("AES");
        Log.i("Encryption", "Symmetric key: " + DatatypeConverter.printHexBinary(symmKey.getEncoded()));
        byte[] ciphertext = Encryption.symmetricEncrypt(symmKey, "Hello, world!", "AES");
        Log.i("Encryption", "Ciphertext: " + new String(ciphertext));
        String plaintext = new String(Encryption.symmetricDecrypt(symmKey, ciphertext, "AES"));
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
                                        Record_by_uuid encryptedRecord = null;
                                        try {
                                            symmKey = Encryption.createSymmetricKey("AES");
                                        } catch (NoSuchAlgorithmException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            encryptedRecord = Encryption.encryptRecord(dataApolloResponse.data.record_by_uuid, symmKey, "AES");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Log.i("Encryption", "Encrypted DOB: " + encryptedRecord.dob);
                                        Log.i("Encryption", "Encrypted UUID: " + encryptedRecord.uuid);
                                        Log.i("Encryption", "Encrypted Allergies: " + encryptedRecord.allergies.toString());
                                        Log.i("Encryption", "Encrypted Immunizations: " + encryptedRecord.immunizations.toString());
                                        Log.i("Encryption", "Encrypted Medications: " + encryptedRecord.medications.toString());
                                        Log.i("Encryption", "Encrypted Name: " + encryptedRecord.name.toString());
                                        Log.i("Encryption", "Encrypted Visit Notes: " + encryptedRecord.visit_notes.toString());
                                    }
                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.e("minnie",e.getMessage());
                                    }
                                }
        );

    }
}
