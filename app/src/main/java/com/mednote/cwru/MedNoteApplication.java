package com.mednote.cwru;


import android.app.Application;

import com.mednote.cwru.common.di.AppComponent;
import com.mednote.cwru.common.di.AppModule;
import com.mednote.cwru.common.di.DaggerAppComponent;
import com.mednote.cwru.common.di.NetworkModule;

import dagger.android.DaggerApplication;

public class MedNoteApplication extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public AppComponent getAppComponenet() {
        return component;
    }

}
