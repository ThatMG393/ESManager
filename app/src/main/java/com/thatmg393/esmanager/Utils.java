package com.thatmg393.esmanager;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public final class Utils {
    
    public static class SharedPreferenceUtil {
		
		private SharedPreferences sharedPreference;
		private SharedPreferences.Editor spe;
		
		@SuppressLint("CommitPrefEdits")
		public SharedPreferenceUtil(String name, Context ctx) {
			try {
				String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
				sharedPreference = EncryptedSharedPreferences.create(name, masterKeyAlias, ctx, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
			} catch (GeneralSecurityException | IOException e) {
				e.printStackTrace();
			}

			spe = sharedPreference.edit();
		}
		
		public void addBoolean(String key, boolean value) {
			spe.putBoolean(key, value);
			spe.apply();
		}
		
		public boolean getBoolean(String key) {
			return sharedPreference.getBoolean(key, false);
		}
		
		public void addString(String key, String value) {
			spe.putString(key, value);
			spe.apply();
		}

		public String getString(String key) {
			return sharedPreference.getString(key, "DEFAULT");
		}
	}
	
	public static class ServiceUtils {
		public static boolean checkIfServiceIsRunning(Context ctx, Class<?> serviceClass) {
			ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
			for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
				if (serviceClass.getName().equals(service.service.getClassName())) {
					return true;
				}
			}
			return false;
		}
	}

	public static class ActivityUtils {
		public static boolean checkIfAppIsRunning(final Context context, String appPackageName) {
			final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
			if (procInfos != null) {
				for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
					if (processInfo.processName.equals(appPackageName)) {
						return true;
					}
				}
			}
			return false;
		}
	}
}
