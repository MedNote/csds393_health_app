package com.mednote.cwru.common.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final Application medNoteApplication;

    public AppModule(Application application) {
        this.medNoteApplication = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return medNoteApplication;
    }
}
