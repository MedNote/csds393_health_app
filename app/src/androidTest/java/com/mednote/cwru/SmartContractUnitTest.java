package com.mednote.cwru;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Subscription;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;

import static org.junit.Assert.*;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.OnSuccessListener;
import com.mednote.cwru.ethereum.ContractInteraction;
import com.mednote.cwru.ethereum.EHR;
import com.mednote.cwru.ethereum.EthereumConstants;
import com.mednote.cwru.ethereum.Utils;
import com.mednote.cwru.login.LoginRepository;
import com.mednote.cwru.login.LoginServerResponse;
import com.mednote.cwru.login.ProviderLoginDataSource;
import com.mednote.cwru.login.SignUpServerResponse;
import com.mednote.cwru.login.models.AccountCredentials;
import com.mednote.cwru.login.models.Name;
import com.mednote.cwru.serverapi.ServerResult;
import com.mednote.cwru.util.FutureTaskWrapper;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;
import com.mednote.cwru.util.helpers.ExecutorServiceHelper;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import io.reactivex.disposables.Disposable;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SmartContractUnitTest {

    //private key for dummy wallet -- this is totally not secure this is just for testing purposes
    private static final String PRIVATE_KEY = "a364bb7760b042df99a560cb929fad458b3bbe3b1dc63cdeb7dfbe97c8451e03";

    java.util.concurrent.Semaphore s = new java.util.concurrent.Semaphore(0);

    @Test
    public void testInfuraConnection() throws ExecutionException, InterruptedException {
        Utils.setupBouncyCastle();
        Web3j web3 = Utils.getWeb3(EthereumConstants.url);
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
    public void testWalletLoad() throws Exception {
        Utils.setupBouncyCastle();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SecureRandom sec = new SecureRandom();
        byte[] entropy = sec.generateSeed(16);
        String mnemonic = MnemonicUtils.generateMnemonic(entropy);
        String password = "admin";
        String[] deets = Utils.loadWallet(appContext, password, mnemonic);
        String address = deets[0];
        String walletPath = deets[1];
        //fill up ether
        //Web3j web3 = Utils.getWeb3("https://mainnet.infura.io/v3/6fbf9fccb0db473dafa741602c69eab0");
        //Credentials credentials = WalletUtils.loadCredentials(password, walletPath);
        //Utils.faucetFill(web3, credentials);
    }

    @Test
    public void testWalletRecreate() throws Exception {
        Utils.setupBouncyCastle();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String password = "admin";
        String[] deets_generated = Utils.createWallet(appContext, password);
        String address_generated = deets_generated[0];
        String walletPath = deets_generated[1];
        String[] deets_loaded = Utils.loadWallet(appContext, password, deets_generated[2]);
        assertEquals(deets_generated[0], deets_loaded[0]);
    }


    @Test
    public void testLoadWallet() throws CipherException, IOException {
        Credentials credentials = WalletUtils.loadCredentials("admin", "/data/user/0/com.mednote.cwru/files/UTC--2022-04-09T00-20-38.1Z--35546d6d0a830993c9126da4ce2d871975cd20a5.json");
        Log.i("SmartContract", credentials.getAddress());
    }

    @Test
    public void testLogin() throws CipherException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Utils.setupBouncyCastle();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ApplicationContextHelper.getInstance().init(appContext);

        // Creating wallet
        String password = "admin";
        String[] deets = Utils.createWallet(appContext, password);
        AccountCredentials accountCredentials = new AccountCredentials(deets[0], password, deets[2]);
        // Registering doctor
        LoginRepository loginRepository = LoginRepository.getInstance(new ProviderLoginDataSource());
        FutureTaskWrapper<ServerResult<SignUpServerResponse>> signUpTask = loginRepository.signUp(new Name("Oleksii", "Fedorenko"), accountCredentials);
        signUpTask.addOnSuccessListener(new OnSuccessListener<ServerResult<SignUpServerResponse>>() {
            @Override
            public void onSuccess(ServerResult<SignUpServerResponse> signUpServerResponseServerResult) {
                SignUpServerResponse signUpServerResponse = signUpServerResponseServerResult.getResult();
                if (signUpServerResponse.getAccountCredentials() != null) {
                    Log.i("SmartContract", "signed in");
                    // Logging in Doctor
                    FutureTaskWrapper<ServerResult<LoginServerResponse>> futureTask = loginRepository.login(accountCredentials);
                    futureTask.addOnSuccessListener(new OnSuccessListener<ServerResult<LoginServerResponse>>() {
                        @Override
                        public void onSuccess(ServerResult<LoginServerResponse> loginServerResponseServerResult) {
                            Log.i("SmartContract", "Login Successful");
                        }
                    });
                    ExecutorServiceHelper.getInstance().execute(futureTask);
                }
            }
        });



        ExecutorServiceHelper.getInstance().execute(signUpTask);
        while (true) {
            if (signUpTask.isComplete()) {
//                break;
            }
        }

    }

    @Test
    public void testGetTransactions() throws ExecutionException, InterruptedException, IOException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String path = appContext.getFilesDir().getAbsolutePath();
        Credentials credentials = Credentials.create(PRIVATE_KEY);
        ContractInteraction ci = new ContractInteraction(credentials);
        List<TransactionReceipt> trs = ci.getTransactionsByType("DoctorCreation", null);
        for(TransactionReceipt tr : trs) {
            //Log.i("SmartContract", tr.)
        }
    }

    @Test
    public void testGetDoctorCreationEvents() throws ExecutionException, InterruptedException, IOException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String path = appContext.getFilesDir().getAbsolutePath();
        Credentials credentials = Credentials.create(PRIVATE_KEY);
        Web3j web3 = Utils.getWeb3(EthereumConstants.url);
        EHR ehr = EHR.load(EthereumConstants.contractAddress, web3, credentials, new DefaultGasProvider());
        EthGetTransactionReceipt tr = web3.ethGetTransactionReceipt("0x2696baf4fa0c08599817e45eab14100efb642b6468696f6af70df3d9de7f3bde").send();
        TransactionReceipt receipt = tr.getResult();
    }

    @Test
    public void testCreateDoctor() {

    }

    @Test
    public void getLogs() throws Exception {
        Web3j web3 = Utils.getWeb3(EthereumConstants.url);
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST, EthereumConstants.contractAddress);
        web3.ethLogFlowable(filter).subscribe(log -> {
            Log.i("SmartContract", log.getTopics().get(0));
        });
    }
}