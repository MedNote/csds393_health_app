package com.mednote.cwru.util.data_source;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.mednote.cwru.util.helpers.ApplicationContextHelper;

import java.io.File;

public class ExternalDataSource extends DataSource {
    private final Context context;

    public ExternalDataSource(){
        super();
        this.context = ApplicationContextHelper.get();

        // Select which one is currentDir
        currentDir = getDefaultDir();
    }

    public void defaultDir() {
        currentDir = getDefaultDir();
    }

    public File getDefaultDir() {
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(context, null);
        File primaryExternalStorage = externalStorageVolumes[0];
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // if (Environment.isExternalStorageLegacy()) {
            primaryExternalStorage =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/HeartBit/");
        }*/

        if (!primaryExternalStorage.exists()) {
            primaryExternalStorage.mkdirs();
        }

        return primaryExternalStorage;
    }
}
