package com.mednote.cwru;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.api.Optional;
import com.apollographql.apollo3.rx2.Rx2Apollo;
import com.google.android.gms.common.api.Api;
import com.mednote.cwru.base.BaseActivity;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.databinding.ActivityDoctorNoteAddBinding;
import com.mednote.cwru.login.LoginRepository;
import com.mednote.cwru.login.models.AccountData;
import com.mednote.cwru.login.models.LoggedInUser;
import com.mednote.cwru.login.models.Note;
import com.mednote.cwru.type.DateTime;
import com.mednote.cwru.type.ImmunizationIn;
import com.mednote.cwru.type.NameIn;
import com.mednote.cwru.type.NoteIn;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;

public class DoctorNoteAddActivity extends BaseActivity implements View.OnClickListener {

    private DoctorNoteAddViewModel doctorNoteAddViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDoctorNoteAddBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_note_add);
        doctorNoteAddViewModel = new DoctorNoteAddViewModel();
        binding.setViewmodel(doctorNoteAddViewModel);


        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ApplicationContextHelper.getInstance().init(getApplicationContext());

        Button registerButton = (Button) findViewById(R.id.add_btn);
        registerButton.setOnClickListener(this);
    }

    public DoctorNoteAddViewModel getDoctorNoteAddViewModel() {
        return doctorNoteAddViewModel;
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
        if (viewClicked == R.id.add_btn) {

            String name = doctorNoteAddViewModel.getName();
            NameIn nameIn = new NameIn(new Optional.Present<>(name), new Optional.Present<>(name));

            String dob = doctorNoteAddViewModel.getDob();

            List<String> allergies = null;
            if (doctorNoteAddViewModel.getAllergie() != null) {
                allergies = Arrays.asList(doctorNoteAddViewModel.getAllergie().split(","));
            }
            List<String> medications = null;
            if (doctorNoteAddViewModel.getMedication() != null) {
                medications = Arrays.asList(doctorNoteAddViewModel.getMedication().split(","));
            }
            String[] immunizations = new String[0];
            if (doctorNoteAddViewModel.getImmunization() != null) {
                immunizations = doctorNoteAddViewModel.getImmunization().split(",");
            }
            ArrayList<ImmunizationIn> immunizationList = new ArrayList<ImmunizationIn>();
            LocalTime timeNow = LocalTime.now();
            for (int i = 0; i < immunizations.length; i++) {
                immunizationList.add(new ImmunizationIn(new Optional.Present<>(immunizations[i])));
            }

            String[] notes = doctorNoteAddViewModel.getNotes().split(",");
            ArrayList<NoteIn> noteList = new ArrayList<NoteIn>();
            for (int i = 0; i < notes.length; i++) {
                noteList.add(new NoteIn(new Optional.Present<>(notes[i])));
            }


            LoginRepository loginRepository = LoginRepository.getInstance(null);
            LoggedInUser loggedInUser = loginRepository.getLoggedInUser();
            AccountData accountData = loggedInUser.getAccountData();

            ApolloClient.Builder l = new ApolloClient.Builder();
            ApolloClient client = l.serverUrl("http://ec2-18-233-36-202.compute-1.amazonaws.com:4000/graphql").build();
            ApolloCall<AppendRecordMutation.Data> queryCall = client.mutation(new AppendRecordMutation(
                    loggedInUser.getUuid(),
                    new Optional.Present<>(nameIn),
//                    new Optional.Present<>(accountData.getAllergies()),
//                    new Optional.Present<>(accountData.getMedications()),
//                    new Optional.Present<>(accountData.getImmunizations()),
//                    new Optional.Present<List<AppendRecordMutation.Visit_note>>(getNotesApollo(accountData.getNotes()))
                    new Optional.Present<>(allergies),
                    new Optional.Present<>(medications),
                    new Optional.Present<>(immunizationList),
                    new Optional.Present<>(noteList)
            ));

            Single<ApolloResponse<AppendRecordMutation.Data>> queryResponse = Rx2Apollo.single(queryCall);

            queryResponse.subscribe(new DisposableSingleObserver<ApolloResponse<AppendRecordMutation.Data>>() {
                                        @Override
                                        public void onSuccess(@NonNull ApolloResponse<AppendRecordMutation.Data> dataApolloResponse) {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Note Add Successful", Toast.LENGTH_LONG).show();
                                                    finish();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Note Add Failed due to: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
            );

        }
    }


    public List<AppendRecordMutation.Visit_note> getNotesApollo(List<Note> notes) {
        List<AppendRecordMutation.Visit_note> visit_notes = new ArrayList<>();
        for (Note note : notes) {
            visit_notes.add(new AppendRecordMutation.Visit_note(note.getNote(), note.getDate()));
        }
        return visit_notes;
    }


}
