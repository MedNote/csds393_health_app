package com.mednote.cwru;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.mednote.cwru.base.BaseActivity;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.databinding.ActivityWalletBinding;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

public class WalletActivity extends BaseActivity implements View.OnClickListener {

    private WalletViewModel walletViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWalletBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet);
        walletViewModel = new WalletViewModel();
        binding.setViewmodel(walletViewModel);


        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ApplicationContextHelper.getInstance().init(getApplicationContext());

        Button registerButton = (Button) findViewById(R.id.create_wallet_button);
        registerButton.setOnClickListener(this);
    }

    public WalletViewModel getWalletViewModel() {
        return walletViewModel;
    }

    @Override
    public PermissionRequestHandler getPermissionRequestHandler() {
        return null;
    }

    @Override
    public ActivityResultLauncher<String[]> getRequestPermissionsLauncher() {
        return null;
    }

    @Override
    public boolean shouldShowPermissionRationale(String permission) {
        return false;
    }

    @Override
    public void showPermissionRationaleUI(String permission) {

    }

    @Override
    public void permissionPreviouslyGranted(String permission) {

    }

    @Override
    public void onClick(View view) {
        int viewClicked = view.getId();
        if (viewClicked == R.id.create_wallet_button) {
            String a = "";
            // TODO: 4/17/22 connect to backend
            //            getSignUpViewModel().signup();
        }
    }

}