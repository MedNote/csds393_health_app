package com.mednote.cwru;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.api.Optional;
import com.apollographql.apollo3.rx2.Rx2Apollo;
import com.mednote.cwru.base.BaseActivity;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.databinding.ActivityLoginBinding;
import com.mednote.cwru.databinding.WearableFragmentBinding;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;
import com.apollographql.apollo3.ApolloClient;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;

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
      ApplicationContextHelper.getInstance().init(getApplicationContext());

      ApolloClient.Builder l = new ApolloClient.Builder();
      ApolloClient client = l.serverUrl("http://ec2-18-233-36-202.compute-1.amazonaws.com:4000/graphql").build();

      /*ApolloCall<RecordByUuidQuery.Data> queryCall = client.query(new RecordByUuidQuery("dalsdfasjdfsdf"));
      Single<ApolloResponse<RecordByUuidQuery.Data>> queryResponse = Rx2Apollo.single(queryCall);

      queryResponse.subscribe(new DisposableSingleObserver<ApolloResponse<RecordByUuidQuery.Data>>() {
                                 @Override
                                 public void onSuccess(@NonNull ApolloResponse<RecordByUuidQuery.Data> dataApolloResponse) {
                                    Log.d("minnie",dataApolloResponse.data.toString());
                                 }

                                 @Override
                                 public void onError(@NonNull Throwable e) {
                                    Log.d("minnie",e.getMessage());
                                 }
                              }
      );*/
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