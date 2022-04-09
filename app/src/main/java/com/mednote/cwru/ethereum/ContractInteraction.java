package com.mednote.cwru.ethereum;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ContractInteraction {

    private Web3j web3;
    private Credentials credentials;
    private String contractAddress = "0xd99b95de8DD71C9763b87083B4A687784F08e731";
    private EHR contract;


    public ContractInteraction(String url, String walletPassword, File walletDir) throws CipherException, IOException, ExecutionException, InterruptedException {
        this.web3 = Utils.getWeb3(url);
        this.credentials = WalletUtils.loadCredentials(walletPassword, walletDir);
        this.contract = new EHR(contractAddress, web3, credentials, new DefaultGasProvider());

    }




}
