package com.thatmg393.esmanager.rpc;

import android.util.ArrayMap;

import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;

import com.thatmg393.esmanager.Constants;
import com.thatmg393.esmanager.models.DiscordProfile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Map;

import java.util.List;

import com.thatmg393.esmanager.interfaces.IRPCListener;
import org.json.JSONException;
import org.json.JSONObject;

public class DiscordSocketClient extends WebSocketClient {
	private final DiscordRPCService rpcService;
    private final Runnable heartbeatRunnable;
    private Thread heartbeatThr;
    
	private int heartbeat_interval, seq;
    private String currentStatus;
    
    private List<IRPCListener> listeners;
    
    public boolean isReady;
	
	public DiscordSocketClient(DiscordRPCService rpcService) throws URISyntaxException {
		super(new URI("wss://gateway.discord.gg/?encoding=json&v=10"));
		
		this.rpcService = rpcService;
		this.heartbeatRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					if (heartbeat_interval < 10000) { throw new RuntimeException("Invalid Heartbeat Interval!"); }
					Thread.sleep(heartbeat_interval);
					send(("{\"op\":1, \"d\":" + (seq==0?"null":Integer.toString(seq)) + "}"));
					rpcService.updateNotif("Heartbeat sent! Wait for another " + heartbeat_interval);
				} catch (InterruptedException ie) { }
			}
		};
		// this.heartbeatThr = new Thread(heartbeatRunnable);
        
        listeners = DiscordRPC.getInstance().getListeners();
        rpcService.updateNotif("WebSocketClient initialized...");
	}
	
	@Override
	public void onOpen(ServerHandshake sh) { }
	
	@Override
    public void onMessage(ByteBuffer message) { }
	
	@Override
    public void onMessage(String message) {
		if (rpcService.wsThread.isAlive() && rpcService.wsThread.interrupted()) { close(); return; }
		ArrayMap<String, Object> messageMap = rpcService.GSON.fromJson(
			message, new TypeToken<ArrayMap<String, Object>>() { }.getType()
		);
		
		Double seqN = (Double)messageMap.get("s");
		if (seqN != null) {
			seq = seqN.intValue();
		}
		
		int opcode = ((Double)messageMap.get("op")).intValue();
		switch (opcode) {
			case 0: onMessage(messageMap); break;
			case 10: // Hello
				Map dataMap = (Map)messageMap.get("d");
                
				heartbeat_interval = ((Double)dataMap.get("heartbeat_interval")).intValue();
                heartbeatThr = new Thread(heartbeatRunnable);
                heartbeatThr.start();
                
                sendIdentify();
                rpcService.updateNotif("Connected to discord.com!");
				break;
			case 1: // Heartbeat Request
				if (!heartbeatThr.interrupted()) heartbeatThr.interrupt();
				send(("{\"op\":1, \"d\":" + (seq==0?"null":Integer.toString(seq)) + "}"));
				break;
			case 11: // Heartbeat ACK
				if (!heartbeatThr.interrupted()) heartbeatThr.interrupt();
                heartbeatThr = new Thread(heartbeatRunnable);
                heartbeatThr.start();
				break;
		}
	}
	
	private void onMessage(ArrayMap<String, Object> data) {
		Map tmp;
		String state = (String) data.get("t");
		switch (state) {
			case "READY":
				tmp = (Map)((Map)data.get("d")).get("user");
                try {
                    JSONObject j = new JSONObject(rpcService.GSON.toJson(tmp));
                    for (IRPCListener l : listeners) { l.onReady(DiscordRPC.getInstance().dp); }
					
					if (DiscordRPC.getInstance().dp == null) DiscordRPC.getInstance().dp = new DiscordProfile(data);
                } catch (JSONException e) { /* Shouldnt happen */ }
				isReady = true;
				break;
			case "SESSIONS_REPLACE":
				tmp = (Map)((List)data.get("d")).get(0);
                try {
                    JSONObject j = new JSONObject(rpcService.GSON.toJson(tmp));
                    String current = j.getString("status");
                    rpcService.updateNotif("Status changed to " + current);
                    rpcService.accStatus = current;
			    	for (IRPCListener l : listeners) { l.onSessionsReplace(null, current); }
					
					if (DiscordRPC.getInstance().dp != null) { DiscordRPC.getInstance().dp.setStatus(current); }
                } catch (JSONException e) { /* Shouldnt happen */ }
				break;
		}
	}
	
	@Override
    public void onClose(int code, String reason, boolean remote) {
		if (!heartbeatThr.interrupted()) heartbeatThr.interrupt();
		for (IRPCListener l : listeners) {
			l.onStop(reason, code);
		}
		isReady = false;
	}
	
	@Override
    public void onError(Exception ex) {
        ex.printStackTrace();
		for (IRPCListener l : listeners) {
			l.onError(ex);
		}
	}
	
	@Override
	public void close() {
		heartbeatThr.interrupt();
		for (IRPCListener l : listeners) {
			l.onStop("Closed by service", 0);
		}
		isReady = false;
		super.close();
	}
	
	private void sendIdentify() {
        ArrayMap<String, Object> prop = new ArrayMap<>();
        prop.put("$os", "linux");
        prop.put("$browser", "Discord Android");
        prop.put("$device", "unknown");

        ArrayMap<String, Object> data = new ArrayMap<>();
        data.put("token", rpcService.authToken);
        data.put("properties", prop);
        data.put("compress", false);
        data.put("intents", 0);

        ArrayMap<String, Object> arr = new ArrayMap<>();
        arr.put("op", 2);
        arr.put("d", data);
		
		send(rpcService.GSON.toJson(arr));
    }
	
	public boolean stopWebsocket() {
		try {
			closeBlocking();
			if (rpcService.wsThread.isAlive() && !rpcService.wsThread.interrupted()) rpcService.wsThread.interrupt();
		} catch (InterruptedException ie) { return false; }
		return true;
	}
}
