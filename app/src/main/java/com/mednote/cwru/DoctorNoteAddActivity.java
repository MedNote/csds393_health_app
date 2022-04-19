package com.mednote.cwru;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.mednote.cwru.base.BaseActivity;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.databinding.ActivityDoctorNoteAddBinding;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

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
            // TODO: 4/17/22 connect to backend
        }
    }

}
