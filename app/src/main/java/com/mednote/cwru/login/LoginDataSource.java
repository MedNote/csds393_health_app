package com.mednote.cwru.login;

import com.mednote.cwru.login.models.AccountCredentials;
import com.mednote.cwru.serverapi.ServerResult;
import com.mednote.cwru.util.FutureTaskWrapper;

import java.util.concurrent.Callable;

public class LoginDataSource {
    public LoginDataSource() {

    }

    public FutureTaskWrapper<ServerResult> login(AccountCredentials accountCredentials) {
        return new FutureTaskWrapper<ServerResult>(new Callable<ServerResult>() {
            @Override
            public ServerResult call() throws Exception {
                // TODO: login with password and mnemonic
                // TODO: get wallet address (ass uuid) from credentials object
                // TODO: user patientExists to check if wallet address exists
                return null;
            }
        });
    }

    public FutureTaskWrapper<ServerResult> logout() {
        return null;
    }

}
