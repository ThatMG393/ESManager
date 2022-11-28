package com.thatmg393.esmanager.utils;

import android.util.Log;
import com.thatmg393.esmanager.BuildConfig;

public class Logger {
    private final String TAG;
    
    public Logger(String TAG) {
        this.TAG = TAG;
    }
    
    public void info(String CS) {
        Log.i(TAG, CS);
    }
    
    public void verbose(String CS) {
        if (!BuildConfig.DEBUG) return;
        Log.v(TAG, CS);
    }
    
    public void warn(String CS) {
        Log.w(TAG, CS);
    }
    
    public void err(String CS) {
        Log.e(TAG, CS);
    }
}
