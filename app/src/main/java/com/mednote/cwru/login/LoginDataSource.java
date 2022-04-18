package com.mednote.cwru.login;

import android.util.Log;

import com.mednote.cwru.ethereum.ContractInteraction;
import com.mednote.cwru.ethereum.EHR;
import com.mednote.cwru.ethereum.Utils;
import com.mednote.cwru.login.models.AccountCredentials;
import com.mednote.cwru.login.models.Name;
import com.mednote.cwru.serverapi.ServerResult;
import com.mednote.cwru.util.Encryption;
import com.mednote.cwru.util.FutureTaskWrapper;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.security.Key;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public abstract class LoginDataSource {
    public LoginDataSource() {

    }

    public FutureTaskWrapper<ServerResult<LoginServerResponse>> login(AccountCredentials accountCredentials) {
        return new FutureTaskWrapper<ServerResult<LoginServerResponse>>(new Callable<ServerResult<LoginServerResponse>>() {
            @Override
            public ServerResult<LoginServerResponse> call() {
                Utils.setupBouncyCastle();
                String[] wallet_loaded = new String[0];
                try {
                    wallet_loaded = Utils.loadWallet(ApplicationContextHelper.get(), accountCredentials.getPassword(), accountCredentials.getMnemonic());
                    Credentials credentials = WalletUtils.loadCredentials(accountCredentials.getPassword(), wallet_loaded[1]);
                    ContractInteraction contractInteraction = new ContractInteraction(credentials);
                    EHR contract = contractInteraction.getContract();
                    RemoteFunctionCall<Boolean> remoteFunctionCall = userExists(contract, credentials.getAddress());
                    Boolean exists = remoteFunctionCall.send();
                    AccountCredentials newAccountCredentials = new AccountCredentials(credentials.getAddress(), accountCredentials.getPassword(), accountCredentials.getMnemonic());
                    return new LoginServerResult(new LoginServerResponse(exists, newAccountCredentials));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return new LoginServerResult(new LoginServerResponse(false, accountCredentials));
            }
        });
    }
    public FutureTaskWrapper<ServerResult<SignUpServerResponse>> signUp(Name name, AccountCredentials signerCredentials, String newAddress, Key publicKey) {
        return new FutureTaskWrapper<ServerResult<SignUpServerResponse>>(new Callable<ServerResult<SignUpServerResponse>>() {
            @Override
            public ServerResult<SignUpServerResponse> call() {
                Utils.setupBouncyCastle();
                String[] signerWallet = new String[0];
                String[] newWallet = new String[0];
                try {
                    // Instantiate EHR as a signer
                    signerWallet = Utils.loadWallet(ApplicationContextHelper.get(), signerCredentials.getPassword(), signerCredentials.getMnemonic());
                    Credentials credentials = WalletUtils.loadCredentials(signerCredentials.getPassword(), signerWallet[1]);
                    ContractInteraction contractInteraction = new ContractInteraction(credentials);
                    EHR contract = contractInteraction.getContract();
                    // Sign up new user
                    Log.i("SmartContract", "Trying to get the RemoteFunctionCall");
                    RemoteFunctionCall<TransactionReceipt> remoteFunctionCall = userRegister(name, contract, newAddress, publicKey.getEncoded());
                    Log.i("SmartContract", "Trying to send the RemoteFunctionCall");
                    TransactionReceipt receipt = remoteFunctionCall.send();
                    Log.i("SmartContract", "Got back the RemoteFunctionCall");
                    return new SignUpServerResult(new SignUpServerResponse(receipt, name, newAddress));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return new SignUpServerResult(new SignUpServerResponse(null, name, newAddress));
            }
        });
    }

    public abstract RemoteFunctionCall<Boolean> userExists(EHR contract, String address);

    public abstract RemoteFunctionCall<TransactionReceipt> userRegister(Name name, EHR contract, String address, byte[] patientPublicKey);

    public FutureTaskWrapper<ServerResult<LogoutServerResponse>> logout() {
        // TODO: finish logout
        return null;
    }

}
