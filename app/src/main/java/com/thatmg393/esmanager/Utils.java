package com.thatmg393.esmanager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.AnyRes;
import androidx.annotation.AttrRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.thatmg393.esmanager.Constants;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class Utils {

	public static final String[] app_perms = {Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.MANAGE_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE};
    
    public static class SharedPreferenceUtil {

		private SharedPreferences sharedPreference;
		
		public SharedPreferenceUtil(String name, Context ctx) {
			try {
				MasterKey masterKey = new MasterKey.Builder(ctx, MasterKey.DEFAULT_MASTER_KEY_ALIAS).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();
				sharedPreference = EncryptedSharedPreferences.create(ctx, name, masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
			} catch (GeneralSecurityException | IOException e) {
				e.printStackTrace();
			}
		}
		
		public void addBoolean(String key, boolean value) {
			sharedPreference.edit().putBoolean(key, value).apply();
		}
		
		public boolean getBoolean(String key) {
			return sharedPreference.getBoolean(key, false);
		}
		
		public void addString(String key, String value) {
			sharedPreference.edit().putString(key, value).apply();
		}

		public String getString(String key) {
			return sharedPreference.getString(key, null);
		}
        
        public void fixMissing() {
            //TODO: Implement this method
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
        
        public static View setFragmentTheme(@NonNull final Context context, @LayoutRes final int layoutRes, @NonNull final ViewGroup fragContainer) {
            LayoutInflater themedInflater = LayoutInflater.from(new ContextThemeWrapper(context, R.style.App_Main));
            View themedView = themedInflater.inflate(layoutRes, fragContainer, false);
            themedView.setBackgroundColor(Color.parseColor(ResourceUtils.getCurrentThemeColorToHex(context, android.R.attr.windowBackground)));
            return themedView;
            // return LayoutInflater.from(context).inflate(layoutRes, fragContainer, false);
        }
        
        public static void setThemeAuto(@NonNull Context context) {
            if (MainActivity.sharedPreferencesUtil.getBoolean(Constants.PreferenceKeys.DARK_MODE)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            //  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            context.setTheme(R.style.App_Main);
        }
        
        public static void changeFragmentWithAnim(@NonNull FragmentTransaction fragTransac, @AnyRes int fragContainer, @NonNull Fragment theFragment) {
            fragTransac
            	.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            	.replace(fragContainer, theFragment).commit();
        }
        
        public static void restartApp(@NonNull Context context) {
            Intent mIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent mPIntent = PendingIntent.getActivity(context, 69420, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            ((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, 600, mPIntent);
            if (context instanceof Activity) ((Activity) context).finishAffinity();
            android.os.Process.killProcess(android.os.Process.myPid());
            Runtime.getRuntime().exit(0); 
        }
        
        public static class ResourceUtils {
            public static String getCurrentThemeColorToHex(@NonNull final Context context, @AttrRes final int attribute) {
    			TypedValue outValue = new TypedValue();
    			context.getTheme().resolveAttribute(attribute, outValue, true);
    			return String.format("#%06X", (0xFFFFFF & outValue.data));
			}
            public static int getCurrentThemeColorToInt(@NonNull final Context context, @AttrRes final int attribute) {
    			TypedValue outValue = new TypedValue();
    			context.getTheme().resolveAttribute(attribute, outValue, true);
    			return outValue.data;
			}
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

		public static void logInfo(String tag, CharSequence message) {
			if (tag != null) {
                Log.i(tag, (String)message);
            } else {
            	Log.i(logTag, (String)message);
            }
		}

		public static void logWarn(String tag, CharSequence message) {
			if (tag != null) {
                Log.w(tag, (String)message);
            } else {
            	Log.w(logTag, (String)message);
            }
		}

		public static void logErr(String tag, CharSequence message) {
			if (tag != null) {
                Log.e(tag, (String)message);
            } else {
            	Log.e(logTag, (String)message);
            }
		}

		public static void logWTF(String tag, CharSequence message) {
			if (tag != null) {
                Log.wtf(tag, (String)message);
            } else {
            	Log.wtf(logTag, (String)message);
            }
		}
	}
    
    public static class ZipUtils {
  	  private static final int BUFFER_SIZE = 1024 * 10;
   	 private static final String TAG = "ESManager/ZipUtils";

   	 public static void unzipFromAssets(Context context, String zipFile, String destination) {
      	  try {
          	  if (destination == null || destination.length() == 0)
              	  destination = context.getFilesDir().getAbsolutePath();
          	  InputStream stream = context.getAssets().open(zipFile);
         	   unzip(stream, destination);
    	    } catch (IOException e) {
       	     e.printStackTrace();
      	  }
   	 }

 	   public static void unzip(String zipFile, String location) {
      	  try {
          	  FileInputStream fin = new FileInputStream(zipFile);
         	   unzip(fin, location);
     	   } catch (FileNotFoundException e) {
          	  e.printStackTrace();
      	  }
  	  }

   	 public static void unzip(InputStream stream, String destination) {
       	 dirChecker(destination, "");
     	   byte[] buffer = new byte[BUFFER_SIZE];
      	  try {
          	  ZipInputStream zin = new ZipInputStream(stream);
         	   ZipEntry ze = null;

           	 while ((ze = zin.getNextEntry()) != null) {
              	  LoggerUtils.logWarn(TAG, "Unzipping " + ze.getName());
					
               	 if (ze.isDirectory()) {
                   	 dirChecker(destination, ze.getName());
             	   } else {
                  	  File f = new File(destination, ze.getName());
                   	 if (!f.exists()) {
                       	 boolean success = f.createNewFile();
                       	 if (!success) {
                          	  LoggerUtils.logWarn(TAG, "Failed to create file: " + f.getName());
                          	  continue;
                       	 }
                        	FileOutputStream fout = new FileOutputStream(f);
                        	int count;
                        	while ((count = zin.read(buffer)) != -1) {
                        	    fout.write(buffer, 0, count);
                       	 }
                      	  zin.closeEntry();
                     	   fout.close();
                    	}
                	}
           	 }
            	zin.close();
        	} catch (Exception e) {
            	LoggerUtils.logErr(TAG, "Unzip failure:\t" + e);
        	}
    	}

   	 private static void dirChecker(String destination, String dir) {
       	 File f = new File(destination, dir);

        	if (!f.isDirectory()) {
            	boolean success = f.mkdirs();
           	 if (!success) {
          	      LoggerUtils.logWarn(TAG, "Failed to create folder: " + f.getName());
         	   }
        	}
        }
    }
    
    public static class FileUtils {
        
        public static final int ROOT_FOLDER = 0;
        public static final int LOGS_FOLDER = 1;
        public static final int PROJECT_FOLDER = 2;
        
        public static String getExternalFilesDirPaths(@NonNull final Context context, @NonNull final int type) {
            String sdcardState = Environment.getExternalStorageState();
            if (sdcardState.equals(Environment.MEDIA_MOUNTED)) {
            	switch (type) {
                	case ROOT_FOLDER:
                		return context.getExternalFilesDir(null).getAbsolutePath();
                	case PROJECT_FOLDER:
                		return context.getExternalFilesDir("Projects").getAbsolutePath();
                	case LOGS_FOLDER:
                		return context.getExternalFilesDir("Logs").getAbsolutePath();
            	}
            } else { LoggerUtils.logErr("ESManager/FileUtils", "The external storage is not currently mounted, it is currently on '" + sdcardState + "' state"); }
            return null;
        }
    }
}
