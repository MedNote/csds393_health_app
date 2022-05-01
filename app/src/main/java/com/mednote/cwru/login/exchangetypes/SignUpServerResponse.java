package com.mednote.cwru.login.exchangetypes;

import com.mednote.cwru.login.models.AccountCredentials;
import com.mednote.cwru.login.models.Name;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

public class SignUpServerResponse {
    private TransactionReceipt transactionReceipt;
    private String accountAddress;
    private Name name;

    public SignUpServerResponse(TransactionReceipt transactionReceipt, Name name, String accountAddress) {
        this.transactionReceipt = transactionReceipt;
        this.name = name;
        this.accountAddress = accountAddress;
    }

    public TransactionReceipt getTransactionReceipt() {
        return transactionReceipt;
    }

    public Name getName() {
        return name;
    }

    public String getAccountAddress() {
        return accountAddress;
    }

    public void setTransactionReceipt(TransactionReceipt transactionReceipt) {
        this.transactionReceipt = transactionReceipt;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public void setAccountAddress(String accountAddress) {
        this.accountAddress = accountAddress;
    }
}
