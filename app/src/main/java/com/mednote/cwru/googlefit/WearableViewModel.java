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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WearableViewModel extends BaseViewModel {
    private static final int CODE_GOOGLE_FIT = 10000;
    private final Context mainActivityContext;

    private GoogleFitReadRequestDataMap observableRequestMap;
    private GoogleFitReadRequestDataList observableRequestList;
    private ArrayList<String> observableRequestStringList;
    // Map of data types and position on list
    private Map<String, Integer> dataTypePosition;


    private Boolean wearableGoogleLoggedIn;
    private Boolean requestGoogleLogin;
    private Boolean permissionsGranted;
    private Boolean dataReceived;
    private Boolean dataRetrievalInProcess;

    public WearableViewModel(Context context) {
        mainActivityContext = context;
        wearableGoogleLoggedIn = false;
        requestGoogleLogin = false;
        permissionsGranted = false;
        dataReceived = false;
        dataRetrievalInProcess = false;

        observableRequestList = new GoogleFitReadRequestDataList();
        observableRequestMap = new GoogleFitReadRequestDataMap();
        observableRequestStringList = new ArrayList<>();
        dataTypePosition = new HashMap<>();

        // Instantiate parameters
        checkGoogleFitPermission();

        // Subscribe to the changes in the observable Map and List
        observableRequestList.addOnListChangedCallback(observableRequestListListener);
        observableRequestMap.addOnMapChangedCallback(observableRequestMapListener);

        // Self callback
        this.addOnPropertyChangedCallback(selfCallback);
    }

    // region Binding Getters
    @Bindable
    public Boolean getWearableGoogleLoggedIn() {
        return wearableGoogleLoggedIn;
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
    public Map<String, Integer> getDataTypePosition() {
        return dataTypePosition;
    }

    @Bindable
    public Boolean getDataRetrievalInProcess() {
        return dataRetrievalInProcess;
    }

    // endregion

    // region Binding Setters
    public void setWearableGoogleLoggedIn(Boolean wearableGoogleLoggedIn) {
        this.wearableGoogleLoggedIn = wearableGoogleLoggedIn;
        notifyPropertyChanged(BR.wearableGoogleLoggedIn);
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

    public void setDataTypePosition(Map<String, Integer> dataTypePosition) {
        this.dataTypePosition = dataTypePosition;
        notifyPropertyChanged(BR.dataTypePosition);
    }

    public void setDataRetrievalInProcess(Boolean dataRetrievalInProcess) {
        this.dataRetrievalInProcess = dataRetrievalInProcess;
        notifyPropertyChanged(BR.dataRetrievalInProcess);
    }

    // endregion

    private GoogleSignInOptionsExtension getFitnessOptions() {
        return HistoricalClientHelper.getFitnessOptions();
    }

    public void requestWearableData() {
        // Set the status to inProgress
        if (!getDataRetrievalInProcess()) {
            setDataRetrievalInProcess(true);
        }
        // This is to update sign in status
        if (!getWearableGoogleLoggedIn()) {
            setRequestGoogleLogin(true);
            return;
        }
        // Update permissions status
        if (!getPermissionsGranted()) {
            requestGoogleFitPermission();
            return;
        }
        if (!getDataReceived()) {
            parallelRequestData();
        }
    }

    private void parallelRequestData() {
        // TODO: better solution for google account
        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this.mainActivityContext, getFitnessOptions());
        HistoricalClientHelper clientHelper = new HistoricalClientHelper(account);

        for (DataType type : WearableDataTypesHelper.getDataTypeList()) {
            // TODO: should time be dynamic?
            long startTime = WearableRepository.getInstance().getStartTime(type);
            long endTime = WearableRepository.getInstance().getEndTime();
            HistoricalClientRequestHelper historicalClientRequestHelper = new HistoricalClientRequestHelper(type, startTime, endTime);
            getObservableRequestList().addAll(historicalClientRequestHelper.GetTimeBuckets());
        }

        for (GoogleFitReadRequestData readRequestData : getObservableRequestList()) {
            // Listen to changes in request properties
            readRequestData.addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    // TODO: fix the conditions
//                    updateObservableRequestMap((GoogleFitReadRequestData) sender);
                    putObservableRequestMap((GoogleFitReadRequestData) sender);
                    // TODO: theres is a better way to determine the completion, but I am too lazy
                    checkRequestStatus();
                }
            });
            HistoricalClientRequestHelper historicalClientRequestHelper = new HistoricalClientRequestHelper(readRequestData);
            DataReadRequest dataReadRequest = historicalClientRequestHelper.CreateDataReadRequest();
            OnSuccessListener<DataReadResponse> generalListener = response -> {
                // TODO: what if Google fit is empty
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        List<GoogleFitDataContainer> googleFitDataContainers = clientHelper.ProcessDataSetList(response.getDataSets());
                        // Only if file is not null post response received
                        // Update on UI thread
                        if (googleFitDataContainers != null) {
                            ((Activity) mainActivityContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    readRequestData.setResponseReceived(true);
                                }
                            });
                            // TODO: save in DB and send to server
                        }
                        // TODO: post data empty
                    }
                };
                ExecutorServiceHelper.getInstance().execute(runnable);
            };
            clientHelper.InitiateDataReadRequest(dataReadRequest, generalListener);
        }
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
            // TODO: update properties
//            newDataProcessed = newDataProcessed && gfRequestData.getSentToServer().get();
            newDataProcessed = newDataProcessed && gfRequestData.getResponseReceived().get();
        }
        if (newDataProcessed != dataReceived) {
            setDataReceived(newDataProcessed);
        }
    }

    synchronized private GoogleFitReadRequestDataMap putObservableRequestMap(GoogleFitReadRequestData gfRequestData) {
        String stringType = WearableDataTypesHelper.getDataCodesMap().get(gfRequestData.getDataType());
        getObservableRequestMap().put(stringType, gfRequestData);
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
            // If previous one was not received
            if (!prevGfRequestData.getResponseReceived().get()
                    && gfRequestData.getResponseReceived().get()) {
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

    private void updateObservableStringList(ObservableMap<String, GoogleFitReadRequestData> sender, String key) {
        Integer position = 0;
        if (dataTypePosition.containsKey(key)) {
            position = dataTypePosition.get(key);
        } else {
            position = dataTypePosition.size();
            dataTypePosition.put(key, position);
        }
        if (position >= getObservableRequestStringList().size()){
            getObservableRequestStringList().add(Objects.requireNonNull(sender.get(key)).toString());
        } else {
            getObservableRequestStringList().set(position, Objects.requireNonNull(sender.get(key)).toString());
        }
        notifyPropertyChanged(BR.observableRequestStringList);
    }

    private void updateObservableStringList(ObservableMap<String, GoogleFitReadRequestData> sender) {
        ArrayList<String> inString = new ArrayList<>();
        for (Map.Entry<String, GoogleFitReadRequestData> entry:
                sender.entrySet()) {
            inString.add(entry.getValue().toString());
        }
        setObservableRequestStringList(inString);
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
            updateObservableStringList(sender, key);
        }
    };

    private Observable.OnPropertyChangedCallback selfCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
        if (propertyId == BR.wearableGoogleLoggedIn) {
            if (getWearableGoogleLoggedIn() && getDataRetrievalInProcess()) {
                requestWearableData();
            }
            if (getWearableGoogleLoggedIn()) {
                setRequestGoogleLogin(false);
            }
        }
        if (getPermissionsGranted() && propertyId == BR.permissionsGranted) {
            if (getPermissionsGranted() && getDataRetrievalInProcess()) {
                requestWearableData();
            }
        }
        if (propertyId == BR.dataReceived) {
            if (getDataReceived()) {
                setDataRetrievalInProcess(false);
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