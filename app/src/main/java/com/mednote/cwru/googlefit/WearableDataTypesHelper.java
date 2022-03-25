package com.mednote.cwru.googlefit;

import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.HealthDataTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Here are the parameters in the IRB that you can collect if they are available:
 * heart rate, steps, activity, sleep, weight, hear rate variability,
 * SpO2, Skin Temperature, electrodermal activity, electrocardiograms,
 * calorie intake, reproductive health.
 */
public class WearableDataTypesHelper {
    private static final Map<String, DataType> dataTypesMap = new HashMap<String, DataType>(){
        {
            // gf = Google Fit
            // hr = Heart Rate
            put("gf-hr", DataType.TYPE_HEART_RATE_BPM);
//            put("gf-hr-aggregate", DataType.AGGREGATE_HEART_RATE_SUMMARY); // Threw error: Unsupported input data type specified for aggregation: DataType{com.google.heart_rate.summary[average(f), max(f), min(f)]}
            put("gf-step-delta", DataType.TYPE_STEP_COUNT_DELTA);
//            put("gf-activity-summary", DataType.AGGREGATE_ACTIVITY_SUMMARY);
            put("gf-activity", DataType.TYPE_ACTIVITY_SEGMENT);
            put("gf-sleep", DataType.TYPE_SLEEP_SEGMENT); // Exception: Unsupported input data type specified for aggregation: DataType{com.google.sleep.segment[sleep_segment_type(i)]}
            put("gf-weight", DataType.TYPE_WEIGHT);
            put("gf-weight-aggregate", DataType.AGGREGATE_WEIGHT_SUMMARY);
            put("gf-oxygen", HealthDataTypes.TYPE_OXYGEN_SATURATION);
            put("gf-oxygen-summary", HealthDataTypes.AGGREGATE_OXYGEN_SATURATION_SUMMARY);
            put("gf-body-temp", HealthDataTypes.TYPE_BODY_TEMPERATURE);
        }};

    private static final Map<DataType, String> dataCodesMap = new HashMap<DataType, String>(){
        {
            // gf = Google Fit
            // hr = Heart Rate
            put(DataType.TYPE_HEART_RATE_BPM, "gf-hr");
            // put(DataType.AGGREGATE_HEART_RATE_SUMMARY, "gf-hr-aggregate");
            put(DataType.TYPE_STEP_COUNT_DELTA, "gf-step-delta");
            // put(DataType.AGGREGATE_ACTIVITY_SUMMARY, "gf-activity-summary");
            put(DataType.TYPE_ACTIVITY_SEGMENT, "gf-activity");
            put(DataType.TYPE_SLEEP_SEGMENT, "gf-sleep");
            put(DataType.TYPE_WEIGHT, "gf-weight");
//            put(DataType.AGGREGATE_WEIGHT_SUMMARY, "gf-weight-aggregate");
            put(HealthDataTypes.TYPE_OXYGEN_SATURATION, "gf-oxygen");
//            put(HealthDataTypes.AGGREGATE_OXYGEN_SATURATION_SUMMARY, "gf-oxygen-summary");
            put(HealthDataTypes.TYPE_BODY_TEMPERATURE, "gf-body-temp");
        }};

    private static final Map<DataType, String> dataNamesMap = new HashMap<DataType, String>(){
        {
            // gf = Google Fit
            // hr = Heart Rate
            put(DataType.TYPE_HEART_RATE_BPM, "Heart Rate");
             put(DataType.AGGREGATE_HEART_RATE_SUMMARY, "Heart Rate Aggregate");
            put(DataType.TYPE_STEP_COUNT_DELTA, "Step Count");
             put(DataType.AGGREGATE_ACTIVITY_SUMMARY, "Activity Summary");
            put(DataType.TYPE_ACTIVITY_SEGMENT, "Activity");
            put(DataType.TYPE_SLEEP_SEGMENT, "Sleep");
            put(DataType.TYPE_WEIGHT, "Weight");
            put(DataType.AGGREGATE_WEIGHT_SUMMARY, "Weight Summary");
            put(HealthDataTypes.TYPE_OXYGEN_SATURATION, "Blood Oxygen");
            put(HealthDataTypes.AGGREGATE_OXYGEN_SATURATION_SUMMARY, "Blood Oxygen Summary");
            put(HealthDataTypes.TYPE_BODY_TEMPERATURE, "Body Temperature");
        }};


    private static final Map<Integer, String> deviceTypeMap = new HashMap<Integer, String>() {{
            put(0, "TYPE_UNKNOWN");
            put(1, "TYPE_PHONE");
            put(2, "TYPE_TABLET");
            put(3, "TYPE_WATCH");
            put(4, "TYPE_CHEST_STRAP");
            put(5, "TYPE_SCALE");
            put(6, "TYPE_HEAD_MOUNTED");
        }};

    // Resolution is in days
    private static final Map<String, Integer> dataCodeRequestResolution = new HashMap<String, Integer>(){
        {
            // gf = Google Fit
            // hr = Heart Rate
            put("gf-hr",                21);
            put("gf-hr-aggregate",      21); // Threw error: Unsupported input data type specified for aggregation: DataType{com.google.heart_rate.summary[average(f), max(f), min(f)]}
            put("gf-step-delta",        28);
            put("gf-activity-summary",  56);
            put("gf-activity",          56);
            put("gf-sleep",             120); // Exception: Unsupported input data type specified for aggregation: DataType{com.google.sleep.segment[sleep_segment_type(i)]}
            put("gf-weight",            1825);
            put("gf-weight-aggregate",  1825);
            put("gf-oxygen",            21);
            put("gf-oxygen-summary",    21);
            put("gf-body-temp",         28);
        }};

    public static Map<DataType, String> getDataNamesMap() {
        return dataNamesMap;
    }

    // region Data Type
    public static Map<String, DataType> getDataTypesMap() {
        Map<String, DataType> dataTypesMap = new HashMap<>();
        for (Map.Entry<DataType, String> dataTypeEntry : getDataCodesMap().entrySet()) {
            dataTypesMap.put(dataTypeEntry.getValue(), dataTypeEntry.getKey());
            // System.out.println(dataTypeEntry.getValue().getName());
        }
        return dataTypesMap;
    }

    public static List<DataType> getDataTypeList() {
        List<DataType> dataTypeList = new ArrayList<>();
        for (Map.Entry<String, DataType> entry : getDataTypesMap().entrySet()) {
            dataTypeList.add(entry.getValue());
        }
        return dataTypeList;
    }

    public static Map<DataType, String> getDataCodesMap() {
        /*Map<DataType, String> dataCodesMap = new HashMap<>();
        for (Map.Entry<String, DataType> dataTypeEntry : getDataTypesMap().entrySet()) {
            dataCodesMap.put(dataTypeEntry.getValue(), dataTypeEntry.getKey());
            // System.out.println(dataTypeEntry.getValue().getName());
        }*/
        return dataCodesMap;
    }
    // endregion

    // region Device Type
    public static Map<Integer, String> getDeviceTypeMap() {
        return deviceTypeMap;
    }
    // endregion

    // region Data Resolution

    public static Map<String, Integer> getDataCodeRequestResolutionMap() {
        return dataCodeRequestResolution;
    }

    public static Map<DataType, Integer> getDataTypeRequestResolutionMap() {
        Map<DataType, Integer> dataTypeRequestResolution = new HashMap<>();
        for (Map.Entry<String, Integer> entry : getDataCodeRequestResolutionMap().entrySet()) {
            dataTypeRequestResolution.put(getDataTypesMap().get(entry.getKey()), entry.getValue());
        }
        return dataTypeRequestResolution;
    }

    /**
     * Returns resolution in Milliseconds
     * @param dataType
     * @return
     */
    public static long getDataTypeRequestResolution(DataType dataType) {
        int daysResolution = 15;
        if (getDataTypeRequestResolutionMap().containsKey(dataType)) {
            daysResolution = getDataTypeRequestResolutionMap().get(dataType);
        }
        return TimeUnit.DAYS.toMillis(daysResolution);
    }

    // endregion
}
