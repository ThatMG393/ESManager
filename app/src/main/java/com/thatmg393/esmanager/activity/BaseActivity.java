package com.thatmg393.esmanager.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.itsaky.utils.logsender.LogSender;
import com.thatmg393.esmanager.BuildConfig;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class BaseActivity extends AppCompatActivity {
    
    private Thread.UncaughtExceptionHandler thrUEH;
    @Override
    protected void onStart() {
        if (thrUEH == null) setUEH();
        super.onStart();
    }
    
    private void setUEH() {
        thrUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread curThr, Throwable ex) {
                Intent caInt = new Intent(getApplicationContext(), CrashActivity.class);
                caInt.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                caInt.putExtra("exceptionOverview", ex.getClass().getName() + " : " + ex.getMessage());
                caInt.putExtra("exceptionStackTrace", getThrowableStackTrace(ex));
                
                PendingIntent pdInt = PendingIntent.getActivity(getApplicationContext(), 69, caInt, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager aMan = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                aMan.set(AlarmManager.RTC_WAKEUP, 10, pdInt);
                finishAffinity();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(2);
                
                thrUEH.uncaughtException(curThr, ex);
            }
        });
    }
    
    private String getThrowableStackTrace(final Throwable err) {
        final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
        
		try {
			Throwable cause = err;
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
        
			printWriter.close();
        	result.close();
        } catch (IOException ioe) {}
        
		return result.toString();
	}
    
    public final void startLogging() {
        if (BuildConfig.DEBUG) LogSender.startLogging(this);
    }
}
