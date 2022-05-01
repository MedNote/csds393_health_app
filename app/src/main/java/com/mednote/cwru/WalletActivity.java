package com.mednote.cwru;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.mednote.cwru.base.BaseActivity;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.databinding.ActivityWalletBinding;
import com.mednote.cwru.ethereum.Utils;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

import org.web3j.crypto.CipherException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

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

//        Button registerButton = (Button) findViewById(R.id.create_wallet_button);
//        registerButton.setOnClickListener(this);
        // Generate Wallet
        Bundle extras = getIntent().getExtras();
        String password = null;
        if (extras != null) {
            password = extras.getString("password");
            //The key argument here must match that used in the other activity
        }
        Utils.setupBouncyCastle();
        try {
            String[] deets_generated = Utils.createWallet(this, password);
            String address_generated = deets_generated[0];
            String mnemonic = deets_generated[2];
            getWalletViewModel().setWalletId(address_generated);
            getWalletViewModel().setMnemonic(mnemonic);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        findViewById(R.id.id_tv).setOnClickListener(this);
        findViewById(R.id.mnemonic_tv).setOnClickListener(this);
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
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (viewClicked == R.id.id_tv) {
            ClipData clip = ClipData.newPlainText("wallet_id", getWalletViewModel().getWalletId());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Wallet ID copied", Toast.LENGTH_LONG).show();
        }
        if (viewClicked == R.id.mnemonic_tv) {
            ClipData clip = ClipData.newPlainText("mnemonic", getWalletViewModel().getMnemonic());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Mnemonic copied", Toast.LENGTH_LONG).show();
        }
    }

}
