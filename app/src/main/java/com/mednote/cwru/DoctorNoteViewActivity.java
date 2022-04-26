package com.mednote.cwru;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.rx2.Rx2Apollo;
import com.mednote.cwru.base.BaseActivity;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.databinding.ActivityDoctorNoteViewBinding;
import com.mednote.cwru.databinding.UserFragmentBinding;
import com.mednote.cwru.type.NoteIn;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;

public class DoctorNoteViewActivity extends BaseActivity {

    private DoctorNoteViewViewModel doctorNoteViewViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDoctorNoteViewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_note_view);
        doctorNoteViewViewModel = new DoctorNoteViewViewModel();
        binding.setViewmodel(doctorNoteViewViewModel);
        View tester = binding.getRoot();

        ApolloClient.Builder l = new ApolloClient.Builder();
        ApolloClient client = l.serverUrl("http://ec2-18-233-36-202.compute-1.amazonaws.com:4000/graphql").build();

        ApolloCall<RecordByUuidQuery.Data> queryCall = client.query(new RecordByUuidQuery("111-111"));
        Single<ApolloResponse<RecordByUuidQuery.Data>> queryResponse = Rx2Apollo.single(queryCall);

        queryResponse.subscribe(new DisposableSingleObserver<ApolloResponse<RecordByUuidQuery.Data>>() {
                                    @Override
                                    public void onSuccess(@NonNull ApolloResponse<RecordByUuidQuery.Data> dataApolloResponse) {
                                        doctorNoteViewViewModel.setName(dataApolloResponse.data.record_by_uuid.name.toString());
                                        doctorNoteViewViewModel.setDob(dataApolloResponse.data.record_by_uuid.dob.toString());
                                        doctorNoteViewViewModel.setAllergie(String.join(",", dataApolloResponse.data.record_by_uuid.allergies));
                                        doctorNoteViewViewModel.setNotes(dataApolloResponse.data.record_by_uuid.visit_notes.toString());
                                        doctorNoteViewViewModel.setImmunization(dataApolloResponse.data.record_by_uuid.immunizations.toString());
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.d("error",e.getMessage());
                                    }
                                }
        );
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ApplicationContextHelper.getInstance().init(getApplicationContext());
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
