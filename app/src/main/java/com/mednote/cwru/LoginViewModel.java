package com.mednote.cwru;

import androidx.databinding.Bindable;

import com.mednote.cwru.base.BaseViewModel;

import java.util.concurrent.TimeUnit;

public class LoginViewModel extends BaseViewModel {

    private String WalletID;
    private String mnemonic;
    private String password;
    private Boolean doctor;
    private LoginStatus loginStatus;

    public LoginViewModel() {
        WalletID = "";
        mnemonic = "";
        password = "";
        doctor = false;
        loginStatus = LoginStatus.logged_out;
    }

    @Override
    protected void instantiatePermissions() {

    }

    @Bindable
    public String getWalletID() {
        return WalletID;
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    @Bindable
    public String getMnemonic() {
        return mnemonic;
    }

    @Bindable
    public Boolean getDoctor() {
        return doctor;
    }

    @Bindable
    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public void setWalletID(String ID) {
        this.WalletID = ID;
        notifyPropertyChanged(BR.walletID);
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
        notifyPropertyChanged(BR.mnemonic);
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public void setLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
        notifyPropertyChanged(BR.loginStatus);
    }

    public void login() {
        setLoginStatus(LoginStatus.pending);
        try {
//        TODO: finish login with blockchain
            TimeUnit.SECONDS.sleep(2);
            setLoginStatus(LoginStatus.logged_in);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
