package com.mednote.cwru.googlefit;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Device;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mednote.cwru.base.AbstractRepository;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;
import com.mednote.cwru.util.helpers.TimeStampHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HistoricalClientHelper {
    private static final String TAG = "Google_Fit: ";
    private final Context appContext;
    private final GoogleSignInAccount googleSignInAccount;
    private int MaxInContainer = 20000;

    public HistoricalClientHelper(GoogleSignInAccount googleSignInAccount) {
        this.appContext = ApplicationContextHelper.get();
        this.googleSignInAccount = googleSignInAccount;
//                GoogleSignIn.getAccountForExtension(this.appContext, fitnessOptions);
    }

    public void setMaxInContainer(int maxInContainer) {
        MaxInContainer = maxInContainer;
    }

    public static GoogleSignInOptionsExtension getFitnessOptions() {
        FitnessOptions.Builder optionsBuilder = FitnessOptions.builder();

        for (DataType type : WearableDataTypesHelper.getDataTypeList()) {
            optionsBuilder.addDataType(type, FitnessOptions.ACCESS_READ);
        }
        return optionsBuilder.build();
    }

    public Task<DataReadResponse> InitiateDataReadRequest(DataReadRequest readRequest, OnSuccessListener<DataReadResponse> onSuccessListener) {
        return Fitness.getHistoryClient(this.appContext, googleSignInAccount)
                .readData(readRequest)
                .addOnSuccessListener(onSuccessListener)
                /*.addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataReadResponse> task) {
                        Log.w(TAG, "There was an error reading data from Google Fit");
                    }
                })*/
                .addOnCanceledListener(() -> Log.w(TAG, "Reading request from Google Fit was cancelled"))
                .addOnFailureListener(e -> Log.w(TAG, "There was an error reading data from Google Fit", e));
    }

    public List<GoogleFitDataContainer> ProcessBucketList(List<Bucket> bucketList) {
        GoogleFitDataContainer currentContainer = new GoogleFitDataContainer();
        List<GoogleFitDataContainer> googleFitDataContainers = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            for (DataSet dataSet : bucket.getDataSets()) {
                for (DataPoint dp : dataSet.getDataPoints()) {
//                    LogDataPoint(dp);
                    if (currentContainer.getDataPointList().size() < MaxInContainer) {
                        currentContainer.addDataPoint(dp);
                    } else {
                        googleFitDataContainers.add(currentContainer);
                        currentContainer = new GoogleFitDataContainer();
                    }
                }
            }
        }
        if (currentContainer.getDataPointList().size() > 0) {
            googleFitDataContainers.add(currentContainer);
        }
        return googleFitDataContainers;
    }

    public List<GoogleFitDataContainer> ProcessDataSetList(List<DataSet> dataSetList) {
        GoogleFitDataContainer currentContainer = new GoogleFitDataContainer();
        List<GoogleFitDataContainer> googleFitDataContainers = new ArrayList<>();
        for (DataSet dataSet : dataSetList) {
            // TODO: Logging for debug purposes
            HistoricalClientLogger.LogDataSet(dataSet);
            for (DataPoint dp : dataSet.getDataPoints()) {
//                    LogDataPoint(dp);
                if (currentContainer.getDataPointList().size() < MaxInContainer) {
                    currentContainer.addDataPoint(dp);
                } else {
                    googleFitDataContainers.add(currentContainer);
                    currentContainer = new GoogleFitDataContainer();
                }
            }
        }
        if (currentContainer.getDataPointList().size() > 0) {
            googleFitDataContainers.add(currentContainer);
        }
        return googleFitDataContainers;
    }
}