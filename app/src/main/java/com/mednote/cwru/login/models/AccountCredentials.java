package com.mednote.cwru.login.models;

import java.io.Serializable;

public class AccountCredentials implements Serializable {
    private final String uuid;
    private String password;
    private String mnemonic;

    public AccountCredentials(String uuid, String password, String mnemonic) {
        this.uuid = uuid;
        this.password = password;
        this.mnemonic = mnemonic;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPassword() {
        return password;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }
}
