package com.mednote.cwru.ethereum;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.BitSet;
import java.util.concurrent.ExecutionException;

public class Utils {

    /**
    Instantiates a Web3j object with the provided URL.

    @param url HTTP Infura URL
    @return   Web3j object
     **/
    @NonNull
    public static Web3j getWeb3(String url) throws ExecutionException, InterruptedException {
        Web3j web3 = Web3j.build(new HttpService(url));
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
        return web3;
    }

    /**
    Creates a new wallet and returns the details.

    @param c Context of app
    @param password Password with which to generate wallet
    @return String array, arr[0] = address, arr[1] = wallet path
     **/
    public static String[] createWallet(Context c, String password) throws InvalidAlgorithmParameterException, CipherException, NoSuchAlgorithmException, IOException, NoSuchProviderException {
        String walletPath = c.getFilesDir().getAbsolutePath();
        File walletDir = new File(walletPath);
        Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet(password, walletDir);
        String fileName = bip39Wallet.getFilename();
        String mnemonic = bip39Wallet.getMnemonic();
        walletPath = walletPath + "/" + fileName;
        walletDir = new File(walletPath);
        Credentials credentials = WalletUtils.loadBip39Credentials(password, mnemonic);
        Log.i("SmartContract", "New wallet with address " + credentials.getAddress() + " created at " + walletPath + ".");
        return new String[] {credentials.getAddress(), walletPath, mnemonic};
    }

    public static String[] loadWallet(Context c, String password, String mnemonic) throws CipherException, IOException {
        String walletPath = c.getFilesDir().getAbsolutePath();
        File walletDir = new File(walletPath);
        Bip39Wallet bip39Wallet = WalletUtils.generateBip39WalletFromMnemonic(password, mnemonic, walletDir);
        String fileName = bip39Wallet.getFilename();
        walletPath = walletPath + "/" + fileName;
        walletDir = new File(walletPath);
        Credentials credentials = WalletUtils.loadBip39Credentials(password, mnemonic);
        Log.i("SmartContract", "Existing wallet with address " + credentials.getAddress() + " created at " + walletPath + ".");
        return new String[] {credentials.getAddress(), walletPath, mnemonic};
    }

    public static void faucetFill(Web3j web3, Credentials c) throws Exception {
        Log.i("SmartContract", "Sending 1 Wei ("
                + Convert.fromWei("1", Convert.Unit.ETHER).toPlainString() + " Ether)");
        TransactionReceipt transferReceipt = Transfer.sendFunds(
                web3, c,
                c.getAddress(),
                BigDecimal.ONE, Convert.Unit.WEI)
                .send();
        Log.i("SmartContract", "Transaction complete, view it at https://ropsten.etherscan.io/tx/"
                + transferReceipt.getTransactionHash());
    }

    /**
     * This method is for configuring the BouncyCastle encryption dependency.
     */
    public static void setupBouncyCastle() {
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

}
