package com.thatmg393.esmanager.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.thatmg393.esmanager.Constants;
import com.thatmg393.esmanager.MainActivity;
import com.thatmg393.esmanager.interfaces.IOnActivityResult;

public class PermissionUtils {
    public enum PermissionStatus {
        GRANTED, DENIED, CANNOT_BE_GRANTED, SUCCESS, FAILURE, WAITING;
    }
    
    public static PermissionStatus getUsageAccessEnabled(@NonNull Context ctx) {
        AppOpsManager appOps = (AppOpsManager) ctx.getSystemService(Context.APP_OPS_SERVICE);
        final int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), ctx.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_DEFAULT ?
            (ctx.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED)
            : (mode == AppOpsManager.MODE_ALLOWED);
        return granted ? PermissionStatus.GRANTED : PermissionStatus.DENIED;
    }
    
    public static PermissionStatus askForUsageAccess(@NonNull Context ctx, @NonNull IOnActivityResult ioar) {
        if (getUsageAccessEnabled(ctx) == PermissionStatus.DENIED) {
            Intent settingsI = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            MainActivity.getInstance().launchActivityForResult(settingsI, Constants.ResultCodes.PMU_usac, ioar);
            
            return PermissionStatus.WAITING;
        }
        return PermissionStatus.GRANTED;
    }
    
    public static void askForPermission(@NonNull final Activity activity, @NonNull final String permission, @NonNull int resultCode) {
        if (isPermissionDenied(activity.getApplicationContext(), permission)) {    
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                activity.requestPermissions(new String[]{ permission }, resultCode);
            }
        }
    }
        
    public static void askForPermissions(@NonNull final Activity activity, @NonNull final String[] permissions, @NonNull int resultCode) {
        for (String permission : permissions) {
            if (isPermissionDenied(activity.getApplicationContext(), permission)) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    activity.requestPermissions(new String[]{ permission }, resultCode);
                }
            }
        }
	}
	
	public static boolean isPermissionDenied(@NonNull final Context context, @NonNull final String permission) {
		return (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) ? true : false;
	}
}
