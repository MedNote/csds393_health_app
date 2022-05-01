package com.mednote.cwru.serverapi;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.rx2.Rx2Apollo;
import com.mednote.cwru.BR;
import com.mednote.cwru.GetKeyForUuidQuery;
import com.mednote.cwru.RecordByUuidQuery;
import com.mednote.cwru.base.BaseViewModel;
import com.mednote.cwru.ethereum.ContractInteraction;
import com.mednote.cwru.ethereum.EHR;
import com.mednote.cwru.ethereum.Utils;
import com.mednote.cwru.login.exchangetypes.LoginServerResponse;
import com.mednote.cwru.login.exchangetypes.LoginServerResult;
import com.mednote.cwru.login.models.AccountCredentials;
import com.mednote.cwru.login.models.AccountData;
import com.mednote.cwru.login.models.Immunization;
import com.mednote.cwru.login.models.LoggedInUser;
import com.mednote.cwru.login.models.Name;
import com.mednote.cwru.login.models.Note;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.RemoteFunctionCall;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;

public class ServerInteractionViewModel extends BaseViewModel {

    private DataRequestStatus dataRequestStatus;
    private DataRequestStatus keyRequestStatus;
    private LoggedInUser loggedInUser;
    private ApolloClient client;
    private String requestUuid;

    public ServerInteractionViewModel(LoggedInUser loggedInUser, String requestUuid) {
        this.loggedInUser = loggedInUser;
        this.requestUuid = requestUuid;
        this.dataRequestStatus = DataRequestStatus.no_request;
        this.keyRequestStatus = DataRequestStatus.no_request;

        ApolloClient.Builder l = new ApolloClient.Builder();
        client = l.serverUrl("http://ec2-18-233-36-202.compute-1.amazonaws.com:4000/graphql").build();
    }

    @Bindable
    public DataRequestStatus getDataRequestStatus() {
        return dataRequestStatus;
    }

    @Bindable
    public DataRequestStatus getKeyRequestStatus() {
        return keyRequestStatus;
    }

    public String getRequestUuid() {
        return requestUuid;
    }

    public LoggedInUser getLoggedInUser() {
        return loggedInUser;
    }

    public ApolloClient getClient() {
        return client;
    }

    public void setDataRequestStatus(DataRequestStatus dataRequestStatus) {
        this.dataRequestStatus = dataRequestStatus;
        ((Activity) applicationContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                notifyPropertyChanged(BR.dataRequestStatus);
            }
        });
    }

    public void setKeyRequestStatus(DataRequestStatus keyRequestStatus) {
        this.keyRequestStatus = keyRequestStatus;

        ((Activity) applicationContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                notifyPropertyChanged(BR.keyRequestStatus);
            }
        });
    }

    public void setLoggedInUser(LoggedInUser loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public void setRequestUuid(String requestUuid) {
        this.requestUuid = requestUuid;
    }

    public void getSymmetricKey() {
        setKeyRequestStatus(DataRequestStatus.initiated);

        ApolloCall<GetKeyForUuidQuery.Data> queryCall = client.query(new GetKeyForUuidQuery(requestUuid));
        Single<ApolloResponse<GetKeyForUuidQuery.Data>> queryResponse = Rx2Apollo.single(queryCall);

        queryResponse.subscribe(new DisposableSingleObserver<ApolloResponse<GetKeyForUuidQuery.Data>>() {
                                    @Override
                                    public void onSuccess(@NonNull ApolloResponse<GetKeyForUuidQuery.Data> dataApolloResponse) {
                                        if (dataApolloResponse.data != null) {
                                            GetKeyForUuidQuery.Get_key_for_uuid getKeyForUuid = dataApolloResponse.data.get_key_for_uuid;
                                            // TODO: decrypt key
                                            getLoggedInUser().setSymmetricKey(getKeyForUuid.symmetric_key);
                                            setKeyRequestStatus(DataRequestStatus.data_received);
                                        } else {
                                            setKeyRequestStatus(DataRequestStatus.error);
                                        }
                                        Log.d("GraphQL", String.valueOf(dataApolloResponse.data));
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.d("GraphQL",e.getMessage());
                                        setKeyRequestStatus(DataRequestStatus.error);
                                    }
                                }
        );
        setKeyRequestStatus(DataRequestStatus.request_sent);
    }

    public void getDataFromServer() {
        setDataRequestStatus(DataRequestStatus.initiated);
        ApolloCall<RecordByUuidQuery.Data> queryCall = client.query(new RecordByUuidQuery(requestUuid));
        Single<ApolloResponse<RecordByUuidQuery.Data>> queryResponse = Rx2Apollo.single(queryCall);

        queryResponse.subscribe(new DisposableSingleObserver<ApolloResponse<RecordByUuidQuery.Data>>() {
                                    @Override
                                    public void onSuccess(@NonNull ApolloResponse<RecordByUuidQuery.Data> dataApolloResponse) {
                                        if (dataApolloResponse.data != null) {
                                            RecordByUuidQuery.Record_by_uuid recordByUuid = dataApolloResponse.data.record_by_uuid;
                                            // TODO: decrypt data
//                                            getLoggedInUser().setSymmetricKey(getKeyForUuid.symmetric_key);
                                            try {
                                                AccountData accountData = decryptServerData(recordByUuid);
                                                getLoggedInUser().setAccountData(accountData);
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            }
                                            setKeyRequestStatus(DataRequestStatus.data_received);
                                        } else {
                                            setKeyRequestStatus(DataRequestStatus.error);
                                        }
                                        Log.d("GraphQL", String.valueOf(dataApolloResponse.data));
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.d("GraphQL",e.getMessage());
                                        setDataRequestStatus(DataRequestStatus.error);
                                    }
                                }
        );
        setDataRequestStatus(DataRequestStatus.request_sent);
    }

    public AccountData decryptServerData(RecordByUuidQuery.Record_by_uuid record) throws IllegalAccessException {
        // TODO: actually decrypt Data
        String uuid = null;
        Name name = null;
        String dob = null;
        List<String> allergies = new ArrayList<String>();
        List<String> medications = new ArrayList<String>();
        List<Immunization> immunizations = new ArrayList<Immunization>();
        List<Note> visit_notes = new ArrayList<Note>();
        for (Field f : record.getClass().getDeclaredFields()) {
            String fieldName = f.getName();
            switch (fieldName) {
                case "uuid": {
                    uuid = (String) f.get(record);
                    break;
                }
                case "name": {
                    RecordByUuidQuery.Name temp = (RecordByUuidQuery.Name) f.get(record);
                    String firstName = temp.first_name;
                    String lastName = temp.last_name;
                    name = new Name(firstName, lastName);
                    break;
                }
                case "dob": {
                    String temp = (String) f.get(record);
                    if (temp == null || temp.equals("null")) {
                        dob = null;
                    } else {
                        dob = (String) f.get(record);
                    }
                    break;
                }
                case "allergies": {
                    List<String> temp = (List<String>) f.get(record);
                    for (String str : temp) {
                        if (str == null || str.equals("null")) {
                            allergies.add(null);
                        } else {
                            allergies.add((String) str);
                        }
                    }
                    break;
                }
                case "medications": {
                    List<String> temp = (List<String>) f.get(record);
                    for (String str : temp) {
                        if (str == null || str.equals("null")) {
                            medications.add(null);
                        } else {
                            medications.add((String) str);
                        }
                    }
                    break;
                }
                case "immunizations": {
                    List<RecordByUuidQuery.Immunization> temp = (List<RecordByUuidQuery.Immunization>) f.get(record);
                    for (RecordByUuidQuery.Immunization imm : temp) {
                        String decryptImm;
                        String decryptDate;
                        if (imm.immunization == null || imm.immunization.equals("null")) {
                            decryptImm = null;
                        } else {
                            decryptImm = imm.immunization;
                        }

                        immunizations.add(new Immunization(decryptImm, (String) imm.date));
                    }
                    break;
                }
                case "visit_notes": {
                    List<RecordByUuidQuery.Visit_note> temp = (List<RecordByUuidQuery.Visit_note>) f.get(record);
                    for (RecordByUuidQuery.Visit_note vis : temp) {
                        visit_notes.add(new Note(
                                new String(vis.note),
                                (String) vis.date));
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
        AccountData accountData = new AccountData(uuid);
        accountData.setDob(dob);
        accountData.setAllergies(allergies);
        accountData.setName(name);
        accountData.setImmunizations(immunizations);
        accountData.setMedications(medications);
        accountData.setNotes(visit_notes);
        return accountData;
    }

    public void encryptServerData() {

    }

    @Override
    protected void instantiatePermissions() {

    }
}
