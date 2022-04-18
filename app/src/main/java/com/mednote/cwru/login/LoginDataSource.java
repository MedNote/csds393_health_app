package com.mednote.cwru.login;

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
                // TODO: login with password and mnemonic
                // TODO: get wallet address (ass uuid) from credentials object
                // TODO: user patientExists to check if wallet address exists
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
    public FutureTaskWrapper<ServerResult<SignUpServerResponse>> signUp(Name name, AccountCredentials accountCredentials) {
        return new FutureTaskWrapper<ServerResult<SignUpServerResponse>>(new Callable<ServerResult<SignUpServerResponse>>() {
            @Override
            public ServerResult<SignUpServerResponse> call() {
                // TODO: sign up with password and mnemonic
                // TODO: get wallet address (ass uuid) from credentials object
                // TODO: user patientExists to check if wallet address exists
                Utils.setupBouncyCastle();
                String[] wallet_loaded = new String[0];
                try {
                    wallet_loaded = Utils.loadWallet(ApplicationContextHelper.get(), accountCredentials.getPassword(), accountCredentials.getMnemonic());
                    Credentials credentials = WalletUtils.loadCredentials(accountCredentials.getPassword(), wallet_loaded[1]);
                    ContractInteraction contractInteraction = new ContractInteraction(credentials);
                    EHR contract = contractInteraction.getContract();
                    Key[] keys = Encryption.getKeys();
                    RemoteFunctionCall<TransactionReceipt> remoteFunctionCall = userRegister(name, contract, credentials.getAddress(), keys[1].getEncoded());
                    TransactionReceipt receipt = remoteFunctionCall.send();
                    AccountCredentials newAccountCredentials = new AccountCredentials(credentials.getAddress(), accountCredentials.getPassword(), wallet_loaded[2]);
                    return new SignUpServerResult(new SignUpServerResponse(receipt, name, newAccountCredentials));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return new SignUpServerResult(new SignUpServerResponse(null, name, null));
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
