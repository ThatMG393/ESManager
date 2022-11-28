package com.thatmg393.esmanager.rpc;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thatmg393.esmanager.BuildConfig;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.rpc.DiscordRPC;
import com.thatmg393.esmanager.utils.Logger;
import com.thatmg393.esmanager.utils.SharedPreference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.net.URISyntaxException;

public class DiscordRPCService extends Service {
    private final Logger LOG = new Logger("ESManager/DiscordRPCService");
    
	protected final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	
	protected String authToken;
    protected String accStatus;
    protected boolean canStart;
	
	protected RPCBinder rpcb = new RPCBinder();
	protected DiscordSocketClient webSocketClient;
	protected Thread wsThread;
	
	private NotificationCompat.Builder builder;
    private NotificationCompat notification;
	private NotificationManager manager;
	
	@Override
    public IBinder onBind(Intent binder) {
        DiscordRPC.getInstance().serviceBinded = true;
        
        updateNotif("Service started...");
        
		return rpcb;
    }
    
    public boolean isShuttingDown;
    
    @Override
    public boolean onUnbind(Intent smth) {
        if (!isShuttingDown) {
            isShuttingDown = true;
            
            DiscordRPC.getInstance().serviceBinded = false;
            try { unregisterReceiver(br); }
            catch (IllegalArgumentException iae) {}
            getManager().cancel(1);
			if (!wsThread.isInterrupted()) wsThread.interrupt();
			if (!webSocketClient.stopWebsocket()) { webSocketClient.stopWebsocket(); }
            stopSelf();
            
            isShuttingDown = false;
        }
        
        return true;
    }
    
    protected BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent data) {
            LOG.verbose("Killing service...");
            DiscordRPC.getInstance().stopRPCService();
        }
    };
        
    
	@Override
	public void onCreate() {
        registerReceiver(br, new IntentFilter(getPackageName() + ".KillRPC"));
		wsThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					webSocketClient = new DiscordSocketClient(DiscordRPCService.this);
					webSocketClient.connect();
				} catch (URISyntaxException urise) { }
			};
		});
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel nChannel = new NotificationChannel(
                "RichPresenceService",
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_MIN);
                
            nChannel.setDescription("Discord Rich Presence service notification, this will let you know if Rich Presence service is running. You can turn it off in the app settings.");
            nChannel.setVibrationPattern(new long[]{ 0, 1000, 500, 1000});
            nChannel.enableVibration(true);
            
            getManager().createNotificationChannel(nChannel);

            builder = new NotificationCompat.Builder(this, "RichPresenceService");
        } else {
            builder = new NotificationCompat.Builder(this);
            builder.setPriority(NotificationCompat.PRIORITY_LOW);
        }
        
        int pFlag = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S_V2) ? PendingIntent.FLAG_IMMUTABLE : 0;
        PendingIntent ksiPending = PendingIntent.getBroadcast(this, 1, new Intent(getPackageName() + ".KillRPC"), pFlag);
        NotificationCompat.Action killServiceAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher_round, "Stop service", ksiPending)
                                                                    .setAllowGeneratedReplies(false)
                                                                    .setAuthenticationRequired(false)
                                                                    .build();
        
        builder.setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Discord Rich Presence")
            .setContentText("Discord Rich Presence service is running")
            .addAction(killServiceAction)
            .setWhen(System.currentTimeMillis())
            .setOngoing(true)
            .setAutoCancel(true);
            
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
            builder.setAllowSystemGeneratedContextualActions(false);
            
		startForeground(1, builder.build());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
        super.onDestroy();
	}
    
    private void startWSC() {
        updateNotif("Starting WebSocketClient...");
		wsThread.start();
    }
    
    public void showLoginDiag() {
		updateNotif("Logging in...");
        
        String tmp = SharedPreference.getInstance().getString("uname");
		if (tmp != null) {
            updateNotif("Logged in.");
            
			authToken = tmp;
            startWSC();
			return;
		}
        
		AlertDialog ad = new AlertDialog.Builder(this).create();
		LayoutInflater li = LayoutInflater.from(getApplicationContext());
		View lv = li.inflate(R.layout.rpc_login, null);
		
		WebView wv = lv.findViewById(R.id.rcp_webview);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setDatabaseEnabled(true);
		wv.getSettings().setDomStorageEnabled(true);
		
		wv.setWebViewClient(new WebViewClient() {
			@SuppressWarnings("deprecation")
			@Override
			public boolean shouldOverrideUrlLoading(WebView v, String url) {
				if (url.endsWith("/app")) {
                    updateNotif("Logged in...");
					authToken = extractToken();
					SharedPreference.getInstance().addString("uname", authToken);
                    
					ad.dismiss();
                    startWSC();
				}
				return true;
			}
		});
		
		ad.setButton(DialogInterface.BUTTON_NEGATIVE, "Sign in later", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface di, int idk) {
				di.dismiss();
                DiscordRPC.getInstance().stopRPCService();
			}
		});

		ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
                DiscordRPC.getInstance().stopRPCService();
			}
		});

		ad.setView(lv);
        
        int LAYOUT_FLAG = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
		ad.getWindow().setType(LAYOUT_FLAG);
		ad.show();
        
        wv.loadUrl("https://discord.com/login");
        updateNotif("Waiting for user to log-in...");
	}
    
    private String processImageLink(String link) {
        if (link.isEmpty()) return null;
        if (link.contains("://")) link = link.split("://")[1];
        if (link.startsWith("media.discordapp.net/")) return link.replace("media.discordapp.net/", "mp:");
        else if (link.startsWith("cdn.discordapp.com")) {
            // Trick: allow using CDN URL for custom image
            // https://cdn.discordapp.com/app-assets/application-id/../../whatever.png_or_gif#.png
            // ".." resolves to the parent directory
            // "#" at the end to exclude ".png" from the link
            // so it becomes
            // https://cdn.discordapp.com/whatever.png_or_gif
            return link.replace("cdn.discordapp.com/", "../../") + "#";
        }
        return link;
    }
	
	public void sendPresence() {
		if (webSocketClient == null || !webSocketClient.isReady) {
            
            return;
        }
        
        long current = System.currentTimeMillis();

        ArrayMap<String, Object> presence = new ArrayMap<>();
        
        ArrayMap<String, Object> activity = new ArrayMap<>();
        activity.put("name", "Test Name");
        activity.put("state", "Test state");
        activity.put("details", "Test Details");
        activity.put("type", 0);
        activity.put("application_id", "956735773716123659");
        
        // Images
        ArrayMap<String, Object> assets = new ArrayMap<>();
        assets.put("large_image", processImageLink("https://cdn.discordapp.com/attachments/927768438045282354/1039714834113105950/Screenshot_20221105-180841_Call_of_Duty.jpg"));
        assets.put("small_image", processImageLink("https://cdn.discordapp.com/attachments/927768438045282354/1039714834113105950/Screenshot_20221105-180841_Call_of_Duty.jpg"));
        activity.put("assets", assets);

        // Buttons
        ArrayMap<String, Object> button = new ArrayMap<>();
        button.put("label", "Test button 1");
        button.put("url", "https://github.com");
        activity.put("buttons", button);

        ArrayMap<String, Object> timestamps = new ArrayMap<>();
        timestamps.put("start", current);
        activity.put("timestamps", timestamps);

        presence.put("activities", new Object[] {activity});
        presence.put("afk", true);
        presence.put("since", current);
        presence.put("status", accStatus);

        ArrayMap<String, Object> arr = new ArrayMap<>();
        arr.put("op", 3);
        arr.put("d", presence);

        webSocketClient.send(GSON.toJson(arr));
        System.out.println(GSON.toJson(arr));
    }
	
	protected void updateNotif(String s) {
        if (!BuildConfig.DEBUG) return;
		if (!DiscordRPC.getInstance().serviceBinded) return;
        
        builder.setContentText(s);
        getManager().notify(1, builder.build());
        LOG.verbose(s);
    }
	
	protected NotificationManager getManager() {
        if (manager == null) 
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }
    
    private String extractToken() {
        try {
            File f = new File(getFilesDir().getParentFile(), "app_webview/Default/Local Storage/leveldb");
            File[] fArr = f.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File file, String name) {
						return name.endsWith(".log");
					}
				});
            if (fArr.length == 0) return null;
            f = fArr[0];
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("token")) break;
            }
            line = line.substring(line.indexOf("token") + 5);
            line = line.substring(line.indexOf("\"") + 1);
            
            return line.substring(0, line.indexOf("\""));
        } catch (Throwable e) {
            return null;
        }
    }
	
	public class RPCBinder extends Binder {
        public DiscordRPCService getService() {
            return DiscordRPCService.this;
        }
    }
}
