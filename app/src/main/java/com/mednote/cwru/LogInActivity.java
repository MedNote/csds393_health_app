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
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.databinding.ActivityLoginBinding;
import com.mednote.cwru.serverapi.DataRequestStatus;
import com.mednote.cwru.serverapi.ServerInteractionViewModel;

public class LogInActivity extends BaseActivity implements View.OnClickListener {

   private LoginViewModel loginViewModel;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      ActivityLoginBinding binding =  DataBindingUtil.setContentView(this, R.layout.activity_login);
      loginViewModel = new LoginViewModel();
      binding.setViewmodel(loginViewModel);

//      setContentView(R.layout.activity_login);
      Toolbar toolbar = findViewById(R.id.main_toolbar);
      setSupportActionBar(toolbar);

      Button loginButton = (Button) findViewById(R.id.proceed_to_verification_button);
      loginButton.setOnClickListener(this);

      // Add observable listeners
      getLoginViewModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
         @Override
         public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == BR.loginStatus) {
               String toastText = "Status is " + getLoginViewModel().getLoginStatus();
               Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show();
               if (getLoginViewModel().getLoginStatus() == LoginStatus.logged_in) {
                  Intent new_intent = new Intent(LogInActivity.this, MainActivity.class);
                  startActivity(new_intent);
               }
            }
         }
      });
      // Check login status
      getLoginViewModel().checkLoginStatus();
   }

   public LoginViewModel getLoginViewModel() {
      return loginViewModel;
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
         getLoginViewModel().login();
      }
   }
}