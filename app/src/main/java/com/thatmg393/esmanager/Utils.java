package com.thatmg393.esmanager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.thatmg393.esmanager.utils.SharedPreference;
import java.util.List;

public final class Utils {

	public static final String[] app_perms = {Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.MANAGE_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE};
	
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
        public static View setFragmentTheme(@NonNull final Context context, @LayoutRes final int layoutRes, @NonNull final ViewGroup fragContainer) {
            LayoutInflater themedInflater = LayoutInflater.from(new ContextThemeWrapper(context, R.style.App_Main));
            View themedView = themedInflater.inflate(layoutRes, fragContainer, false);
            themedView.setBackgroundColor(Color.parseColor(ResourceUtils.getCurrentThemeColorToHex(context, android.R.attr.windowBackground)));
            return themedView;
            // return LayoutInflater.from(context).inflate(layoutRes, fragContainer, false);
        }
        
        public static void setThemeAuto(@NonNull Context context) {
            if (SharedPreference.getInstance().getBool(Constants.PreferenceKeys.DARK_MODE)) {
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
            	.replace(fragContainer, theFragment)
                .commit();
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
    
    public static class ZipUtils {
  	  
    }
    
    public static class FileUtils {
        
        public static final int ROOT_FOLDER = 0;
        public static final int LOGS_FOLDER = 1;
        public static final int PROJECT_FOLDER = 2;
		public static final int ES_ROOT_FOLDER = 3;
        
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
            } else { /* LoggerUtils.logErr("ESManager/FileUtils", "The external storage is not currently mounted, it is currently on '" + sdcardState + "' state"); */ }
            return null;
        }
    }
}
