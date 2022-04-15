package com.mednote.cwru.ethereum;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.protocol.core.methods.response.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RequiresApi(api = Build.VERSION_CODES.R)
public class ContractInteraction {

    private final Web3j web3;
    private final Credentials credentials;
    private final String contractAddress = "0xd99b95de8DD71C9763b87083B4A687784F08e731";
    private EHR contract;

    //event objects
    public static final Event PATIENT_CREATION = new Event("PatientCreation", Arrays.asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    public static final Event DOCTOR_CREATION = new Event("DoctorCreation", Arrays.asList(new TypeReference<Address>(true) {}));
    public static final Event DOCTOR_AUTHORIZATION = new Event("DoctorAuthorization", Arrays.asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Bool>(false) {}));
    public static final Event MEDICAL_RECORD_VIEW = new Event("MedicalRecordView", Arrays.asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    public static final Event MEDICAL_RECORD_UPDATE = new Event("MedicalRecordUpdate", Arrays.asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Bytes32>(false) {}));

    //event object hashes
    private static final String PATIENT_CREATION_HASH = EventEncoder.encode(PATIENT_CREATION);
    private static final String DOCTOR_CREATION_HASH = EventEncoder.encode(DOCTOR_CREATION);
    private static final String DOCTOR_AUTHORIZATION_HASH = EventEncoder.encode(DOCTOR_AUTHORIZATION);
    private static final String MEDICAL_RECORD_VIEW_HASH = EventEncoder.encode(MEDICAL_RECORD_VIEW);
    private static final String MEDICAL_RECORD_UPDATE_HASH = EventEncoder.encode(MEDICAL_RECORD_UPDATE);


    private static final Map<String,String> events = new HashMap<String,String>();
    static {
        events.put("PatientCreation", PATIENT_CREATION_HASH);
        events.put("DoctorCreation", DOCTOR_CREATION_HASH);
        events.put("DoctorAuthorization", DOCTOR_AUTHORIZATION_HASH);
        events.put("MedicalRecordView", MEDICAL_RECORD_VIEW_HASH);
        events.put("MedicalRecordUpdate", MEDICAL_RECORD_UPDATE_HASH);
    }

    public ContractInteraction(String url, String walletPassword, File walletDir) throws CipherException, IOException, ExecutionException, InterruptedException {
        this.web3 = Utils.getWeb3(url);
        this.credentials = WalletUtils.loadCredentials(walletPassword, walletDir);
        this.contract = new EHR(contractAddress, web3, credentials, new DefaultGasProvider());
    }

    public ContractInteraction(String url, Credentials credentials) throws ExecutionException, InterruptedException {
        this.web3 = Utils.getWeb3(url);
        this.credentials = credentials;
    }

    /**
     * Get the transaction receipts of the given type, from the given address.
     * @param type One of "PatientCreation", "DoctorCreation", "DoctorAuth", "MedicalRecordView", "MedicalRecordUpdate"
     * @param fromAddress Address of transaction sender, or null for transactions sent by every address.
     * @return List of transaction receipts
     */
    public List<TransactionReceipt> getTransactionsByType(String type, String fromAddress) throws IOException, ExecutionException, InterruptedException {
        EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, contractAddress);
        filter.addSingleTopic(events.get(type));
        if (fromAddress != null) {
            filter.addOptionalTopics(fromAddress);
        }
        List<TransactionReceipt> trs = new ArrayList<TransactionReceipt>();
        web3.ethLogFlowable(filter).subscribe(log -> {
            String eventHash = log.getTopics().get(0);
            if (eventHash == PATIENT_CREATION_HASH) {

            } else if (eventHash == DOCTOR_CREATION_HASH) {

            } else if (eventHash == DOCTOR_AUTHORIZATION_HASH) {

            } else if (eventHash == MEDICAL_RECORD_VIEW_HASH) {

            } else {

            }
        });
        return trs;
    }




}
