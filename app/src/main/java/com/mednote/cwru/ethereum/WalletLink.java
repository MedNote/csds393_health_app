package com.mednote.cwru.ethereum;

import android.content.Context;
import android.util.Log;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.concurrent.ExecutionException;

public class WalletLink {

    private String walletPath;
    private File walletDir;
    private String password;

    public WalletLink(Context c, String password) throws ExecutionException, InterruptedException, InvalidAlgorithmParameterException, CipherException, NoSuchAlgorithmException, IOException, NoSuchProviderException {
        this.setup();
        this.setupBouncyCastle();
        this.password = password;
        this.walletPath = c.getFilesDir().getAbsolutePath();
        this.walletDir = new File(walletPath);
        createWallet();
    }

    public void setup() throws ExecutionException, InterruptedException {
        Web3j web3 = Web3j.build(new HttpService("https://ropsten.infura.io/v3/6fbf9fccb0db473dafa741602c69eab0"));
        try {
            Web3ClientVersion clientVersion = web3.web3ClientVersion().sendAsync().get();
            if(!clientVersion.hasError()){
                Log.w("SmartContract", "Infura connection successful");
            }
            else {
                Log.e("SmartContract", "Infura connection failed");
            }
        }
        catch (Exception e) {
            throw e;
        }
    }

    public void createWallet() throws InvalidAlgorithmParameterException, CipherException, NoSuchAlgorithmException, IOException, NoSuchProviderException {
        String fileName;
        try{
            fileName =  WalletUtils.generateLightNewWalletFile(password,walletDir);
            this.walletDir = new File(walletPath + "/" + fileName);
            Credentials credentials = WalletUtils.loadCredentials(password, walletDir);
            Log.i("SmartContract", "Your address is " + credentials.getAddress());

        }
        catch (Exception e){
            throw e;
        }
    }

    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Web3j will set up the provider lazily when it's first used.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            // BC with same package name, shouldn't happen in real life.
            return;
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    private void fillEther() {

    }

}
