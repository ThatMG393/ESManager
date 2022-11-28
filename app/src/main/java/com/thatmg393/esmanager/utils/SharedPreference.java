package com.thatmg393.esmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.thatmg393.esmanager.interfaces.IOnSharedPreferenceChange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class SharedPreference {
    private static SharedPreference spInstance;
    
    public static SharedPreference newInstance(Context ctx, String name) {
        if (spInstance != null) return spInstance;
        spInstance = new SharedPreference();
        
        try {
            MasterKey mk = new MasterKey.Builder(ctx, MasterKey.DEFAULT_MASTER_KEY_ALIAS).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
		    spInstance.sp = EncryptedSharedPreferences.create(ctx, name, mk, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) { e.printStackTrace(); }
        
        return spInstance;
    }
    
    public static SharedPreference getInstance() {
        if (spInstance == null) throw new IllegalStateException("You should call newInstance(Context) before you get this instance");
        return spInstance;
    }
    
    private SharedPreferences sp;
    private SharedPreference() {
        if (spInstance != null) 
            throw new IllegalStateException("You should call newInstance(Context) or getInstance() to get an instance of this class");
    }
	
	private List<IOnSharedPreferenceChange> lIOSP = new ArrayList<IOnSharedPreferenceChange>();
    public void addListener(@NonNull IOnSharedPreferenceChange iosp) {
		lIOSP.add(iosp);
	}
	public void removeListener(@NonNull IOnSharedPreferenceChange iosp) {
		lIOSP.remove(iosp);
	}
	
    public String getString(String key) {
        return sp.getString(key, null);
    }
    public String getString(String key, String fallback) {
        return sp.getString(key, fallback);
    }
    public boolean addString(String key, String value) {
        boolean tmp = sp.edit().putString(key, value).commit();
		if (tmp) for (IOnSharedPreferenceChange iosp : lIOSP) { iosp.onChange(key, value); }
		return tmp;
    }
    
    public boolean getBool(String key) {
        return sp.getBoolean(key, false);
    }
    public boolean getBool(String key, boolean fallback) {
        return sp.getBoolean(key, fallback);
    }
    public boolean addBool(String key, boolean value) {
        boolean tmp = sp.edit().putBoolean(key, value).commit();
		if (tmp) for (IOnSharedPreferenceChange iosp : lIOSP) { iosp.onChange(key, value); }
		return tmp;
    }
}
