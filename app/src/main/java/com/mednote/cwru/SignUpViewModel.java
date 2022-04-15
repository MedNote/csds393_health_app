package com.mednote.cwru;

import android.util.Log;

import androidx.databinding.Bindable;

import com.mednote.cwru.base.BaseViewModel;

import java.util.concurrent.TimeUnit;

public class SignUpViewModel extends BaseViewModel {

    private String userName;
    private String password;
    private String name;
    private String DOB;
    private RegistrationStatus signUpStatus;

    public SignUpViewModel() {
        userName = "";
        password = "";
        name = "";
        DOB = "";
        signUpStatus = RegistrationStatus.registered;
    }

    @Override
    protected void instantiatePermissions() {
    }

    @Bindable
    public String getUserName() {
        return userName;
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    @Bindable
    public String getAcctName() { return name; }

    @Bindable
    public String getDOB() { return DOB; }

    @Bindable
    public RegistrationStatus getSignUpStatus() {
        return signUpStatus;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        notifyPropertyChanged(BR.userName);
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public void setAcctName(String name) {
        this.name = name;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
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
            // TODO: available only to the doctor
            // TODO: generateWallet
            // TODO:

        } catch (InterruptedException e) {

            e.printStackTrace();
        }

    }
}