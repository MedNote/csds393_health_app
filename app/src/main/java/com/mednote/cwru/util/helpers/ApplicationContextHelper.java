package com.mednote.cwru.util.helpers;

import android.content.Context;

public class ApplicationContextHelper {
    private static ApplicationContextHelper instance;
    private Context mContext;

    public static ApplicationContextHelper getInstance() {
        if (instance == null) {
            /* To solve memory leak, we just need to hold the
            reference of getApplicationContext() called from any context.
             */
            instance = new ApplicationContextHelper();
        }

        return instance;
    }

    private ApplicationContextHelper(){}

    public void init(Context context){
        if(mContext == null){
            mContext = context;
        }
    }

    private ApplicationContextHelper(Context context) {
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    public static Context get() {
        return getInstance().getContext();
    }
}
