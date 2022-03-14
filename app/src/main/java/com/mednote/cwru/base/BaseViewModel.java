package com.mednote.cwru.base;

import android.content.Context;

import androidx.databinding.ObservableBoolean;


import com.mednote.cwru.util.helpers.ApplicationContextHelper;

import java.util.Map;

public abstract class BaseViewModel extends ObservableViewModel {

    protected Context applicationContext;
    // region Permissions
    protected Map<String, ObservableBoolean> requiredPermissions;
    protected Map<String, ObservableBoolean> recommendedPermissions;

    public Map<String, ObservableBoolean> getRequiredPermissions() {
        return requiredPermissions;
    }

    public Map<String, ObservableBoolean> getRecommendedPermissions() {
        return recommendedPermissions;
    }

    public void setRecommendedPermission(String key, Boolean value) {
        if (this.recommendedPermissions.containsKey(key)) {
            ObservableBoolean observableBoolean = this.recommendedPermissions.get(key);
            assert observableBoolean != null;
            observableBoolean.set(value);
        }
    }

    public void setRequiredPermissions(String key, Boolean value) {
        if (this.requiredPermissions.containsKey(key)) {
            ObservableBoolean observableBoolean = this.requiredPermissions.get(key);
            assert observableBoolean != null;
            observableBoolean.set(value);
        }
    }

    protected abstract void instantiatePermissions();
    // endregion

    public BaseViewModel() {
        this.applicationContext = ApplicationContextHelper.get();
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }
}
