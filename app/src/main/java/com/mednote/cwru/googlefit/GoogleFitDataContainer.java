package com.mednote.cwru.googlefit;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Device;
import com.google.android.gms.fitness.data.Field;
import com.mednote.cwru.util.helpers.TimeStampHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

public class GoogleFitDataContainer extends Observable {
    private long startTime;
    private long endTime;
    private DataType dataType;
    private List<DataPoint> dataPointList;
    private static final TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private static String currentTimeZoneString;
    private File containerFile;

    public GoogleFitDataContainer() {
        this(new ArrayList<>());
    }

    public GoogleFitDataContainer(List<DataPoint> dataPointList) {
        startTime = Long.MAX_VALUE;
        endTime = 0;
        dataType = null;
        Calendar now = GregorianCalendar.getInstance();
        currentTimeZoneString = TimeStampHelper.getTimeZone(now.getTimeZone());
        setDataPointList(dataPointList);
        containerFile = null;
    }

    public DataType getDataType() {
        if (dataType == null) {
            setDataTypeFromList();
        }
        return dataType;
    }

    public long getStartTime() {
        if (startTime == Long.MAX_VALUE) {
            setTimesFromList();
        }
        return startTime;
    }

    public long getEndTime() {
        if (endTime == 0) {
            setTimesFromList();
        }
        return endTime;
    }

    public File getContainerFile() {
        return containerFile;
    }

    public int getSize() {
        return getDataPointList().size();
    }

    public List<DataPoint> getDataPointList() {
        return dataPointList;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    protected void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    protected void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setContainerFile(File containerFile) {
        this.containerFile = containerFile;
    }

    protected void setDataPointList(List<DataPoint> dataPointList) {
        this.dataPointList = dataPointList;
        InitiateValues();
    }

    public void addDataPoint(DataPoint dataPoint) {
        this.dataPointList.add(dataPoint);
        AdjustTimes(dataPoint.getStartTime(timeUnit), dataPoint.getEndTime(timeUnit));
    }

    public void InitiateValues() {
        // SortDataPointList();
        setTimesFromList();
        setDataTypeFromList();
    }

    protected void setTimesFromList() {
        int size = dataPointList.size();
        if (size > 0) {
            startTime = dataPointList.get(0).getStartTime(timeUnit);
            endTime = dataPointList.get(size - 1).getEndTime(timeUnit);
        }
    }

    protected void setDataTypeFromList() {
        int size = dataPointList.size();
        if (size > 0) {
            dataType = dataPointList.get(0).getDataType();
        }
    }

    protected void AdjustTimes(long dpStart, long dpEnd) {
        if (dpStart < getStartTime()) {
            setStartTime(dpStart);
        }
        if (dpEnd > getEndTime()) {
            setEndTime(dpEnd);
        }
    }
}