package com.mednote.cwru.googlefit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableList;
import androidx.databinding.ObservableMap;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mednote.cwru.BR;
import com.mednote.cwru.base.BaseViewModel;
import com.mednote.cwru.util.helpers.ExecutorServiceHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WearableViewModel extends BaseViewModel {
    private static final int CODE_GOOGLE_FIT = 10000;
    private final Context mainActivityContext;

    private GoogleFitReadRequestDataMap observableRequestMap;
    private GoogleFitReadRequestDataList observableRequestList;
    private ArrayList<String> observableRequestStringList;

    private Boolean googleLoggedIn;
    private Boolean requestGoogleLogin;
    private Boolean permissionsGranted;
    private Boolean dataReceived;
    private Boolean dataRetrievalInProcess;

    public WearableViewModel(Context context) {
        mainActivityContext = context;
        googleLoggedIn = false;
        requestGoogleLogin = false;
        permissionsGranted = false;
        dataReceived = false;
        dataRetrievalInProcess = false;

        observableRequestList = new GoogleFitReadRequestDataList();
        observableRequestMap = new GoogleFitReadRequestDataMap();
        observableRequestStringList = new ArrayList<>();

        // Subscribe to the changes in the observable Map and List
        observableRequestList.addOnListChangedCallback(observableRequestListListener);
        observableRequestMap.addOnMapChangedCallback(observableRequestMapListener);

        // Self callback
        this.addOnPropertyChangedCallback(selfCallback);
    }

    // region Binding Getters
    @Bindable
    public Boolean getGoogleLoggedIn() {
        return googleLoggedIn;
    }

    @Bindable
    public Boolean getRequestGoogleLogin() {
        return requestGoogleLogin;
    }

    @Bindable
    public Boolean getPermissionsGranted() {
        return permissionsGranted;
    }

    @Bindable
    public Boolean getDataReceived() {
        return dataReceived;
    }

    @Bindable
    public GoogleFitReadRequestDataMap getObservableRequestMap() {
        return observableRequestMap;
    }

    @Bindable
    public GoogleFitReadRequestDataList getObservableRequestList() {
        return observableRequestList;
    }

    @Bindable
    public ArrayList<String> getObservableRequestStringList() {
        return observableRequestStringList;
    }

    @Bindable
    public Boolean getDataRetrievalInProcess() {
        return dataRetrievalInProcess;
    }

    // endregion

    // region Binding Setters
    public void setGoogleLoggedIn(Boolean googleLoggedIn) {
        this.googleLoggedIn = googleLoggedIn;
        notifyPropertyChanged(BR.googleLoggedIn);
    }

    public void setRequestGoogleLogin(Boolean requestGoogleLogin) {
        this.requestGoogleLogin = requestGoogleLogin;
        notifyPropertyChanged(BR.requestGoogleLogin);
    }

    public void setPermissionsGranted(Boolean permissionsGranted) {
        this.permissionsGranted = permissionsGranted;
        notifyPropertyChanged(BR.permissionsGranted);
    }

    public void setDataReceived(Boolean dataReceived) {
        this.dataReceived = dataReceived;
        notifyPropertyChanged(BR.dataReceived);
    }

    public void setObservableRequestMap(GoogleFitReadRequestDataMap observableRequestMap) {
        this.observableRequestMap = observableRequestMap;
        notifyPropertyChanged(BR.observableRequestMap);
    }

    public void setObservableRequestList(GoogleFitReadRequestDataList observableRequestList) {
        this.observableRequestList = observableRequestList;
        notifyPropertyChanged(BR.observableRequestList);
    }

    public void setObservableRequestStringList(ArrayList<String> observableRequestStringList) {
        this.observableRequestStringList = observableRequestStringList;
        notifyPropertyChanged(BR.observableRequestStringList);
    }

    public void setDataRetrievalInProcess(Boolean dataRetrievalInProcess) {
        this.dataRetrievalInProcess = dataRetrievalInProcess;
        notifyPropertyChanged(BR.dataRetrievalInProcess);
    }

    // endregion

    synchronized private GoogleFitReadRequestDataMap putObservableRequestMap(GoogleFitReadRequestData gfRequestData) {
        String stringType = WearableDataTypesHelper.getDataCodesMap().get(gfRequestData.getDataType());
        ((Activity) mainActivityContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getObservableRequestMap().put(stringType, gfRequestData);
            }
        });
        return getObservableRequestMap();
    }

    private void updateObservableRequestMapFromList(ObservableList<GoogleFitReadRequestData> sender, int positionStart, int itemCount) {
        for (int i = positionStart; i < positionStart + itemCount; i++) {
            updateObservableRequestMap(sender.get(i));
        }
    }

    private void updateObservableRequestMap(GoogleFitReadRequestData gfRequestData) {
        String stringType = WearableDataTypesHelper.getDataCodesMap().get(gfRequestData.getDataType());
        if (getObservableRequestMap().containsKey(stringType)) {
            GoogleFitReadRequestData prevGfRequestData = Objects.requireNonNull(getObservableRequestMap().get(stringType));
            // General case when the sent status is the same
            if (prevGfRequestData.getEndTime() < gfRequestData.getEndTime()
                    && prevGfRequestData.getSentToServer().get()
                    == gfRequestData.getSentToServer().get()) {
                putObservableRequestMap(gfRequestData);
            }
            // If previous one was not sent to server
            if (!prevGfRequestData.getSentToServer().get()
                    && gfRequestData.getSentToServer().get()) {
                putObservableRequestMap(gfRequestData);
            }
        } else {
            putObservableRequestMap(gfRequestData);
        }
    }

    private GoogleSignInOptionsExtension getFitnessOptions() {
        return HistoricalClientHelper.getFitnessOptions();
    }

    public void requestWearableData() {
        // Set the status to inProgress
        setDataRetrievalInProcess(true);
        // This is to update sign in status
        if (!getGoogleLoggedIn()) {
            setRequestGoogleLogin(true);
            return;
        }
        // Update permissions status
        checkGoogleFitPermission();
        if (!permissionsGranted) {
            requestGoogleFitPermission();
            return;
        }
        if (!dataReceived) {
            parallelRequestData();
        }
    }

    private void parallelRequestData() {
        // TODO: better solution for google account
        /*GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this.mainActivityContext, getFitnessOptions());

        ArrayList<GoogleFitReadRequestData> requestDataArray = new ArrayList<>();
        for (DataType type : WearableDataTypesHelper.getDataTypeList()) {
            HistoricalClientRequestHelper historicalClientRequestHelper = new HistoricalClientRequestHelper(type)
            requestDataArray.addAll(clientHelper.GetTimeBuckets(type));
        }
        HistoricalClientHelper clientHelper = new HistoricalClientHelper(account);

        for (GoogleFitReadRequestData gfRequestData : requestDataArray) {
            MutableLiveData<GoogleFitReadRequestData> requestData = new MutableLiveData<>(gfRequestData);
            liveReadRequestData.add(requestData); // TODO: can this crash the app under certain conditions?
            OnSuccessListener<DataReadResponse> generalListener = response -> {
                // TODO: what if Google fit is empty
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        File savedFile = clientHelper.ProcessDataSetList(response.getDataSets());
                        // Only if file is not null post response received
                        if (savedFile != null) {
                            GoogleFitReadRequestData newGfRequestData = new GoogleFitReadRequestData(gfRequestData);
                            newGfRequestData.setFile(savedFile);
                            newGfRequestData.setResponseReceived(true);
                            requestData.postValue(newGfRequestData);
                            updateObservableRequestMap(newGfRequestData);
                            sendFileToServer("wearable", savedFile, newGfRequestData);
                        }
                        // TODO: post data empty
                    }
                };
                ExecutorServiceHelper.getInstance().execute(runnable);
            };
            DataReadRequest dataReadRequest = clientHelper.CreateDataReadRequest(Objects.requireNonNull(requestData.getValue()));
            clientHelper.InitiateDataReadRequest(dataReadRequest, generalListener);
        }*/
    }

    public void requestGoogleFitPermission() {
        // TODO: better solution for google account
        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this.mainActivityContext, getFitnessOptions());
        // In the activity catch the result and invoke the on activity result in ViewModel
        GoogleSignIn.requestPermissions(
                (Activity) this.mainActivityContext,
                CODE_GOOGLE_FIT,
                account,
                getFitnessOptions());
    }

    public void checkGoogleFitPermission() {
        // TODO: better solution for google account
        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this.mainActivityContext, getFitnessOptions());
        setPermissionsGranted(GoogleSignIn.hasPermissions(account, getFitnessOptions()));
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode != CODE_GOOGLE_FIT) {
            return;
        }
        setPermissionsGranted(resultCode == Activity.RESULT_OK);
    }

    /**
     * Checks if all the requests were processed
     */
    private void checkRequestStatus() {
        boolean newDataProcessed = true;
        for (String key: observableRequestMap.keySet()) {
            GoogleFitReadRequestData gfRequestData = observableRequestMap.get(key);
            newDataProcessed = newDataProcessed && gfRequestData.getSentToServer().get();
        }
        if (newDataProcessed != dataReceived) {
            setDataReceived(newDataProcessed);
        }
    }

    private ObservableList.OnListChangedCallback<ObservableList<GoogleFitReadRequestData>> observableRequestListListener = new ObservableList.OnListChangedCallback<ObservableList<GoogleFitReadRequestData>>() {
        @Override
        public void onChanged(ObservableList<GoogleFitReadRequestData> sender) {
            notifyPropertyChanged(BR.observableRequestList);
            updateObservableRequestMapFromList(sender, 0, sender.size());
        }

        @Override
        public void onItemRangeChanged(ObservableList<GoogleFitReadRequestData> sender, int positionStart, int itemCount) {
            notifyPropertyChanged(BR.observableRequestList);
            updateObservableRequestMapFromList(sender, positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList<GoogleFitReadRequestData> sender, int positionStart, int itemCount) {
            notifyPropertyChanged(BR.observableRequestList);
            updateObservableRequestMapFromList(sender, positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList<GoogleFitReadRequestData> sender, int fromPosition, int toPosition, int itemCount) {
            notifyPropertyChanged(BR.observableRequestList);
        }

        @Override
        public void onItemRangeRemoved(ObservableList<GoogleFitReadRequestData> sender, int positionStart, int itemCount) {
            notifyPropertyChanged(BR.observableRequestList);
            updateObservableRequestMapFromList(sender, positionStart, itemCount);
        }
    };

    private final ObservableMap.OnMapChangedCallback<? extends ObservableMap<String, GoogleFitReadRequestData>, String, GoogleFitReadRequestData> observableRequestMapListener = new ObservableMap.OnMapChangedCallback<ObservableMap<String, GoogleFitReadRequestData>, String, GoogleFitReadRequestData>() {
        @Override
        public void onMapChanged(ObservableMap<String, GoogleFitReadRequestData> sender, String key) {
            notifyPropertyChanged(BR.observableRequestMap);
            // TODO: there are better ways to do this, but I am lazy
            ArrayList<String> inString = new ArrayList<>();
            for (Map.Entry<String, GoogleFitReadRequestData> entry:
                 sender.entrySet()) {
                inString.add(entry.getValue().toString());
            }
            setObservableRequestStringList(inString);
        }
    };

    private Observable.OnPropertyChangedCallback selfCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == BR.googleLoggedIn) {
                if (getDataRetrievalInProcess()) {
                    requestWearableData();
                }
            }
            if (propertyId == BR.permissionsGranted) {
                if (getDataRetrievalInProcess()) {
                    requestWearableData();
                }
            }
        }
    };

    @Override
    protected void instantiatePermissions() {
        requiredPermissions = new HashMap<String, ObservableBoolean>() {{
            put(Manifest.permission.BODY_SENSORS, new ObservableBoolean(false));
        }};
        recommendedPermissions = new HashMap<>();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            requiredPermissions.put(Manifest.permission.ACTIVITY_RECOGNITION, new ObservableBoolean(false));
        }
    }
}