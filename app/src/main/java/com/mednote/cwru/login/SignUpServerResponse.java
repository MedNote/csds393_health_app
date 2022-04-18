package com.mednote.cwru.login;

import com.mednote.cwru.login.models.AccountCredentials;
import com.mednote.cwru.login.models.Name;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

public class SignUpServerResponse {
    private TransactionReceipt transactionReceipt;
    private AccountCredentials accountCredentials;
    private Name name;

    public SignUpServerResponse(TransactionReceipt transactionReceipt, Name name, AccountCredentials accountCredentials) {
        this.transactionReceipt = transactionReceipt;
        this.name = name;
        this.accountCredentials = accountCredentials;
    }

    public TransactionReceipt getTransactionReceipt() {
        return transactionReceipt;
    }

    public Name getName() {
        return name;
    }

    public AccountCredentials getAccountCredentials() {
        return accountCredentials;
    }

    public void setTransactionReceipt(TransactionReceipt transactionReceipt) {
        this.transactionReceipt = transactionReceipt;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setAccountCredentials(AccountCredentials accountCredentials) {
        this.accountCredentials = accountCredentials;
    }
}
