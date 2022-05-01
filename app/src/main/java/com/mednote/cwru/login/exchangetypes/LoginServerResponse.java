package com.mednote.cwru.login.exchangetypes;

import com.mednote.cwru.login.models.AccountCredentials;

import org.web3j.abi.datatypes.Bool;

public class LoginServerResponse {
    private Boolean userExists;
    private AccountCredentials accountCredentials;

    public LoginServerResponse(Boolean userExists, AccountCredentials accountCredentials) {
        this.userExists = userExists;
        this.accountCredentials = accountCredentials;
    }

    public Boolean getUserExists() {
        return userExists;
    }

    public AccountCredentials getAccountCredentials() {
        return accountCredentials;
    }

    public void setUserExists(Boolean userExists) {
        this.userExists = userExists;
    }

    public void setAccountCredentials(AccountCredentials accountCredentials) {
        this.accountCredentials = accountCredentials;
    }
}
