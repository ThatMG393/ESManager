package com.thatmg393.esmanager.lua;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LSPServer extends Service {
	@Override
	public IBinder onBind(Intent binder) {
		return null;
	}
	
	@Override
	public void onCreate() {
	    super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    return START_REDELIVER_INTENT;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
