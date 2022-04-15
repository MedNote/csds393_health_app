package com.mednote.cwru.googlefit;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableBoolean;

import com.google.android.gms.fitness.data.DataType;
import com.mednote.cwru.BR;
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
    private final ObservableBoolean responseReceived;
    private final ObservableBoolean sentToServer;

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

    // region Binding Getters
    @Bindable
    public DataType getDataType() {
        return dataType;
    }

    @Bindable
    public long getEndTime() {
        return endTime;
    }

    @Bindable
    public long getStartTime() {
        return startTime;
    }

    @Bindable
    public File getFile() {
        return file;
    }

    @Bindable
    public ObservableBoolean getResponseReceived() {
        return responseReceived;
    }

    @Bindable
    public ObservableBoolean getSentToServer() {
        return sentToServer;
    }
    // endregion

    // region Binding Setters
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
        notifyPropertyChanged(BR.dataType);
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
        notifyPropertyChanged(BR.endTime);
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        notifyPropertyChanged(BR.startTime);
    }

    public void setFile(File file) {
        this.file = file;
        notifyPropertyChanged(BR.file);
    }

    public void setResponseReceived(boolean responseReceived) {
        this.responseReceived.set(responseReceived);
        notifyPropertyChanged(BR.responseReceived);
    }

    public void setSentToServer(boolean sentToServer) {
        this.sentToServer.set(sentToServer);
        notifyPropertyChanged(BR.sentToServer);
    }
    // endregion

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