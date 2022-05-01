package com.mednote.cwru;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;

import com.mednote.cwru.base.BaseActivity;
import com.apollographql.apollo3.*;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.databinding.ActivitySignupBinding;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

public class SignUpActivity extends BaseActivity implements View.OnClickListener  {

    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ActivitySignupBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        signUpViewModel = new SignUpViewModel();
        binding.setViewmodel(signUpViewModel);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ApplicationContextHelper.getInstance().init(getApplicationContext());

        Button registerButton = (Button) findViewById(R.id.editview);
        registerButton.setOnClickListener(this);

        // Add observable listeners
        getSignUpViewModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.signUpStatus) {
                    String toastText = "Registration is " + getSignUpViewModel().getSignUpStatus();
                    Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
                    if (getSignUpViewModel().getSignUpStatus() == RegistrationStatus.registered) {
                        Intent new_intent = new Intent(SignUpActivity.this, WalletActivity.class);
                        new_intent.putExtra("password",getSignUpViewModel().getPassword());
                        startActivity(new_intent);
                    }
                }
            }
        });



        ApolloClient.Builder l = new ApolloClient.Builder();
        ApolloClient client = l.serverUrl("http://ec2-18-233-36-202.compute-1.amazonaws.com:4000/graphql").build();
    }

    public SignUpViewModel getSignUpViewModel() {
        return signUpViewModel;
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
        if (viewClicked == R.id.editview) {
            getSignUpViewModel().signup();
        }
    }
}