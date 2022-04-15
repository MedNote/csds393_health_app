package com.mednote.cwru.login;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.mednote.cwru.login.models.AccountCredentials;
import com.mednote.cwru.login.models.LoggedInUser;
import com.mednote.cwru.serverapi.ServerResult;
import com.mednote.cwru.util.FutureTaskWrapper;
import com.mednote.cwru.util.helpers.ApplicationContextHelper;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {
    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;
    private LoggedInUser user = null;
    private Context appContext;
//  TODO: use SecurePreferences and use Keystore to store encrypted keys
    private SharedPreferences sp;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
        // Use context singleton
        this.appContext = ApplicationContextHelper.get();
        this.sp = appContext.getSharedPreferences("UserData", Context.MODE_PRIVATE);
    }

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository(new LoginDataSource());
        }
        return instance;
    }

    public boolean isLoggedIn() {
        // return user != null;
        return sp.getBoolean("logged", false);
    }

    public LoggedInUser getLoggedInUser () {
        if (user == null) {
            // handle login
            String json = sp.getString("user", "");
            if (json.equals(""))
                return null;
            Gson gson = new Gson();
            user = gson.fromJson(json, LoggedInUser.class);
        }
        return user;
    }

    public void logout() {
        this.user = null;
        dataSource.logout();
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("user").apply();
        editor.remove("logged").apply();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("user", json).apply();
        editor.putBoolean("logged", true).apply();
    }

    public void saveUser () {
        this.setLoggedInUser(this.user);
    }

    public FutureTaskWrapper<ServerResult> login(AccountCredentials accountCredentials) {
        OnSuccessListener<ServerResult> listenerRepository = new OnSuccessListener<ServerResult>() {
            @Override
            public void onSuccess(ServerResult serverResult) {
                // TODO: finish
                onLoginResult(serverResult);
            }
        };
        // handle login
        FutureTaskWrapper<ServerResult> task = dataSource.login(accountCredentials);
        task.addOnSuccessListener(listenerRepository);
        return task;
    }

    /**
     * Interprets the serverResult
     * @param serverResult
     * @return
     */
    public LoggedInUser onLoginResult(ServerResult serverResult) {
        return null;
    }
}