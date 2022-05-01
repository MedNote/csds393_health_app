package com.mednote.cwru;

import androidx.databinding.Bindable;

import com.mednote.cwru.base.BaseViewModel;

public class WalletViewModel extends BaseViewModel  {

    private String walletId;
    private String fname;
    private String lname;
    private String rsaKey;
    private String mnemonic;

    public WalletViewModel() {
        walletId = "";
        fname = "";
        lname = "";
        rsaKey = "";
    }

    @Override
    protected void instantiatePermissions() {
    }

    @Bindable
    public String getWalletId() {
        return walletId;
    }

    @Bindable
    public String getFname() { return fname; }

    @Bindable
    public String getLname() { return lname; }

    @Bindable
    public String getRsaKey() {
        return rsaKey;
    }

    @Bindable
    public String getMnemonic() {
        return mnemonic;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
        notifyPropertyChanged(BR.walletId);
    }

    public void setFname(String fname) {
        this.fname = fname;
        notifyPropertyChanged(BR.fname);
    }

    public void setLname(String lname) {
        this.lname = lname;
        notifyPropertyChanged(BR.lname);
    }

    public void setRsaKey(String rsaKey) {
        this.rsaKey = rsaKey;
        notifyPropertyChanged(BR.rsaKey);
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
        notifyPropertyChanged(BR.mnemonic);
    }
}
