package com.mednote.cwru;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;

import com.mednote.cwru.base.BaseActivity;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.databinding.ActivityDoctorNoteViewBinding;
import com.mednote.cwru.login.LoginRepository;
import com.mednote.cwru.login.models.AccountData;
import com.mednote.cwru.login.models.LoggedInUser;
import com.mednote.cwru.login.models.Note;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

import java.util.ArrayList;
import java.util.List;

public class DoctorNoteViewActivity extends BaseActivity {

    private DoctorNoteViewViewModel doctorNoteViewViewModel;
    private LoggedInUser loggedInUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDoctorNoteViewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_note_view);
        doctorNoteViewViewModel = new DoctorNoteViewViewModel();
        binding.setViewmodel(doctorNoteViewViewModel);
        View tester = binding.getRoot();


        /*ApolloClient.Builder l = new ApolloClient.Builder();
        ApolloClient client = l.serverUrl("http://ec2-18-233-36-202.compute-1.amazonaws.com:4000/graphql").build();

        ApolloCall<RecordByUuidQuery.Data> queryCall = client.query(new RecordByUuidQuery("111-111"));
        Single<ApolloResponse<RecordByUuidQuery.Data>> queryResponse = Rx2Apollo.single(queryCall);

        queryResponse.subscribe(new DisposableSingleObserver<ApolloResponse<RecordByUuidQuery.Data>>() {
                                    @Override
                                    public void onSuccess(@NonNull ApolloResponse<RecordByUuidQuery.Data> dataApolloResponse) {
                                        doctorNoteViewViewModel.setName(dataApolloResponse.data.record_by_uuid.name.toString());
                                        doctorNoteViewViewModel.setDob(dataApolloResponse.data.record_by_uuid.dob.toString());
                                        doctorNoteViewViewModel.setAllergie(String.join(",", dataApolloResponse.data.record_by_uuid.allergies));
//                                        doctorNoteViewViewModel.setNotes(dataApolloResponse.data.record_by_uuid.visit_notes.toString());
                                        List<String> notes = new ArrayList<>();
                                        for (RecordByUuidQuery.Visit_note visit_note : dataApolloResponse.data.record_by_uuid.visit_notes) {
                                            notes.add(visit_note.note);
                                        }
                                        doctorNoteViewViewModel.setNotes(notes);
                                        doctorNoteViewViewModel.setImmunization(dataApolloResponse.data.record_by_uuid.immunizations.toString());
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.d("error",e.getMessage());
                                    }
                                }
        );*/

        // Updating UI
        LoginRepository loginRepository = LoginRepository.getInstance(null);
        loggedInUser = loginRepository.getLoggedInUser();
        AccountData accountData = loggedInUser.getAccountData();
        doctorNoteViewViewModel.setName(accountData.getName().toString());
        doctorNoteViewViewModel.setDob(accountData.getDob().toString());
        doctorNoteViewViewModel.setMedication(String.join(",\n", accountData.getMedications().toString()));
        doctorNoteViewViewModel.setAllergie(String.join(",", accountData.getAllergies()));
//                                        doctorNoteViewViewModel.setNotes(dataApolloResponse.data.record_by_uuid.visit_notes.toString());
        List<String> notes = new ArrayList<>();
        for (Note visit_note : accountData.getNotes()) {
            notes.add(visit_note.getNote());
        }
        doctorNoteViewViewModel.setNotes(notes);
        doctorNoteViewViewModel.setImmunization(String.join(",\n", accountData.getImmunizations().toString()));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, getDoctorNoteViewViewModel().getNotes());
        doctorNoteViewViewModel.setNotesAdapter(arrayAdapter);
        ListView listView = (ListView) findViewById(R.id.notes_tv);
        listView.setAdapter(arrayAdapter);

        getDoctorNoteViewViewModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.notes) {
                    arrayAdapter.addAll(getDoctorNoteViewViewModel().getNotes());
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

        // Toolbar view
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);


    }

    public LoggedInUser getLoggedInUser() {
        return loggedInUser;
    }

    public DoctorNoteViewViewModel getDoctorNoteViewViewModel() {
        return doctorNoteViewViewModel;
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
}
