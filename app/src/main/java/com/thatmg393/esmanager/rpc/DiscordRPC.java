package com.thatmg393.esmanager.rpc;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.thatmg393.esmanager.Constants;
import com.thatmg393.esmanager.MainActivity;
import com.thatmg393.esmanager.Utils;
import com.thatmg393.esmanager.models.DiscordProfile;
import com.thatmg393.esmanager.interfaces.IOnActivityResult;
import com.thatmg393.esmanager.interfaces.IRPCListener;

import com.thatmg393.esmanager.rpc.DiscordRPCService;
import com.thatmg393.esmanager.utils.Logger;
import com.thatmg393.esmanager.utils.NetworkUtils;
import com.thatmg393.esmanager.utils.SharedPreference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DiscordRPC implements ServiceConnection, IOnActivityResult {
    private final Logger LOG = new Logger("ESManager/DiscordRPC");
    
    private static volatile DiscordRPC instance;
    private volatile Application app;
    
    public static DiscordRPC getInstance() {
        if (instance == null) instance = new DiscordRPC();
		if (instance.app == null) throw new IllegalStateException("You must call getInstance(Context) first!");
		return instance;
    }
    
    public static DiscordRPC initializeInstance(Application app) {
        if (instance == null) instance = new DiscordRPC();
        if (instance.app == null) instance.app = app;
        instance.rpcServIntent = new Intent(app, DiscordRPCService.class);
        return instance;
    }
    
    public Intent rpcServIntent;
	public DiscordProfile dp;

    private DiscordRPC() {
        // Prevent from the Reflection API.
        if (instance != null)
			throw new RuntimeException("Use getInstance(Context) method to get a instance of this class.");
    }
	
	protected DiscordRPCService serviceInstance;
	public DiscordRPCService getServiceInstance() {
		return serviceInstance;
	}
	
	private List<IRPCListener> Lrpcl = new ArrayList<IRPCListener>();
	public void addListener(IRPCListener rpcl) {
		Lrpcl.add(rpcl);
	}
	public List<IRPCListener> getListeners() {
		return Lrpcl;
	}
    
	public boolean serviceBinded;
	public void startRPCService() {
        LOG.verbose("Starting RPC service...");
        
        if (NetworkUtils.isConnectedToInternet(app)
		 && !isServiceRunning(app) && !serviceBinded) {
            app.bindService(rpcServIntent, this, Context.BIND_AUTO_CREATE);
		} else {
			Toast.makeText(app, "Cannot start RPC Service.", Toast.LENGTH_LONG).show();
		}
	}
	
	public void stopRPCService() {
        LOG.verbose("Stopping RPC service...");
		if (isServiceRunning(app)
		 && serviceBinded 
		 && !serviceInstance.isShuttingDown) { app.unbindService(this); }
			
        SharedPreference.getInstance().addBool(Constants.PreferenceKeys.RPC_ENABLED, false);
	}
	
	@Override
    public void onServiceConnected(ComponentName componentName, IBinder binder) {
		serviceInstance = ((DiscordRPCService.RPCBinder)binder).getService();
		serviceInstance.updateNotif("Connected to service...");
		
		serviceInstance.updateNotif("Checking for permission...");
		openOverlaySettings();
    }
    
    private void openOverlaySettings() {
        if (!Settings.canDrawOverlays(serviceInstance)) {
            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            i.setData(Uri.parse("package:" + serviceInstance.getPackageName()));
            MainActivity.getInstance().launchActivityForResult(i, Constants.ResultCodes.RPC_ovlac, this);
        } else {
            serviceInstance.showLoginDiag();
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ResultCodes.RPC_ovlac) {
            if (Settings.canDrawOverlays(app)) {
                serviceInstance.showLoginDiag();
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
		System.out.println("Service disconnected...");
	}
	
    public boolean isServiceRunning(Context app) {
        return Utils.ServiceUtils.isServiceRunning(app, DiscordRPCService.class);
    }
}