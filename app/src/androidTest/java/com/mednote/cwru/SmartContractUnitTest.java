package com.mednote.cwru;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
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

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.rx2.Rx2Apollo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mednote.cwru.ethereum.ContractInteraction;
import com.mednote.cwru.ethereum.EHR;
import com.mednote.cwru.ethereum.EthereumConstants;
import com.mednote.cwru.ethereum.Utils;
import com.mednote.cwru.login.LoginRepository;
import com.mednote.cwru.login.exchangetypes.LoginServerResponse;
import com.mednote.cwru.login.datasource.ProviderLoginDataSource;
import com.mednote.cwru.login.exchangetypes.SignUpServerResponse;
import com.mednote.cwru.login.models.AccountCredentials;
import com.mednote.cwru.login.models.Name;
import com.mednote.cwru.serverapi.DataRequestStatus;
import com.mednote.cwru.serverapi.ServerResult;
import com.mednote.cwru.util.Encryption;
import com.mednote.cwru.util.FutureTaskWrapper;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;
import com.mednote.cwru.util.helpers.ExecutorServiceHelper;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;

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
        String message = "Address: " + address + " password: " + password + " mnemonic: " + deets[2];
        Log.i("SmartContract", message);
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
    public void testWalletLoadPrivateKeyEqual() throws Exception {
        Utils.setupBouncyCastle();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Using doctor wallet
        String password = "admin";
//        String address = "0xea06a63b68149a4e1749bbfe1ce06f8d8c762cf2";
        String mnemonic = "test copy seed flash fix more roof abandon poem bread reform net";
        String[] deets1 = Utils.loadWallet(appContext, password, mnemonic);
        Log.i("SmartContract", deets1[3]);
        String[] deets2 = Utils.loadWallet(appContext, password, mnemonic);
        Log.i("SmartContract", deets2[3]);
        assertEquals(deets1[3], deets2[3]);
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
    public void testSignUp() throws CipherException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Utils.setupBouncyCastle();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ApplicationContextHelper.getInstance().init(appContext);

        // Using doctor wallet
        String password = "admin";
        String address = "0xea06a63b68149a4e1749bbfe1ce06f8d8c762cf2";
        String mnemonic = "hungry hair truth vapor smooth blast swear bulb camera eager invest chronic";
        AccountCredentials accountCredentials = new AccountCredentials(address, password, mnemonic);

        // Creating new Address
        String newAddress = "0xbb0974413561bfc3961858426ae9639d3c66ad62";
        Key[] keys = Encryption.getKeys();
        String message = "Public: " + keys[0].toString() + " private: " + keys[1].toString();
        Log.i("SmartContract", message);

        // Registering user
        LoginRepository loginRepository = LoginRepository.getInstance(new ProviderLoginDataSource());
        FutureTaskWrapper<ServerResult<SignUpServerResponse>> signUpTask = loginRepository.signUp(new Name("Oleksii", "Fedorenko"), accountCredentials, newAddress, keys[0]);
        signUpTask.addOnSuccessListener(new OnSuccessListener<ServerResult<SignUpServerResponse>>() {
            @Override
            public void onSuccess(ServerResult<SignUpServerResponse> signUpServerResponseServerResult) {
                SignUpServerResponse signUpServerResponse = signUpServerResponseServerResult.getResult();
                if (signUpServerResponse != null && signUpServerResponse.getTransactionReceipt() != null) {
                    Log.i("SmartContract", "signed up");
                }
            }
        });



        ExecutorServiceHelper.getInstance().execute(signUpTask);
        while (true) {
            if (signUpTask.isComplete()) {
                break;
            }
        }

    }

    @Test
    public void genKeyPair() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
        Utils.setupBouncyCastle();
        Key[] keys = Encryption.getKeys();
        String message = "Public: " + keys[0].toString() + " private: " + keys[1].toString();
        Log.i("SmartContract", message);

    }

    @Test
    public void testLogin() throws CipherException, IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Utils.setupBouncyCastle();
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ApplicationContextHelper.getInstance().init(appContext);

        // Using doctor wallet
        String password = "admin";
        String address = "0xea06a63b68149a4e1749bbfe1ce06f8d8c762cf2";
        String mnemonic = "hungry hair truth vapor smooth blast swear bulb camera eager invest chronic";
        AccountCredentials accountCredentials = new AccountCredentials(address, password, mnemonic);

        // Logging in doctor
        Log.i("SmartContract", "signed in");
        // Logging in Doctor
        LoginRepository loginRepository = LoginRepository.getInstance(new ProviderLoginDataSource());
        FutureTaskWrapper<ServerResult<LoginServerResponse>> futureTask = loginRepository.login(accountCredentials);
        futureTask.addOnSuccessListener(new OnSuccessListener<ServerResult<LoginServerResponse>>() {
            @Override
            public void onSuccess(ServerResult<LoginServerResponse> loginServerResponseServerResult) {
                Log.i("SmartContract", "Login Successful");
            }
        });
        ExecutorServiceHelper.getInstance().execute(futureTask);

        while (true) {
            if (futureTask.isComplete()) {
                break;
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
    public void testGettingSymmetricKey() throws ExecutionException, InterruptedException, IOException {
        ApolloClient.Builder l = new ApolloClient.Builder();
        ApolloClient client = l.serverUrl("http://ec2-18-233-36-202.compute-1.amazonaws.com:4000/graphql").build();
        String requestUuid = "0xa99818ba06798cbe402882e035ee62ca47f41f75";

        ApolloCall<GetKeyForUuidQuery.Data> queryCall = client.query(new GetKeyForUuidQuery(requestUuid));
        Single<ApolloResponse<GetKeyForUuidQuery.Data>> queryResponse = Rx2Apollo.single(queryCall);

        queryResponse.subscribe(new DisposableSingleObserver<ApolloResponse<GetKeyForUuidQuery.Data>>() {
                                    @Override
                                    public void onSuccess(@NonNull ApolloResponse<GetKeyForUuidQuery.Data> dataApolloResponse) {
                                        if (dataApolloResponse.data != null) {
                                            GetKeyForUuidQuery.Get_key_for_uuid getKeyForUuid = dataApolloResponse.data.get_key_for_uuid;
                                            // TODO: decrypt key
//                                            getLoggedInUser().setSymmetricKey(getKeyForUuid.symmetric_key);
                                        }
                                        Log.d("GraphQL", String.valueOf(dataApolloResponse.data));
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.d("GraphQL",e.getMessage());
                                    }
                                }
        );

        while (true) {
//            if (futureTask.isComplete()) {
//                break;
//            }
        }
    }

    @Test
    public void testSettingSymmetricKey() throws ExecutionException, InterruptedException, IOException {
        ApolloClient.Builder l = new ApolloClient.Builder();
        ApolloClient client = l.serverUrl("http://ec2-18-233-36-202.compute-1.amazonaws.com:4000/graphql").build();
        String requestUuid = "0xa99818ba06798cbe402882e035ee62ca47f41f75";
        String symmetricKey = "aaaaaa";

        ApolloCall<AddKeyForUuidMutation.Data> queryCall = client.mutation(new AddKeyForUuidMutation(requestUuid, symmetricKey));
        Single<ApolloResponse<AddKeyForUuidMutation.Data>> queryResponse = Rx2Apollo.single(queryCall);

        queryResponse.subscribe(new DisposableSingleObserver<ApolloResponse<AddKeyForUuidMutation.Data>>() {
                                    @Override
                                    public void onSuccess(@NonNull ApolloResponse<AddKeyForUuidMutation.Data> dataApolloResponse) {
                                        if (dataApolloResponse.data != null) {
                                            AddKeyForUuidMutation.Add_key_for_uuid addKeyForUuid = dataApolloResponse.data.add_key_for_uuid;
                                            // TODO: decrypt key
//                                            getLoggedInUser().setSymmetricKey(getKeyForUuid.symmetric_key);
                                        }
                                        Log.d("GraphQL", String.valueOf(dataApolloResponse.data));
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.d("GraphQL",e.getMessage());
                                    }
                                }
        );

        while (true) {
//            if (futureTask.isComplete()) {
//                break;
//            }
        }
    }

    @Test
    public void testAddingSymmetricKey() throws ExecutionException, InterruptedException, IOException {
        ApolloClient.Builder l = new ApolloClient.Builder();
        ApolloClient client = l.serverUrl("http://ec2-18-233-36-202.compute-1.amazonaws.com:4000/graphql").build();
        String requestUuid = "0xa99818ba06798cbe402882e035ee62ca47f41f75";
        String symmetricKey = "aaaaaa";

        ApolloCall<AddKeyForUuidMutation.Data> queryCall = client.mutation(new AddKeyForUuidMutation(requestUuid, symmetricKey));
        Single<ApolloResponse<AddKeyForUuidMutation.Data>> queryResponse = Rx2Apollo.single(queryCall);

        queryResponse.subscribe(new DisposableSingleObserver<ApolloResponse<AddKeyForUuidMutation.Data>>() {
                                    @Override
                                    public void onSuccess(@NonNull ApolloResponse<AddKeyForUuidMutation.Data> dataApolloResponse) {
                                        if (dataApolloResponse.data != null) {
                                            AddKeyForUuidMutation.Add_key_for_uuid addKeyForUuid = dataApolloResponse.data.add_key_for_uuid;
                                            Log.i("GraphQL", addKeyForUuid.toString());
                                            // TODO: decrypt key
//                                            getLoggedInUser().setSymmetricKey(getKeyForUuid.symmetric_key);
                                        }
                                        Log.d("GraphQL", String.valueOf(dataApolloResponse.data));
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.d("GraphQL",e.getMessage());
                                    }
                                }
        );

        while (true) {
//            if (futureTask.isComplete()) {
//                break;
//            }
        }
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