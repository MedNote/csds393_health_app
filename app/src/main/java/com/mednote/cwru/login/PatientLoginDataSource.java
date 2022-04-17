package com.mednote.cwru.login;

import com.mednote.cwru.ethereum.EHR;

import org.web3j.protocol.core.RemoteFunctionCall;

public class PatientLoginDataSource extends LoginDataSource{
    @Override
    public RemoteFunctionCall<Boolean> userExists(EHR contract, String address) {
        return contract.patientExists(address);
    }
}
