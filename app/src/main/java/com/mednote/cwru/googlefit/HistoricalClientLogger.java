package com.mednote.cwru.googlefit;

import android.util.Log;

import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Device;
import com.google.android.gms.fitness.data.Field;
import com.mednote.cwru.util.helpers.TimeStampHelper;

import java.util.concurrent.TimeUnit;

public class HistoricalClientLogger {
    private static final String TAG = "Google_Fit: ";

    public static void LogContainer(GoogleFitDataContainer container) {
        Log.i(TAG,"Container:");
        Log.i(TAG,"\tType: " + container.getDataType().getName());
        Log.i(TAG,"\tN of DataPoints: " + container.getDataPointList().size());
        Log.i(TAG,"\tStart: " + TimeStampHelper.getTimestamp(container.getStartTime()));
        Log.i(TAG,"\tEnd: " + TimeStampHelper.getTimestamp(container.getEndTime()));
    }

    public static void LogBucketData(Bucket bucket) {
        Log.i(TAG,"Bucket:");
        Log.i(TAG,"\tType: " + bucket.getBucketType());
        Log.i(TAG,"\tActivity: " + bucket.getActivity());
        Log.i(TAG,"\tN of DataSets: " + bucket.getDataSets().size());
        Log.i(TAG,"\tStart: " + TimeStampHelper.getTimestamp(bucket.getStartTime(TimeUnit.MILLISECONDS)));
        Log.i(TAG,"\tEnd: " + TimeStampHelper.getTimestamp(bucket.getEndTime(TimeUnit.MILLISECONDS)));
        /*for (DataSet dataSet : bucket.getDataSets()) {
            this.dumpDataSet(dataSet);
        }*/
    }

    public static void LogDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        Log.i(TAG,"\tType: " + dataSet.getDataType().getName());
        Log.i(TAG,"\tSource: " + dataSet.getDataSource());
        Log.i(TAG,"\tN of DataPoints: " + dataSet.getDataPoints().size());
        for (DataPoint dp : dataSet.getDataPoints()) {
            LogDataPoint(dp);
        }
    }

    public static void LogDataPoint(DataPoint dp) {
        Log.i(TAG,"Data point:");
        Log.i(TAG,"\tType: " + dp.getDataType().getName());
        Log.i(TAG,"\tStart: " + TimeStampHelper.getTimestamp(dp.getStartTime(TimeUnit.MILLISECONDS)));
        Log.i(TAG,"\tEnd: " + TimeStampHelper.getTimestamp(dp.getEndTime(TimeUnit.MILLISECONDS)));
        for (Field field : dp.getDataType().getFields()) {
            Log.i(TAG,"\tField: " + field.getName());
            Log.i(TAG,"\tValue: " + dp.getValue(field));
        }
        Device device = dp.getOriginalDataSource().getDevice();
        if (device != null) {
            Log.i(TAG,"\tDevice: " + device.getManufacturer());
            Log.i(TAG,"\tDevice: " + device.getModel());
            Log.i(TAG,"\tDevice: " + device.getType());
            Log.i(TAG,"\tDevice: " + device.getUid());
        } else {
            Log.i(TAG,"\tDevice: " + device);
        }

        Log.i(TAG,"\tApplication: " + dp.getOriginalDataSource().getAppPackageName());
        Log.i(TAG,"\tStream: " + dp.getOriginalDataSource().getStreamName());
        Log.i(TAG,"\tStream Identifier: " + dp.getOriginalDataSource().getStreamIdentifier());

    }
}
