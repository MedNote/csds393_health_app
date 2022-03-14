package com.mednote.cwru.util.data_source;

import android.content.Context;

import com.mednote.cwru.util.helpers.ApplicationContextHelper;

import java.io.File;

public class InternalDataSource extends DataSource {
    private final Context context;

    public InternalDataSource(){
        super();
        this.context = ApplicationContextHelper.get();
        currentDir = getDefaultDir();
    }

    public void defaultDir() {
        currentDir = getDefaultDir();
    }

    public File getDefaultDir() {
        return context.getFilesDir();
    }
}
