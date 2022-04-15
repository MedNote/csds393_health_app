package com.mednote.cwru.login.models;

import com.mednote.cwru.login.models.AccountCredentials;
import com.mednote.cwru.login.models.AccountData;

import java.io.Serializable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser implements Serializable {

    private AccountCredentials accountCredentials;
    private String symmetricKey;
    private AccountData accountData;

    public LoggedInUser(AccountCredentials accountCredentials) {
        this.accountCredentials = accountCredentials;
    }

    public AccountCredentials getAccountCredentials() {
        return accountCredentials;
    }

    public String getUuid() {
        return getAccountCredentials().getUuid();
    }

    public AccountData getAccountData() {
        return accountData;
    }

    public String getSymmetricKey() {
        return symmetricKey;
    }

    public void setAccountCredentials(AccountCredentials accountCredentials) {
        this.accountCredentials = accountCredentials;
    }

    public void setSymmetricKey(String symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    public void setAccountData(AccountData accountData) {
        this.accountData = accountData;
    }
}