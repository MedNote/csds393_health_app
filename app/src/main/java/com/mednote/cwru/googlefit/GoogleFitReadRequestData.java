package com.mednote.cwru.googlefit;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableBoolean;

import com.google.android.gms.fitness.data.DataType;
import com.mednote.cwru.util.helpers.TimeStampHelper;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

public class GoogleFitReadRequestData extends BaseObservable {
    private DataType dataType;
    private long startTime;
    private long endTime;
    private File file;
    private ObservableBoolean responseReceived;
    private ObservableBoolean sentToServer;

    public GoogleFitReadRequestData(DataType dataType, long startTime, long endTime) {
        this.dataType = dataType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.responseReceived = new ObservableBoolean(false);
        this.sentToServer = new ObservableBoolean(false);
    }

    public GoogleFitReadRequestData(GoogleFitReadRequestData clone) {
        this.dataType = clone.dataType;
        this.startTime = clone.startTime;
        this.endTime = clone.endTime;
        this.file = clone.file;
        this.responseReceived = new ObservableBoolean(clone.responseReceived);
        this.sentToServer = new ObservableBoolean(clone.sentToServer);
    }

    public DataType getDataType() {
        return dataType;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public File getFile() {
        return file;
    }

    public ObservableBoolean getResponseReceived() {
        return responseReceived;
    }

    public ObservableBoolean getSentToServer() {
        return sentToServer;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setResponseReceived(boolean responseReceived) {
        this.responseReceived.set(responseReceived);
    }

    public void setSentToServer(boolean sentToServer) {
        this.sentToServer.set(sentToServer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoogleFitReadRequestData that = (GoogleFitReadRequestData) o;
        return startTime == that.startTime &&
                endTime == that.endTime &&
                dataType.equals(that.dataType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataType, startTime, endTime);
    }

    @Override
    public String toString() {
        Calendar now = GregorianCalendar.getInstance();
        String output =  WearableDataTypesHelper.getDataNamesMap().get(dataType) + " data" +
                /*"\nfrom " + TimeStampHelper.getTimestampUI(startTime, now.getTimeZone()) +*/
                " \nup to " + TimeStampHelper.getTimestampUI(endTime, now.getTimeZone()) + ".";
                /*" data, from " + startTime +
                " to " + endTime;*/

        if (sentToServer.get()) {
            output = "Sent to server " + output;
        } else if (responseReceived.get()) {
            output = "Retrieved " + output;
        } else {
            output = "Requesting " + output;
        }
        output += "\n";
        return output;
    }
}