package com.mednote.cwru;

import androidx.databinding.Bindable;
import androidx.lifecycle.ViewModel;

import com.mednote.cwru.base.BaseViewModel;

import java.util.UUID;

public class UserViewModel extends BaseViewModel {
    private String uuid;
    private String name;
    private String dob;


    public UserViewModel() {
        uuid = ""; //UUID.randomUUID().toString();
        name = "Oleksii Fedorenko";
        dob = "2000/06/17";
    }

    @Bindable
    public String getUuid() {
        return uuid;
    }

    @Bindable
    public String getName() {
        return name;
    }

    @Bindable
    public String getDob() {
        return dob;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
        notifyPropertyChanged(BR.uuid);
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public void setDob(String dob) {
        this.dob = dob;
        notifyPropertyChanged(BR.dob);
    }

    @Override
    protected void instantiatePermissions() {

    }
}