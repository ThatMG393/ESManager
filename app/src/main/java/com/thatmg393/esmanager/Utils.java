package com.thatmg393.esmanager;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public final class Utils {

	public static final String[] app_perms = {Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.MANAGE_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE};
    
    public static class SharedPreferenceUtil {

		private SharedPreferences sharedPreference;
		
		SharedPreferenceUtil(String name, Context ctx) {
			try {
				MasterKey masterKey = new MasterKey.Builder(ctx, MasterKey.DEFAULT_MASTER_KEY_ALIAS).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
				sharedPreference = EncryptedSharedPreferences.create(ctx, name, masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
			} catch (GeneralSecurityException | IOException e) {
				e.printStackTrace();
			}
		}
		
		public void addBoolean(String key, boolean value) {
			sharedPreference.edit().putBoolean(key, value).commit();
		}
		
		public boolean getBoolean(String key) {
			return sharedPreference.getBoolean(key, false);
		}
		
		public void addString(String key, String value) {
			sharedPreference.edit().putString(key, value).commit();
		}

		public String getString(String key) {
			return sharedPreference.getString(key, null);
		}
	}
	
	public static class ServiceUtils {
        
        @SuppressWarnings("deprecation")
		public static boolean isServiceRunning(@NonNull final Context ctx, @NonNull final Class<?> serviceClass) {
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

		public static boolean isPermissionDenied(@NonNull final Context context, @NonNull final String permission) {
			return (context.checkCallingPermission(permission) == PackageManager.PERMISSION_GRANTED) ? true : false ;
		}
        
        public static void askForPermission(@NonNull final Activity activity, @NonNull final String permission, @NonNull int resultCode) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, resultCode);
        }
	}

	public static class ThreadUtils {
    	public static void runOnMainThread(@NonNull final Context context, @NonNull final Runnable task) {
			Handler h = new Handler(context.getMainLooper());
			h.post(task);
		}
	}

	public static class LoggerUtils {
		private static final String logTag = "ESManager";

		public static void logInfo(String message) {
			Log.i(logTag, message);
		}

		public static void logWarn(String message) {
			Log.w(logTag, message);
		}

		public static void logErr(String message) {
			Log.e(logTag, message);
		}

		public static void logWTF(String message) {
			Log.wtf(logTag, message);
		}
	}
}
