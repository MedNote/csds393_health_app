package com.mednote.cwru;

import androidx.databinding.Bindable;

import com.mednote.cwru.base.BaseViewModel;

import java.util.concurrent.TimeUnit;

public class LoginViewModel extends BaseViewModel {

    private String userName;
    private String password;
    private LoginStatus loginStatus;

    public LoginViewModel() {
        userName = "";
        password = "";
        loginStatus = LoginStatus.logged_out;
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
    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        notifyPropertyChanged(BR.userName);
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
