package com.mednote.cwru.login.models;

import com.mednote.cwru.login.models.AccountCredentials;
import com.mednote.cwru.login.models.AccountData;

import java.io.Serializable;
import java.security.Key;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser implements Serializable {

    private AccountCredentials accountCredentials;
    private Key publicKey;
    private Key privateKey;
    private String symmetricKey;
    private AccountData accountData;

    public LoggedInUser(AccountCredentials accountCredentials) {
        this.accountCredentials = accountCredentials;
        this.privateKey = null;
        this.publicKey = null;
        this.symmetricKey = null;
        this.accountData = null;
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

    public Key getPublicKey() {
        return publicKey;
    }

    public Key getPrivateKey() {
        return privateKey;
    }

    public String getSymmetricKey() {
        return symmetricKey;
    }

    public void setAccountCredentials(AccountCredentials accountCredentials) {
        this.accountCredentials = accountCredentials;
    }

    public void setPrivateKey(Key privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(Key publicKey) {
        this.publicKey = publicKey;
    }

    public void setSymmetricKey(String symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    public void setAccountData(AccountData accountData) {
        this.accountData = accountData;
    }
}