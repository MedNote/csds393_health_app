package com.mednote.cwru;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.rx2.Rx2Apollo;
import com.mednote.cwru.base.BaseActivity;
import com.mednote.cwru.base.PermissionRequestHandler;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;
import com.apollographql.apollo3.ApolloClient;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;

public class LogInActivity extends BaseActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);
      Toolbar toolbar = findViewById(R.id.main_toolbar);
      setSupportActionBar(toolbar);
      ApplicationContextHelper.getInstance().init(getApplicationContext());

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
      );
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