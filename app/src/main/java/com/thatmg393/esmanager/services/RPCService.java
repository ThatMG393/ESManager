package com.thatmg393.esmanager.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.ArrayMap;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thatmg393.esmanager.MainActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Map;


public class RPCService extends Service
{
    private static WebSocketClient webSocketClient;
	private static Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	
	private static Runnable heartbeatRunnable, wsRunnable;
	private static Thread heartbeatThr, wsThr;
	
	private static int heartbeat_interval, seq;

	private static Context ctx;
    private static Handler mainLooper;

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

	@Override
	public void onDestroy() {
		Toast.makeText(this, "RPCService has been destroyed!", Toast.LENGTH_SHORT).show();
	}
	
    @Override
    public void onCreate() {
        ctx = RPCService.this;

		Toast.makeText(this, "RPCService has been cr!", Toast.LENGTH_SHORT).show();

		mainLooper = new Handler(getMainLooper());
		
		heartbeatRunnable = new Runnable() {
			@Override
			public void run() {
				try {
                    if (heartbeat_interval < 10000) throw new RuntimeException("Invalid Heartbeat Interval!");
                    Thread.sleep(heartbeat_interval);
                    webSocketClient.send(("{\"op\":1, \"d\":" + (seq==0?"null":Integer.toString(seq)) + "}"));

                    showToastM("[ESManager/Thread4] Heartbeat sent! Need to wait for another: " + heartbeat_interval + "ms");
                } catch (InterruptedException e) { }
			}
		};
		
		wsRunnable = new Runnable() {
			@Override
			public void run() {
				createWebSocketClient();
			}
		};
    }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "onStartCommand()!", Toast.LENGTH_SHORT).show();
		
		wsThr = new Thread(wsRunnable);
		wsThr.start();
		
		return START_NOT_STICKY;
	}
	
	private static void createWebSocketClient() {
		URI uri;
		
		try {
			uri = new URI("wss://gateway.discord.gg/?encoding=json&v=9");
		} catch (URISyntaxException mfurle) {
			mfurle.printStackTrace();
			return;
		}
		
		ArrayMap<String, String> headerMap = new ArrayMap<>();
        //headerMap.put("Accept-Encoding", "gzip");
        //headerMap.put("Content-Type", "gzip");

        webSocketClient = new WebSocketClient(uri, headerMap) {
            @Override
            public void onOpen(ServerHandshake s) {
                showToastM("[ESManager/Thread3] Connection opened!");
            }

            @Override
            public void onMessage(ByteBuffer message) {
                showToastM("[ESManager/Thread3] ByteBuffer message received!");
            }

            @Override
            public void onMessage(String message) {
                showToastM("[ESManager/Thread3] Message received! Contents: " + message);
                ArrayMap<String, Object> map = gson.fromJson(
                    message, new TypeToken<ArrayMap<String, Object>>() {}.getType()
                );

                // obtain sequence number
                Object o = map.get("s");
                if (o != null) {
                    seq = ((Double)o).intValue();
                }

                int opcode = ((Double)map.get("op")).intValue();
                switch (opcode) {
                    case 0: // Dispatch event
                        if (((String)map.get("t")).equals("READY")) {
                            Map data = (Map) ((Map)map.get("d")).get("user");
                            return;
                        }
                        break;
                    case 10: // Hello
                        Map data = (Map) map.get("d");
                        heartbeat_interval = ((Double)data.get("heartbeat_interval")).intValue();
                        heartbeatThr = new Thread(heartbeatRunnable);
                        heartbeatThr.start();
                        sendIdentify();
                        break;
                    case 1: // Heartbeat request
                        if (!heartbeatThr.interrupted()) {
                            heartbeatThr.interrupt();
                        }
                        webSocketClient.send(/*encodeString*/("{\"op\":1, \"d\":" + (seq==0?"null":Integer.toString(seq)) + "}"));

                        break;
                    case 11: // Heartbeat ACK
                        if (!heartbeatThr.interrupted()) {
                            heartbeatThr.interrupt();
                        }
                        heartbeatThr = new Thread(heartbeatRunnable);
                        heartbeatThr.start();
                        break;
                }
                showToastM("[ESManager/Thread3] Is websocket connected: " + webSocketClient.isOpen());
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (!heartbeatThr.interrupted()) {
                    heartbeatThr.interrupt();
                }
                throw new RuntimeException("Interrupt");
            }

            @Override
            public void onError(Exception e) {
                // Let's do something with this in the future!
            }
        };
        webSocketClient.connect();
        
        showToastM("[ESManager/Thread3] Is websocket connected: " + webSocketClient.isOpen());
    }

    public static void sendPresence() {
        long current = System.currentTimeMillis();

        ArrayMap<String, Object> presence = new ArrayMap<>();

        ArrayMap<String, Object> activity = new ArrayMap<>();
        activity.put("name", "Test Name");
        activity.put("state", "Test state");
        activity.put("details", "Test Details");
        activity.put("type", 0);
        activity.put("application_id", "956735773716123659");

        //Buttons
        ArrayMap<String, Object> button = new ArrayMap<>();
        button.put("label", "Test button 1");
        button.put("url", "https://github.com");
        activity.put("buttons", button);

        ArrayMap<String, Object> timestamps = new ArrayMap<>();
        timestamps.put("start", current);

        activity.put("timestamps", timestamps);

        presence.put("activities", activity);
        presence.put("afk", true);
        presence.put("since", current);
        presence.put("status", null);

        ArrayMap<String, Object> arr = new ArrayMap<>();
        arr.put("op", 3);
        arr.put("d", presence);

        webSocketClient.send(gson.toJson(arr));
        showToastM("[ESManager/Thread3-MainThread] Presence sent! JSON Content: " + arr.toString());
    }

    private static void sendIdentify() {
        ArrayMap<String, Object> prop = new ArrayMap<>();
        prop.put("$os", "linux");
        prop.put("$browser", "Discord Android");
        prop.put("$device", "unknown");

        ArrayMap<String, Object> data = new ArrayMap<>();
        data.put("token", MainActivity.sharedPreferencesUtil.getString("uname"));
        data.put("properties", prop);
        data.put("compress", false);
        data.put("intents", 0);

        ArrayMap<String, Object> arr = new ArrayMap<>();
        arr.put("op", 2);
        arr.put("d", data);

        webSocketClient.send(gson.toJson(arr));
        showToastM("[ESManager/Thread3] Identify sent! JSON Content: " + arr.toString());
    }

    private static void showToastM(String message) {
        mainLooper.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ctx, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
