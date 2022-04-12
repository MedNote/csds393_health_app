package com.mednote.cwru.ethereum;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;


@SuppressWarnings("rawtypes")
public class EHR extends Contract {
    public static final String BINARY = "608060405260008055600060015534801561001957600080fd5b50610bf2806100296000396000f3fe608060405234801561001057600080fd5b50600436106100a95760003560e01c8063a7bb580311610071578063a7bb580314610175578063afd9a94c146101a7578063b163b365146101ba578063b74be800146101cd578063c47cf5de146101e0578063f4863bed146101f957600080fd5b80632ad230a9146100ae57806351710f03146100f357806352e9e8a01461012257806358c9202c1461014d5780635ab361ef14610162575b600080fd5b6100de6100bc366004610920565b6001600160a01b03166000908152600360208190526040909120015460ff1690565b60405190151581526020015b60405180910390f35b6100de610101366004610920565b6001600160a01b031660009081526002602052604090206004015460ff1690565b610135610130366004610942565b61020c565b6040516001600160a01b0390911681526020016100ea565b61016061015b366004610920565b610274565b005b610160610170366004610920565b61035d565b610188610183366004610a28565b610436565b6040805160ff90941684526020840192909252908201526060016100ea565b6101606101b5366004610a65565b610465565b6101606101c8366004610a8f565b610518565b6100de6101db366004610b28565b610640565b6101356101ee366004610a28565b805160209091012090565b610160610207366004610a8f565b6106c3565b6040805160008082526020820180845287905260ff861692820192909252606081018490526080810183905260019060a0016020604051602081039080840390855afa158015610260573d6000803e3d6000fd5b5050604051601f1901519695505050505050565b6001600160a01b0381166000908152600360208190526040909120015460ff1680156102b257503360009081526002602052604090206004015460ff165b80156102e557503360009081526002602090815260408083206001600160a01b038516845260030190915290205460ff16155b6102ee57600080fd5b3360008181526002602090815260408083206001600160a01b0386168085526003909101835292819020805460ff1916600190811790915590519081529192917f58efeaa91070e02fbcbc866786a01ab2d142a9df696ebcd9fd1515e0954377fd91015b60405180910390a350565b6001600160a01b0381166000908152600360208190526040909120015460ff16801561039b57503360009081526002602052604090206004015460ff165b80156103cd57503360009081526002602090815260408083206001600160a01b038516845260030190915290205460ff165b6103d657600080fd5b3360008181526002602090815260408083206001600160a01b03861680855260039091018352818420805460ff19169055905192835292917f58efeaa91070e02fbcbc866786a01ab2d142a9df696ebcd9fd1515e0954377fd9101610352565b6000806000835160411461044957600080fd5b5050506020810151604082015160609092015160001a92909190565b6001600160a01b03821660009081526002602052604090206004015460ff1680156104f057503360009081526002602052604090206004015460ff16806104f05750336000908152600360208190526040909120015460ff1680156104f057506001600160a01b038216600090815260026020908152604080832033845260030190915290205460ff165b6104f957600080fd5b6001600160a01b03909116600090815260056020526040902060010155565b6001600160a01b0382166000908152600360208190526040909120015460ff161561054257600080fd5b6001600160a01b0382166000908152600360209081526040909120855161056b9287019061086b565b506001600160a01b0382166000908152600360209081526040909120845161059b9260019092019186019061086b565b506001600160a01b038216600090815260036020908152604090912082516105cb9260029092019184019061086b565b506001600160a01b0382166000908152600360208190526040822001805460ff1916600190811790915580549161060183610b5b565b90915550506040516001600160a01b038316907f4cd17b5d25f6ea7efaa933b18d886d9ebcb6b66e8a67f82a70a472bf1fc2004d90600090a250505050565b6001600160a01b03821660009081526003602081905260408220015460ff16801561068657506001600160a01b03821660009081526002602052604090206004015460ff165b61068f57600080fd5b506001600160a01b038082166000908152600260209081526040808320938616835260039093019052205460ff1692915050565b336000908152600360208190526040909120015460ff16801561070257506001600160a01b03821660009081526002602052604090206004015460ff16155b61070b57600080fd5b6001600160a01b038216600090815260026020908152604090912085516107349287019061086b565b506001600160a01b038216600090815260026020908152604090912084516107649260019092019186019061086b565b506001600160a01b038216600090815260026020818152604090922083516107949391909201919084019061086b565b506001600160a01b0382166000818152600260209081526040808320338085526003820184528285208054600160ff1991821681179092559686526004909201805490961682179095556005909252822080546001600160a01b03191690931783557fe3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b8559201919091558054908061082a83610b5b565b909155505060405133906001600160a01b038416907f98d045482cdb3a3eb48eaffed7579d8339191329ff0088239dd97ac546d59cba90600090a350505050565b82805461087790610b82565b90600052602060002090601f01602090048101928261089957600085556108df565b82601f106108b257805160ff19168380011785556108df565b828001600101855582156108df579182015b828111156108df5782518255916020019190600101906108c4565b506108eb9291506108ef565b5090565b5b808211156108eb57600081556001016108f0565b80356001600160a01b038116811461091b57600080fd5b919050565b60006020828403121561093257600080fd5b61093b82610904565b9392505050565b6000806000806080858703121561095857600080fd5b84359350602085013560ff8116811461097057600080fd5b93969395505050506040820135916060013590565b634e487b7160e01b600052604160045260246000fd5b600082601f8301126109ac57600080fd5b813567ffffffffffffffff808211156109c7576109c7610985565b604051601f8301601f19908116603f011681019082821181831017156109ef576109ef610985565b81604052838152866020858801011115610a0857600080fd5b836020870160208301376000602085830101528094505050505092915050565b600060208284031215610a3a57600080fd5b813567ffffffffffffffff811115610a5157600080fd5b610a5d8482850161099b565b949350505050565b60008060408385031215610a7857600080fd5b610a8183610904565b946020939093013593505050565b60008060008060808587031215610aa557600080fd5b843567ffffffffffffffff80821115610abd57600080fd5b610ac98883890161099b565b95506020870135915080821115610adf57600080fd5b610aeb8883890161099b565b9450610af960408801610904565b93506060870135915080821115610b0f57600080fd5b50610b1c8782880161099b565b91505092959194509250565b60008060408385031215610b3b57600080fd5b610b4483610904565b9150610b5260208401610904565b90509250929050565b600060018201610b7b57634e487b7160e01b600052601160045260246000fd5b5060010190565b600181811c90821680610b9657607f821691505b602082108103610bb657634e487b7160e01b600052602260045260246000fd5b5091905056fea2646970667358221220b84defa6f833ee096525c86e39e26179e15e9788fb6528261d3fc48a3933d30164736f6c634300080d0033";

    public static final String FUNC_AUTHORIZEDOCTOR = "authorizeDoctor";

    public static final String FUNC_CREATEDOCTOR = "createDoctor";

    public static final String FUNC_CREATEPATIENT = "createPatient";

    public static final String FUNC_DEAUTHORIZEDOCTOR = "deauthorizeDoctor";

    public static final String FUNC_DOCTORAUTHORIZED = "doctorAuthorized";

    public static final String FUNC_DOCTOREXISTS = "doctorExists";

    public static final String FUNC_GETADDRESS = "getAddress";

    public static final String FUNC_PATIENTEXISTS = "patientExists";

    public static final String FUNC_RECOVERPUBLICADDRESS = "recoverPublicAddress";

    public static final String FUNC_SPLITSIGNATURE = "splitSignature";

    public static final String FUNC_UPDATERECORDHASH = "updateRecordHash";

    public static final Event DOCTORAUTH_EVENT = new Event("DoctorAuth", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Bool>() {}));
    ;

    public static final Event DOCTORCREATION_EVENT = new Event("DoctorCreation", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    public static final Event MEDICALRECORDUPDATE_EVENT = new Event("MedicalRecordUpdate", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Bytes32>() {}));
    ;

    public static final Event MEDICALRECORDVIEW_EVENT = new Event("MedicalRecordView", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event PATIENTCREATION_EVENT = new Event("PatientCreation", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected EHR(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected EHR(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected EHR(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected EHR(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<DoctorAuthEventResponse> getDoctorAuthEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DOCTORAUTH_EVENT, transactionReceipt);
        ArrayList<DoctorAuthEventResponse> responses = new ArrayList<DoctorAuthEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DoctorAuthEventResponse typedResponse = new DoctorAuthEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._patient = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._doctor = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.isAuthorized = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DoctorAuthEventResponse> doctorAuthEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DoctorAuthEventResponse>() {
            @Override
            public DoctorAuthEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DOCTORAUTH_EVENT, log);
                DoctorAuthEventResponse typedResponse = new DoctorAuthEventResponse();
                typedResponse.log = log;
                typedResponse._patient = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._doctor = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.isAuthorized = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<DoctorAuthEventResponse> doctorAuthEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DOCTORAUTH_EVENT));
        return doctorAuthEventFlowable(filter);
    }

    public List<DoctorCreationEventResponse> getDoctorCreationEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DOCTORCREATION_EVENT, transactionReceipt);
        ArrayList<DoctorCreationEventResponse> responses = new ArrayList<DoctorCreationEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DoctorCreationEventResponse typedResponse = new DoctorCreationEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._doctor = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DoctorCreationEventResponse> doctorCreationEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DoctorCreationEventResponse>() {
            @Override
            public DoctorCreationEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DOCTORCREATION_EVENT, log);
                DoctorCreationEventResponse typedResponse = new DoctorCreationEventResponse();
                typedResponse.log = log;
                typedResponse._doctor = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<DoctorCreationEventResponse> doctorCreationEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DOCTORCREATION_EVENT));
        return doctorCreationEventFlowable(filter);
    }

    public List<MedicalRecordUpdateEventResponse> getMedicalRecordUpdateEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MEDICALRECORDUPDATE_EVENT, transactionReceipt);
        ArrayList<MedicalRecordUpdateEventResponse> responses = new ArrayList<MedicalRecordUpdateEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MedicalRecordUpdateEventResponse typedResponse = new MedicalRecordUpdateEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._patient = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._updator = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.fileHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<MedicalRecordUpdateEventResponse> medicalRecordUpdateEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, MedicalRecordUpdateEventResponse>() {
            @Override
            public MedicalRecordUpdateEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MEDICALRECORDUPDATE_EVENT, log);
                MedicalRecordUpdateEventResponse typedResponse = new MedicalRecordUpdateEventResponse();
                typedResponse.log = log;
                typedResponse._patient = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._updator = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.fileHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<MedicalRecordUpdateEventResponse> medicalRecordUpdateEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MEDICALRECORDUPDATE_EVENT));
        return medicalRecordUpdateEventFlowable(filter);
    }

    public List<MedicalRecordViewEventResponse> getMedicalRecordViewEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(MEDICALRECORDVIEW_EVENT, transactionReceipt);
        ArrayList<MedicalRecordViewEventResponse> responses = new ArrayList<MedicalRecordViewEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MedicalRecordViewEventResponse typedResponse = new MedicalRecordViewEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._patient = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._viewer = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<MedicalRecordViewEventResponse> medicalRecordViewEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, MedicalRecordViewEventResponse>() {
            @Override
            public MedicalRecordViewEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MEDICALRECORDVIEW_EVENT, log);
                MedicalRecordViewEventResponse typedResponse = new MedicalRecordViewEventResponse();
                typedResponse.log = log;
                typedResponse._patient = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._viewer = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<MedicalRecordViewEventResponse> medicalRecordViewEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MEDICALRECORDVIEW_EVENT));
        return medicalRecordViewEventFlowable(filter);
    }

    public List<PatientCreationEventResponse> getPatientCreationEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PATIENTCREATION_EVENT, transactionReceipt);
        ArrayList<PatientCreationEventResponse> responses = new ArrayList<PatientCreationEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PatientCreationEventResponse typedResponse = new PatientCreationEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse._patient = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._doctor = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<PatientCreationEventResponse> patientCreationEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, PatientCreationEventResponse>() {
            @Override
            public PatientCreationEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PATIENTCREATION_EVENT, log);
                PatientCreationEventResponse typedResponse = new PatientCreationEventResponse();
                typedResponse.log = log;
                typedResponse._patient = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._doctor = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<PatientCreationEventResponse> patientCreationEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PATIENTCREATION_EVENT));
        return patientCreationEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> authorizeDoctor(String _doctorAddress) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_AUTHORIZEDOCTOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _doctorAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> createDoctor(String _firstName, String _lastName, String _doctorAddress, byte[] _doctorPublicKey) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CREATEDOCTOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_firstName), 
                new org.web3j.abi.datatypes.Utf8String(_lastName), 
                new org.web3j.abi.datatypes.Address(160, _doctorAddress), 
                new org.web3j.abi.datatypes.DynamicBytes(_doctorPublicKey)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> createPatient(String _firstName, String _lastName, String _patientAddress, byte[] _patientPublicKey) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CREATEPATIENT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_firstName), 
                new org.web3j.abi.datatypes.Utf8String(_lastName), 
                new org.web3j.abi.datatypes.Address(160, _patientAddress), 
                new org.web3j.abi.datatypes.DynamicBytes(_patientPublicKey)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> deauthorizeDoctor(String _doctorAddress) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEAUTHORIZEDOCTOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _doctorAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> doctorAuthorized(String _doctorAddress, String _patientAddress) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DOCTORAUTHORIZED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _doctorAddress), 
                new org.web3j.abi.datatypes.Address(160, _patientAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> doctorExists(String _doctorAddress) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DOCTOREXISTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _doctorAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> getAddress(byte[] pubkey) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(pubkey)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> patientExists(String _patientAddress) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_PATIENTEXISTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _patientAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> recoverPublicAddress(byte[] messagehash, BigInteger v, byte[] r, byte[] s) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_RECOVERPUBLICADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(messagehash), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple3<BigInteger, byte[], byte[]>> splitSignature(byte[] signature) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SPLITSIGNATURE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(signature)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}, new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}));
        return new RemoteFunctionCall<Tuple3<BigInteger, byte[], byte[]>>(function,
                new Callable<Tuple3<BigInteger, byte[], byte[]>>() {
                    @Override
                    public Tuple3<BigInteger, byte[], byte[]> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<BigInteger, byte[], byte[]>(
                                (BigInteger) results.get(0).getValue(), 
                                (byte[]) results.get(1).getValue(), 
                                (byte[]) results.get(2).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> updateRecordHash(String _patientAddress, byte[] _hash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UPDATERECORDHASH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _patientAddress), 
                new org.web3j.abi.datatypes.generated.Bytes32(_hash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static EHR load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new EHR(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static EHR load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new EHR(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static EHR load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new EHR(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static EHR load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new EHR(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<EHR> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(EHR.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<EHR> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(EHR.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<EHR> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(EHR.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<EHR> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(EHR.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class DoctorAuthEventResponse extends BaseEventResponse {
        public String _patient;

        public String _doctor;

        public Boolean isAuthorized;
    }

    public static class DoctorCreationEventResponse extends BaseEventResponse {
        public String _doctor;
    }

    public static class MedicalRecordUpdateEventResponse extends BaseEventResponse {
        public String _patient;

        public String _updator;

        public byte[] fileHash;
    }

    public static class MedicalRecordViewEventResponse extends BaseEventResponse {
        public String _patient;

        public String _viewer;
    }

    public static class PatientCreationEventResponse extends BaseEventResponse {
        public String _patient;

        public String _doctor;
    }
}
