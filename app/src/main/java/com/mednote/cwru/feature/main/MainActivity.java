package com.mednote.cwru.feature.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mednote.cwru.MedNoteApplication;
import com.mednote.cwru.R;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    @Inject
    Retrofit retrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MedNoteApplication) getApplication()).getAppComponenet().inject(this); // todo hide this in base class


        // Voila! use injected retrofitClient...
    }
}