package com.mednote.cwru.googlefit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCaller;
import androidx.databinding.Bindable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.mednote.cwru.BR;
import com.mednote.cwru.base.BaseViewModel;
import com.mednote.cwru.base.ObservableViewModel;
import com.mednote.cwru.util.BetterActivityResult;

public class GoogleLoginViewModel extends BaseViewModel {
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher;
    private final Context mainActivityContext;
    private GoogleSignInAccount account = null;

    private Boolean googleLoggedIn;

    String TAG = "Sign In: ";

    public GoogleLoginViewModel(Context context) {
        mainActivityContext = context;
        activityLauncher = BetterActivityResult.registerActivityForResult((ActivityResultCaller) mainActivityContext);
    }

    public GoogleSignInAccount getAccount() {
        return account;
    }

    @Bindable
    public Boolean getGoogleLoggedIn() {
        return googleLoggedIn;
    }

    public void setAccount(GoogleSignInAccount account) {
        this.account = account;
    }

    public void setGoogleLoggedIn(Boolean googleLoggedIn) {
        this.googleLoggedIn = googleLoggedIn;
        notifyPropertyChanged(BR.googleLoggedIn);
    }

    public void googleSignIn() {

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this.mainActivityContext, gso);

        if (!isSignedIn()) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            activityLauncher.launch(signInIntent, result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                handleSignInResult(task);
            });
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            setAccount(completedTask.getResult(ApiException.class));
            setGoogleLoggedIn(true);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            e.printStackTrace();
        }
    }

    private boolean isSignedIn() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        setGoogleLoggedIn(GoogleSignIn.getLastSignedInAccount(this.mainActivityContext) != null);
        return getGoogleLoggedIn();
    }
}
