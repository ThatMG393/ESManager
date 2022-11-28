package com.thatmg393.esmanager.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import androidx.annotation.NonNull;

import com.thatmg393.esmanager.Constants;
import com.thatmg393.esmanager.interfaces.IOnActivityResult;
import com.thatmg393.esmanager.interfaces.IProcessListener;

import com.thatmg393.esmanager.utils.PermissionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProcessListener {
    private Context ctx;
    private String appPkg;
    private IProcessListener ipl;
    
    private List<IProcessListener> Lipl = new ArrayList<IProcessListener>();
    
    private ProcessListenerLooper pll;
    
    public ProcessListener(@NonNull Context ctx, String appPkg) {
        this.ctx = ctx;
        this.appPkg = appPkg;
        
        pll = new ProcessListenerLooper(this);
    }
    
    public ProcessListener(@NonNull Context ctx, String appPkg, IProcessListener ipl) {
        this.ctx = ctx;
        this.appPkg = appPkg;
        this.ipl = ipl;
        
        pll = new ProcessListenerLooper(this);
    }
    
    public boolean startListening(@NonNull Intent activityIntent) {
        if (activityIntent == null) return false;
        
        ctx.startActivity(activityIntent);
        if (ipl != null) ipl.onListenerStart();
        for (IProcessListener ipl2 : Lipl) { ipl2.onListenerStart(); }
        
        System.out.println("Listening...");
        pll.loop();
        return true;
    }
    
    public void stopListening() {
        pll.stopLooping();
		
		if (ipl != null) ipl.onListenerStop();
        for (IProcessListener ipl2 : Lipl) { ipl2.onListenerStop(); }
    }
    
    public void addListener(IProcessListener ipl) {
        Lipl.add(ipl);
    }
    
    private class ProcessListenerLooper extends Thread implements IOnActivityResult {
        private static final int DELAY = 250;
        
        private ProcessListener pl;
        private ExecutorService es;
        private boolean isProcessFound;
        
        private ProcessListenerLooper(ProcessListener pl) {
            this.pl = pl;
            es = Executors.newSingleThreadExecutor();
        }
        
        public void loop() {
            if (PermissionUtils.getUsageAccessEnabled(pl.ctx) == PermissionUtils.PermissionStatus.GRANTED) {
                es.execute(this);
            } else {
                PermissionUtils.askForUsageAccess(pl.ctx, this);
            }
        }
        
        @Override
        public void run() {
            UsageStatsManager usm = (UsageStatsManager)pl.ctx.getSystemService(Context.USAGE_STATS_SERVICE);
            List<UsageStats> lUS;
            
            while (true) {
                long curTime = System.currentTimeMillis();
                lUS = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, curTime - 1000, curTime);
                if (lUS != null && lUS.size() > 0) {
                    for (UsageStats us : lUS) {
                        if (us.getPackageName().equals(Constants.ES_PKG_NAME)) {
                            if (!isProcessFound) {
                                System.out.println("App running...");
                                if (pl.ipl != null) { pl.ipl.onProcessAlive(); }
                                for (IProcessListener ipl : pl.Lipl) { ipl.onProcessAlive(); }
                                isProcessFound = true;
							}
                        } else {
                            if (isProcessFound) {
                                System.out.println("App not running");
                                if (pl.ipl != null) { pl.ipl.onProcessGone(); }
                                for (IProcessListener ipl : pl.Lipl) { ipl.onProcessGone(); }
                                isProcessFound = false;
							}
                        }
                    }
                }
                
                try { sleep(DELAY); }
                catch (InterruptedException ie) { }
            }
        }
        
        public void stopLooping() {
            if (es.isTerminated() && es.isShutdown()) es.shutdownNow();
        }
        
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == Constants.ResultCodes.PMU_usac) {
                if (PermissionUtils.getUsageAccessEnabled(pl.ctx) == PermissionUtils.PermissionStatus.GRANTED) {
                    es.execute(this);
                } else {
                    SharedPreference.getInstance().addBool(Constants.PreferenceKeys.RPC_ENABLED, false);
                }
            }
        }
    }
}