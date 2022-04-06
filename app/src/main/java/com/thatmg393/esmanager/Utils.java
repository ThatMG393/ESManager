package com.thatmg393.esmanager;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public final class Utils {

	public static final String[] app_perms = {Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.MANAGE_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE};
    
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
		public static boolean checkIfServiceIsRunning(@NonNull final Context ctx, @NonNull final Class<?> serviceClass) {
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
		public static boolean checkIfAppIsRunning(@NonNull final Context context, @NonNull final String appPackageName) {
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

		public static boolean arePermissionsDenied(@NonNull final Context context, @NonNull final String[] permissions) {
			int p = 0;
			while (p < permissions.length) {
				if (context.checkSelfPermission(permissions[p]) != PackageManager.PERMISSION_GRANTED) {
					return true;
				}
				p++;
			}
			return false;
		}
	}

	public static class ThreadUtils {
    	public static void runOnMainThread(@NonNull final Context context, @NonNull final Runnable task) {
			Handler h = new Handler(context.getMainLooper());
			h.post(task);
		}
	}
}
