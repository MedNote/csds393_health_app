package com.mednote.cwru;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.mednote.cwru.base.BaseActivity;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.databinding.ActivityDoctorNoteViewBinding;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

public class DoctorNoteViewActivity extends BaseActivity {

    private DoctorNoteViewViewModel doctorNoteViewViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDoctorNoteViewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_doctor_note_view);
        doctorNoteViewViewModel = new DoctorNoteViewViewModel();
        binding.setViewmodel(doctorNoteViewViewModel);


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
