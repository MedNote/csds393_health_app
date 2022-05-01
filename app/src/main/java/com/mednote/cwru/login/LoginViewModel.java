package com.mednote.cwru.login;

import android.app.Activity;
import android.util.Log;

import androidx.databinding.Bindable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.mednote.cwru.BR;
import com.mednote.cwru.LoginStatus;
import com.mednote.cwru.base.BaseViewModel;
import com.mednote.cwru.ethereum.EHR;
import com.mednote.cwru.ethereum.Utils;
import com.mednote.cwru.login.LoginRepository;
import com.mednote.cwru.login.datasource.LoginDataSource;
import com.mednote.cwru.login.datasource.PatientLoginDataSource;
import com.mednote.cwru.login.datasource.ProviderLoginDataSource;
import com.mednote.cwru.login.exchangetypes.LoginServerResponse;
import com.mednote.cwru.login.models.AccountCredentials;
import com.mednote.cwru.login.models.ClientUser;
import com.mednote.cwru.login.models.LoggedInUser;
import com.mednote.cwru.login.models.ProviderUser;
import com.mednote.cwru.serverapi.ServerResult;
import com.mednote.cwru.util.FutureTaskWrapper;
import com.mednote.cwru.util.helpers.ExecutorServiceHelper;

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

    public void checkLoginStatus() {
        LoginRepository loginRepository = LoginRepository.getInstance(null);
        if (loginRepository.isLoggedIn()) {
            setLoginStatus(LoginStatus.logged_in);
        }
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

    public void setDoctor(Boolean doctor) {
        this.doctor = doctor;
        notifyPropertyChanged(BR.doctor);
    }

    public void setLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
        ((Activity) applicationContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                notifyPropertyChanged(BR.loginStatus);
            }
        });
    }

    public void login() {
        setLoginStatus(LoginStatus.pending);
        String address = getWalletID();
//        String password = "admin";
//        String address = "0xea06a63b68149a4e1749bbfe1ce06f8d8c762cf2";
//        String mnemonic = "hungry hair truth vapor smooth blast swear bulb camera eager invest chronic";
        Utils.setupBouncyCastle();
        AccountCredentials accountCredentials = new AccountCredentials(address, password, mnemonic);
        LoginDataSource loginDataSource = null;
        if (doctor) {
            loginDataSource = new ProviderLoginDataSource();
        } else {
            loginDataSource = new PatientLoginDataSource();
        }
        LoginRepository loginRepository = LoginRepository.getInstance(loginDataSource);
        FutureTaskWrapper<ServerResult<LoginServerResponse>> futureTask = loginRepository.login(accountCredentials);
        futureTask.addOnSuccessListener(new OnSuccessListener<ServerResult<LoginServerResponse>>() {
            @Override
            public void onSuccess(ServerResult<LoginServerResponse> loginServerResponseServerResult) {
                LoginServerResponse loginServerResponse = loginServerResponseServerResult.getResult();
                if (loginServerResponse.getUserExists()) {
                    if (loginServerResponse.getUserExists()) {
                        LoggedInUser newUser = null;
                        if (getDoctor()) {
                            newUser = new ProviderUser(loginServerResponse.getAccountCredentials());
                        } else {
                            newUser = new ClientUser(loginServerResponse.getAccountCredentials());
                        }
                        loginRepository.setLoggedInUser(newUser);
                    }
                    setLoginStatus(LoginStatus.logged_in);
                } else {
                    setLoginStatus(LoginStatus.error);
                }

            }
        });
        ExecutorServiceHelper.getInstance().execute(futureTask);

    }
}
