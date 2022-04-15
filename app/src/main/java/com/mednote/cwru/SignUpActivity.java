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
//import com.apollographql.apollo3.rx2.Rx2Apollo;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.databinding.ActivitySignupBinding;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

//import io.reactivex.Single;
//import io.reactivex.observers.DisposableSingleObserver;

public class SignUpActivity extends BaseActivity implements View.OnClickListener  {

    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_signup);
        ActivitySignupBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        signUpViewModel = new SignUpViewModel();
        binding.setViewmodel(signUpViewModel);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ApplicationContextHelper.getInstance().init(getApplicationContext());

        Button registerButton = (Button) findViewById(R.id.proceed_to_verification_button);
        registerButton.setOnClickListener(this);

        // Add observable listeners
        getSignUpViewModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.signUpStatus) {
                    String toastText = "Registration is " + getSignUpViewModel().getSignUpStatus();
                    Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
                    if (getSignUpViewModel().getSignUpStatus() == RegistrationStatus.registered) {
                        Intent new_intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(new_intent);
                    }
                }
            }
        });
        /*

        ApolloClient.Builder l = new ApolloClient.Builder();
        ApolloClient client = l.serverUrl("http://ec2-18-233-36-202.compute-1.amazonaws.com:4000/graphql").build();


        ApolloCall<LaunchListQuery.Data> queryCall = client.query(new LaunchListQuery());
        Single<ApolloResponse<LaunchListQuery.Data>> queryResponse = Rx2Apollo.single(queryCall);

        queryResponse.subscribe(new DisposableSingleObserver<ApolloResponse<LaunchListQuery.Data>>() {
                                    @Override
                                    public void onSuccess(@NonNull ApolloResponse<LaunchListQuery.Data> dataApolloResponse) {
                                        Log.d("minnie",dataApolloResponse.data.toString());
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.d("minnie",e.getMessage());
                                    }
                                }
        ); */
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
        if (viewClicked == R.id.proceed_to_verification_button) {
            getSignUpViewModel().signup();
        }
    }
}