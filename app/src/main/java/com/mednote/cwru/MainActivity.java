package com.mednote.cwru;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.Observable;

import android.content.Intent;
import android.os.Bundle;

import com.mednote.cwru.base.BaseActivity;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.googlefit.GoogleLoginViewModel;
import com.mednote.cwru.googlefit.WearableViewModel;
import com.mednote.cwru.login.LoginRepository;
import com.mednote.cwru.login.models.LoggedInUser;
import com.mednote.cwru.serverapi.DataRequestStatus;
import com.mednote.cwru.serverapi.ServerInteractionViewModel;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

public class MainActivity extends BaseActivity {

    private WearableViewModel wearableViewModel;
    private GoogleLoginViewModel googleLoginViewModel;
    private ServerInteractionViewModel serverInteractionViewModel;
    private LoggedInUser loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        wearableViewModel = new WearableViewModel(this);
        googleLoginViewModel = new GoogleLoginViewModel(this);
        // TODO: Handle permissions of every viewModel (including default one)

        // Request Data
        LoginRepository loginRepository = LoginRepository.getInstance(null);
        loggedInUser = loginRepository.getLoggedInUser();
        serverInteractionViewModel = new ServerInteractionViewModel(getLoggedInUser(), getLoggedInUser().getUuid());

        getServerInteractionViewModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.keyRequestStatus) {
                    if (getServerInteractionViewModel().getKeyRequestStatus() == DataRequestStatus.data_received) {
                        getServerInteractionViewModel().getDataFromServer();
                    }
                }
                if (propertyId == BR.dataRequestStatus) {
                    if (getServerInteractionViewModel().getDataRequestStatus() == DataRequestStatus.data_received) {
                        // TODO: update UI
                    }
                }
            }
        });

        // Request symmetric key
        if (loggedInUser.getSymmetricKey() == null) {
            getServerInteractionViewModel().getSymmetricKey();
        }

    }

    public WearableViewModel getWearableViewModel() {
        return wearableViewModel;
    }

    public GoogleLoginViewModel getGoogleLoginViewModel() {
        return googleLoginViewModel;
    }

    public ServerInteractionViewModel getServerInteractionViewModel() {
        return serverInteractionViewModel;
    }

    public LoggedInUser getLoggedInUser() {
        return loggedInUser;
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