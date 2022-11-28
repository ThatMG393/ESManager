package com.thatmg393.esmanager.models;

import android.util.ArrayMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class DiscordProfile {
	private final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
	
	private String id;
	private String bio;
	private String status;
	private String avatar;
	private String username;
	private String discriminator;
	
	private String avatarUrl;
	
	public DiscordProfile(ArrayMap<String, Object> response) throws JSONException {
		JSONObject jsonData = new JSONObject(GSON.toJson( (Map)((Map)response.get("d")).get("user") ));
		
		this.id = jsonData.getString("id");
		this.bio = jsonData.getString("bio");
		this.status = new JSONObject(GSON.toJson( (Map)((List)response.get("d")).get(0) )).getString("status");
		this.avatar = jsonData.getString("avatar");
		this.username = jsonData.getString("username");
		this.discriminator = jsonData.getString("discriminator");
		
		this.avatarUrl = "https://cdn.discordapp.com/avatars/" + id + "/" + avatar + "?size=4096";
	}
	
    public String getId() {
        return id;
    }
	
    public String getBio() {
        return bio;
    }
	
    public String getStatus() {
        return status;
    }
	
	public void setStatus(String status) {
        this.status = status;
    }
	
    public String getAvatar() {
        return avatar;
    }
	
    public String getUsername() {
	    return username;
    }
	
    public String getDiscriminator() {
    	return discriminator;
	}
	
	public String getFullUsername() {
		return getUsername() + "#" + getDiscriminator();
	}
	
	public String getAvatarUrl() {
		return avatarUrl;
	}
}
