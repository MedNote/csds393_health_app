package com.mednote.cwru.login.datasource;

import com.mednote.cwru.ethereum.EHR;
import com.mednote.cwru.login.datasource.LoginDataSource;
import com.mednote.cwru.login.models.Name;

import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

public class PatientLoginDataSource extends LoginDataSource {
    @Override
    public RemoteFunctionCall<Boolean> userExists(EHR contract, String address) {
        return contract.patientExists(address);
    }

    @Override
    public RemoteFunctionCall<TransactionReceipt> userRegister(Name name, EHR contract, String address, byte[] patientPublicKey) {
        return contract.createPatient(name.getFirst_name(), name.getLast_name(), address, patientPublicKey);
    }
}
