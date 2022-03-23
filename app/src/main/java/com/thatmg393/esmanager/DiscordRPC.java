package com.thatmg393.esmanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.net.ssl.SSLParameters;

public class DiscordRPC extends AppCompatActivity {

    private SharedPreferences pref;

    private WebView webView;
    private TextView textViewLog;
    private Runnable heartbeatRunnable;
    private Gson gson;


    private Thread heartbeatThr, wsThr;
    private WebSocketClient webSocketClient;

    private int heartbeat_interval, seq;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rcp_webview_main);

        init();

        webView = (WebView) findViewById(R.id.rcp_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl("https://discord.com/login");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.stopLoading();
                if (url.endsWith("/app")) {
                    setContentView(R.layout.rcp_main);
                    textViewLog = findViewById(R.id.rcp_log);
                    extractToken();
                    login();
                }
                return false;
            }
        });

    }

    private void init() {
        heartbeatRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    appendToLog("Heartbeat wait for " + heartbeat_interval);
                    if (heartbeat_interval < 10000)
                        throw new RuntimeException("Invalid Heartbeat Interval");
                    Thread.sleep(heartbeat_interval);
                    webSocketClient.send(/*encodeString*/("{\"op\":1, \"d\":" + (seq == 0 ? "null" : Integer.toString(seq)) + "}"));
                    appendToLog("Heartbeat sent");
                } catch (InterruptedException e) {
                    appendToLog(e.toString());
                }
            }
        };

        gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    }

    private void sendPresenceUpdate() {
        long current = System.currentTimeMillis();

        ArrayMap<String, Object> presence = new ArrayMap<>();
        ArrayMap<String, Object> activity = new ArrayMap<>();

        activity.put("name", "Evertech Sandbox");
        /*
         activity.put("state", ""); //Leaved empty
         activity.put("details", ""); //Leaved empty
        */
        activity.put("type", 0);

        ArrayMap<String, Object> timestamps = new ArrayMap<>();
        timestamps.put("start", current);

        activity.put("timestamps", timestamps);

        presence.put("activities", new Object[]{activity});
        presence.put("afk", false);
        presence.put("since", current);
        presence.put("status", null);

        ArrayMap<String, Object> arr = new ArrayMap<>();
        arr.put("op", 3);
        arr.put("d", presence);

        webSocketClient.send(gson.toJson(arr));
    }

    private void removePresenceUpdate() {
        long current = System.currentTimeMillis();

        ArrayMap<String, Object> presence = new ArrayMap<>();
        ArrayMap<String, Object> activity = new ArrayMap<>();

        activity.put("name", ""); //Leaved empty to unset

        presence.put("activities", new Object[]{activity});
        presence.put("afk", false);
        presence.put("since", current);
        presence.put("status", null);

        ArrayMap<String, Object> arr = new ArrayMap<>();
        arr.put("op", 3);
        arr.put("d", presence);

        webSocketClient.send(gson.toJson(arr));
    }

    private void login() {
        if (authToken != null) {
            Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
            connect();
        }
    }

    private void connect() {
        wsThr = new Thread(new Runnable() {
            @Override
            public void run() {
                createWebSocketClient();
            }
        });

        wsThr.start();
    }

    private void createWebSocketClient() {
        appendToLog("Connecting...");

        URI uri;

        try {
            uri = new URI("wss://gateway.discord.gg/?encoding=json&v=9");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        ArrayMap<String, String> headerMap = new ArrayMap<>();

        webSocketClient = new WebSocketClient(uri, headerMap) {

            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                appendToLog("Connected.");
            }

            @Override
            public void onMessage(String message) {
                ArrayMap<String, Object> map = gson.fromJson(message, new TypeToken<ArrayMap<String, Object>>() {
                }.getType());

                Object o = map.get("s");
                if (o != null) {
                    seq = ((Double) o).intValue();
                }

                int opcode = ((Double) map.get("op")).intValue();

                switch (opcode) {
                    case 0:
                        if (((String) map.get("t")).equals("READY")) {
                            appendToLog("Received READY event");
                            Map data = (Map) ((Map) map.get("d")).get("user");
                            appendToLog("Connected to " + data.get("username") + "#" + data.get("discriminator"));
                            return;
                        }
                        break;

                    case 10:
                        Map data = (Map) map.get("d");
                        heartbeat_interval = ((Double) data.get("heartbeat_interval")).intValue();

                        heartbeatThr = new Thread(heartbeatRunnable);
                        heartbeatThr.start();

                        sendMobileIdentity();
                        break;

                    case 1:
                        if (!heartbeatThr.interrupted()) {
                            heartbeatThr.interrupt();
                        }

                        webSocketClient.send(/*encodeString*/("{\"op\":1, \"d\":" + (seq == 0 ? "null" : Integer.toString(seq)) + "}"));
                        break;

                    case 11:
                        if (!heartbeatThr.interrupted()) {
                            heartbeatThr.interrupt();
                        }

                        heartbeatThr = new Thread(heartbeatRunnable);
                        heartbeatThr.start();
                        break;
                }
            }

            @Override
            public void onClose(int reasonCode, String reason, boolean remote) {
                appendToLog("Connection closed with exit code " + reasonCode + " additional info: " + reason + "\n");

                if (!heartbeatThr.interrupted()) {
                    heartbeatThr.interrupt();
                }

                throw new RuntimeException("Heartbeat Interrupted");
            }

            @Override
            public void onError(Exception e) {
                if (!e.getMessage().equals("Interrupt")) {
                    appendToLog(Log.getStackTraceString(e));
                }
            }

            @Override
            protected void onSetSSLParameters(SSLParameters sslParameters) {
                try {
                    super.onSetSSLParameters(sslParameters);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        };

        webSocketClient.connect();
    }

    private void sendMobileIdentity() {
        ArrayMap<String, Object> prop = new ArrayMap<>();
        prop.put("$os", "linux");
        prop.put("$browser", "Discord Android");
        prop.put("$device", "unknown");

        ArrayMap<String, Object> data = new ArrayMap<>();
        data.put("token", authToken);
        data.put("properties", prop);
        data.put("compress", false);
        data.put("intents", 0);

        ArrayMap<String, Object> arr = new ArrayMap<>();
        arr.put("op", 2);
        arr.put("d", data);

        webSocketClient.send(gson.toJson(arr));
    }

    private void appendToLog(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textViewLog != null) {
                    textViewLog.append(message + "\n");
                }
            }
        });
    }

    public final boolean extractToken() {
        // ~~extract token in an ugly way :troll:~~

        try {
            File f = new File(getFilesDir().getParentFile(), "app_webview/Default/Local Storage/leveldb");
            File[] fArr = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String name) {
                    return name.endsWith(".log");
                }
            });

            if (fArr.length == 0) {
                return false;
            }
            f = fArr[0];
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("token")) {
                    break;
                }
            }

            line = line.substring(line.indexOf("token") + 5);
            line = line.substring(line.indexOf("\"") + 1);
            authToken = line.substring(0, line.indexOf("\""));
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}
