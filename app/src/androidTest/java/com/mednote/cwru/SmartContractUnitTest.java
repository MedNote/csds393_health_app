package com.mednote.cwru;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.web3j.crypto.CipherException;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mednote.cwru.ethereum.WalletLink;

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
    public void testInfuraConnection() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String password = "admin";
        WalletLink wl = null;
        try {
            wl = new WalletLink(appContext, password);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}