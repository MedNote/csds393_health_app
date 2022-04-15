package com.mednote.cwru;

import androidx.databinding.Bindable;

import com.mednote.cwru.base.BaseViewModel;

import java.util.concurrent.TimeUnit;

public class SignUpViewModel extends BaseViewModel {

    private String fname;
    private String lname;
    private String password;
    private String DOB;
    private Boolean doctor;
    private RegistrationStatus signUpStatus;

    public SignUpViewModel() {
        fname = "";
        lname = "";
        password = "";
        DOB = "";
        doctor = false;
        signUpStatus = RegistrationStatus.logged_out;
    }

    @Override
    protected void instantiatePermissions() {
    }

    @Bindable
    public String getFname() { return fname; }

    @Bindable
    public String getLname() { return lname; }

    @Bindable
    public String getPassword() {
        return password;
    }

    @Bindable
    public String getDOB() { return DOB; }

    @Bindable
    public RegistrationStatus getSignUpStatus() {
        return signUpStatus;
    }

    public void setFname(String fname) {
        this.fname = fname;
        notifyPropertyChanged(BR.fname);
    }

    public void setLname(String lname) {
        this.lname = lname;
        notifyPropertyChanged(BR.lname);
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
        notifyPropertyChanged(BR.dOB);
    }

    public void setSignUpStatus(RegistrationStatus signUpStatus) {
        this.signUpStatus = signUpStatus;
        notifyPropertyChanged(BR.signUpStatus);
    }

    public void signup() {
        setSignUpStatus(RegistrationStatus.registering);
        try {
            TimeUnit.SECONDS.sleep(2);
            setSignUpStatus(RegistrationStatus.registered);
//        TODO: finish registration with blockchain

        } catch (InterruptedException e) {

            e.printStackTrace();
        }

    }
}