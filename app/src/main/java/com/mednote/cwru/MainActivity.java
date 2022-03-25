package com.mednote.cwru;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import com.mednote.cwru.base.BaseActivity;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.googlefit.GoogleLoginViewModel;
import com.mednote.cwru.googlefit.WearableViewModel;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

public class MainActivity extends BaseActivity {

    private WearableViewModel wearableViewModel;
    private GoogleLoginViewModel googleLoginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Initialize Context Singleton
        ApplicationContextHelper.getInstance().init(getApplicationContext());

        wearableViewModel = new WearableViewModel(this);
        googleLoginViewModel = new GoogleLoginViewModel(this);
        // TODO: Handle permissions of every viewModel (including default one)
    }

    public WearableViewModel getWearableViewModel() {
        return wearableViewModel;
    }

    public GoogleLoginViewModel getGoogleLoginViewModel() {
        return googleLoginViewModel;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        getWearableViewModel().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}