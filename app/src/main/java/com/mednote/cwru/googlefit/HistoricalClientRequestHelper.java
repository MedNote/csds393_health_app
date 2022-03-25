package com.mednote.cwru.googlefit;

import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HistoricalClientRequestHelper {
    public DataType type;
    public long startTime;
    public long endTime;

    public HistoricalClientRequestHelper(DataType type, long startTime, long endTime) {
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public HistoricalClientRequestHelper(GoogleFitReadRequestData googleFitReadRequestData) {
        this(
                googleFitReadRequestData.getDataType(),
                googleFitReadRequestData.getStartTime(),
                googleFitReadRequestData.getEndTime()
        );
    }

    public DataReadRequest CreateDataReadRequest() {
        return CreateDataReadRequest(type, startTime, endTime);
    }

    public DataReadRequest CreateDataReadRequest(DataType type, long startTime, long endTime) {
//        Log.i(TAG, "Range Start: " + TimeStampHelper.getTimestamp(startTime));
//        Log.i(TAG, "Range End: " + TimeStampHelper.getTimestamp(endTime));
//        Log.w(TAG, "Days difference: " + TimeStampHelper.daysBetween(startTime, endTime));
        // Read the data that's been collected throughout the past week.
        DataReadRequest.Builder readRequestBuilder = new DataReadRequest.Builder();
        readRequestBuilder.read(type);
        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
        // bucketByTime allows for a time span, while bucketBySession allows bucketing by sessions.
        // TODO: bucket based on each data type?
//        readRequestBuilder.bucketByTime(1, TimeUnit.DAYS);
        readRequestBuilder.enableServerQueries();
        readRequestBuilder.setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS);
        return readRequestBuilder.build();
    }

    public List<DataReadRequest> CreateDataReadRequests() {
        List<DataReadRequest> readRequests = new ArrayList<>();
        // Resolution is based on the data type
        long requestResolution = WearableDataTypesHelper.getDataTypeRequestResolution(type);
        long currentEndTime = startTime + requestResolution;
        long prevEndTime = startTime;
        // Iteration ends at a point when endTime - currentEndTime < requestResolution
        for (; currentEndTime <= endTime; currentEndTime += requestResolution) {
            readRequests.add(CreateDataReadRequest(type, prevEndTime, currentEndTime));
            prevEndTime = currentEndTime;
        }
        // It adds a request resolution and fails to satisfy the condition
        // Add last interval
        readRequests.add(CreateDataReadRequest(type, currentEndTime - requestResolution, endTime));
        return readRequests;
    }

    public List<GoogleFitReadRequestData> GetTimeBuckets() {
        List<GoogleFitReadRequestData> googleFitReadRequestData = new ArrayList<>();
        // Resolution is based on the data type
        long requestResolution = WearableDataTypesHelper.getDataTypeRequestResolution(type);
        long currentEndTime = startTime + requestResolution;
        long prevEndTime = startTime;
        // Iteration ends at a point when endTime - currentEndTime < requestResolution
        for (; currentEndTime <= endTime; currentEndTime += requestResolution) {
            googleFitReadRequestData.add(new GoogleFitReadRequestData(type, prevEndTime, currentEndTime));
            prevEndTime = currentEndTime;
        }
        // It adds a request resolution and fails to satisfy the condition
        // Add last interval
        googleFitReadRequestData.add(new GoogleFitReadRequestData(type, currentEndTime - requestResolution, endTime));
        return googleFitReadRequestData;
    }
}
