package com.mednote.cwru;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.Toolbar;

import com.mednote.cwru.base.BaseActivity;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;
import com.apollographql.apollo3.ApolloClient;

public class LogInActivity extends BaseActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);
      Toolbar toolbar = findViewById(R.id.main_toolbar);
      setSupportActionBar(toolbar);
      ApplicationContextHelper.getInstance().init(getApplicationContext());

      ApolloClient.Builder l = new ApolloClient.Builder();
      ApolloClient c = l.serverUrl("http://ec2-18-233-36-202.compute-1.amazonaws.com:4000/graphql").build();
   //problema      c.query(new LaunchListQuery()).execute(new Com);

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