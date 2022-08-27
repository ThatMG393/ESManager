package com.thatmg393.esmanager.crashhandler;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import com.itsaky.utils.logsender.LogSender;
import com.thatmg393.esmanager.BuildConfig;


public class Applications extends Application {
	private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
	@Override
	public void onCreate() {
        if (BuildConfig.DEBUG) LogSender.startLogging(this);
		this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				Intent chaIntent = new Intent(getApplicationContext(), CrashLogActivity.class);
				chaIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				chaIntent.putExtra("crashlog", getStackTrace(ex));
                
				PendingIntent chaPendingInt = PendingIntent.getActivity(getApplicationContext(), 69420, chaIntent, PendingIntent.FLAG_ONE_SHOT);
                
				((AlarmManager)getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 500, chaPendingInt);
            	android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(2);
				uncaughtExceptionHandler.uncaughtException(thread, ex);
			}
		});
		super.onCreate();
	}
	private String getStackTrace(Throwable th) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		Throwable cause = th;
		while(cause != null){
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		final String stacktraceAsString = result.toString();
		printWriter.close();
		return stacktraceAsString;
	}
}
