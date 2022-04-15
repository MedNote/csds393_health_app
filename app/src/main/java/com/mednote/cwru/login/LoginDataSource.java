package com.mednote.cwru.login;

import com.mednote.cwru.ethereum.ContractInteraction;
import com.mednote.cwru.ethereum.EHR;
import com.mednote.cwru.ethereum.Utils;
import com.mednote.cwru.login.models.AccountCredentials;
import com.mednote.cwru.serverapi.ServerResult;
import com.mednote.cwru.util.FutureTaskWrapper;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.RemoteFunctionCall;

import java.util.concurrent.Callable;

public abstract class LoginDataSource {
    public LoginDataSource() {

    }

    public FutureTaskWrapper<ServerResult<LoginServerResponse>> login(AccountCredentials accountCredentials) {
        return new FutureTaskWrapper<ServerResult<LoginServerResponse>>(new Callable<ServerResult<LoginServerResponse>>() {
            @Override
            public ServerResult<LoginServerResponse> call() throws Exception {
                // TODO: login with password and mnemonic
                // TODO: get wallet address (ass uuid) from credentials object
                // TODO: user patientExists to check if wallet address exists
                String[] wallet_loaded = Utils.loadWallet(ApplicationContextHelper.get(), accountCredentials.getPassword(), accountCredentials.getMnemonic());
                Credentials credentials = WalletUtils.loadCredentials(accountCredentials.getPassword(), wallet_loaded[1]);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    ContractInteraction contractInteraction = new ContractInteraction(credentials);
                    EHR contract = contractInteraction.getContract();
                    RemoteFunctionCall<Boolean> remoteFunctionCall = userExists(contract, credentials.getAddress());
                    Boolean exists = remoteFunctionCall.send();
                    AccountCredentials newAccountCredentials = new AccountCredentials(credentials.getAddress(), accountCredentials.getPassword(), accountCredentials.getMnemonic());
                    return new LoginServerResult(new LoginServerResponse(exists, newAccountCredentials));
                }
                return new LoginServerResult(new LoginServerResponse(false, accountCredentials));
            }
        });
    }

    public abstract RemoteFunctionCall<Boolean> userExists(EHR contract, String address);

    public FutureTaskWrapper<ServerResult<LogoutServerResponse>> logout() {
        // TODO: finish logout
        return null;
    }

}
