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
import com.mednote.cwru.login.models.LoggedInUser;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.RemoteFunctionCall;

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
//                                            getLoggedInUser().setSymmetricKey(getKeyForUuid.symmetric_key);
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
                                            setKeyRequestStatus(DataRequestStatus.data_received);
                                        } else {
                                            setKeyRequestStatus(DataRequestStatus.error);
                                        }
                                        Log.d("GraphQL", String.valueOf(dataApolloResponse.data));
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.d("minnie",e.getMessage());
                                        setDataRequestStatus(DataRequestStatus.error);
                                    }
                                }
        );
        setDataRequestStatus(DataRequestStatus.request_sent);
    }

    public void decryptServerData() {

    }

    public void encryptServerData() {

    }

    @Override
    protected void instantiatePermissions() {

    }
}
