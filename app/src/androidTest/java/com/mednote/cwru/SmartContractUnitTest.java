package com.mednote.cwru;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;

import static org.junit.Assert.*;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mednote.cwru.ethereum.Utils;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutionException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SmartContractUnitTest {
    @Test
    public void testInfuraConnection() throws ExecutionException, InterruptedException {
        Utils.setupBouncyCastle();
        Web3j web3 = Utils.getWeb3("https://mainnet.infura.io/v3/6fbf9fccb0db473dafa741602c69eab0");
    }

    @Test
    public void testWalletCreation() throws Exception {
        Utils.setupBouncyCastle();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String password = "admin";
        String[] deets = Utils.createWallet(appContext, password);
        String address = deets[0];
        String walletPath = deets[1];
        //fill up ether
        //Web3j web3 = Utils.getWeb3("https://mainnet.infura.io/v3/6fbf9fccb0db473dafa741602c69eab0");
        //Credentials credentials = WalletUtils.loadCredentials(password, walletPath);
        //Utils.faucetFill(web3, credentials);
    }

    @Test
    public void testLoadWallet() throws CipherException, IOException {
        Credentials credentials = WalletUtils.loadCredentials("admin", "/data/user/0/com.mednote.cwru/files/UTC--2022-04-09T00-20-38.1Z--35546d6d0a830993c9126da4ce2d871975cd20a5.json");
        Log.i("SmartContract", credentials.getAddress());
    }
}