package com.mednote.cwru.base;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mednote.cwru.util.helpers.ApplicationContextHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Context Singleton
        ApplicationContextHelper.getInstance().init(getApplicationContext());
    }

    // region Requesting Permissions

    public Map<String, Boolean> arePermissionsGranted(List<String> permissions) {
        Map<String, Boolean> isGranted = new HashMap<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED) {
                isGranted.put(permission, true);
            } else {
                isGranted.put(permission, false);
            }
        }
        return isGranted;
    }

    public List<String> getNotGrantedPermissions(List<String> permissions) {
        List<String> notGranted = new ArrayList<>();
        for (String permission : permissions) {
            if (!(ContextCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED)) {
                notGranted.add(permission);
            }
        }
        return notGranted;
    }

    public abstract PermissionRequestHandler getPermissionRequestHandler();

    public abstract ActivityResultLauncher<String[]> getRequestPermissionsLauncher();

    public void requestPermissions(List<String> permissions) {
        // You can directly ask for the permission.
        // The registered ActivityResultCallback gets the result of this request.
        getRequestPermissionsLauncher().launch(permissions.toArray(new String[permissions.size()]));
    }

    public void requestPermission(String permission, PermissionRequestHandler permissionRequestHandler) {
        if (ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            permissionPreviouslyGranted(permission);
        } else if (shouldShowPermissionRationale(permission)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            showPermissionRationaleUI(permission);
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissions(new ArrayList<String>() {{ add(permission); }});
        }
    }

    public abstract boolean shouldShowPermissionRationale(String permission);

    public abstract void showPermissionRationaleUI(String permission);

    public abstract void permissionPreviouslyGranted(String permission);


    // endregion
}