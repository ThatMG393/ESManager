package com.thatmg393.esmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.core.splashscreen.SplashScreen;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thatmg393.esmanager.activity.BaseActivity;
import com.thatmg393.esmanager.fragments.mainactivityfragments.HomeMenuFragment;
import com.thatmg393.esmanager.fragments.mainactivityfragments.ModsMenuFragment;
import com.thatmg393.esmanager.fragments.mainactivityfragments.SettingsPreferenceFragment;
import com.thatmg393.esmanager.rpc.RPCActivity;
import com.thatmg393.esmanager.rpc.RPCService;

import java.io.File;
import java.lang.ref.WeakReference;

public class MainActivity extends BaseActivity {

    public static Utils.SharedPreferenceUtil sharedPreferencesUtil;
    public static Intent rpcActIntent;
    public static Intent rpcServIntent;
    
    private static WeakReference<MainActivity> mInstance;
    public static MainActivity getInstance() {
        return mInstance.get();
    }
    
    private BottomNavigationView bottomNav;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getExternalFilesDir(null);
        this.startLogging();
        setup();
        Utils.ActivityUtils.setThemeAuto(this);
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.main_toolbar));
        
        bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
            		case R.id.bottom_nav_homeMenu:
                	Utils.ActivityUtils.changeFragmentWithAnim(getSupportFragmentManager().beginTransaction(), R.id.fragment_container, new HomeMenuFragment());
                    break;
            	case R.id.bottom_nav_modsMenu:
                	if (Utils.ActivityUtils.isPermissionDenied(getApplicationContext(), Utils.app_perms[0])) {
                        Utils.ActivityUtils.askForPermission(MainActivity.this, Utils.app_perms[0], Constants.ResultCodes.MA_modsmenu);
                    } else {
                    	Utils.ActivityUtils.changeFragmentWithAnim(getSupportFragmentManager().beginTransaction(), R.id.fragment_container, new ModsMenuFragment());
                    }
                	break;
            	case R.id.bottom_nav_settingsMenu:
                	Utils.ActivityUtils.changeFragmentWithAnim(getSupportFragmentManager().beginTransaction(), R.id.fragment_container, new SettingsPreferenceFragment());
                	break;
        		}
        		return true;
            }
        });
        
        
        if (getIntent().getBooleanExtra(Constants.PreferenceKeys.APPLY_THEME, false)) {
            bottomNav.setSelectedItemId(R.id.bottom_nav_settingsMenu);
        } else {
        	bottomNav.setSelectedItemId(R.id.bottom_nav_homeMenu);
        }
        if (Utils.ActivityUtils.isPermissionDenied(this, Utils.app_perms[0])) Utils.ActivityUtils.askForPermission(this, Utils.app_perms[0], Constants.ResultCodes.MA_startup);
    }

    private void setup() {
        mInstance = new WeakReference<MainActivity>(this);
        
        rpcServIntent = new Intent(getApplicationContext(), RPCService.class);
        rpcActIntent = new Intent(getApplicationContext(), RPCActivity.class);
        sharedPreferencesUtil = new Utils.SharedPreferenceUtil(Constants.PREF_FILE_NAME, this);
        // sharedPreferencesUtil = MainApplication.mainSP; // Fix for now.
        
        // RPC startup.
        if (sharedPreferencesUtil.getBoolean(Constants.PreferenceKeys.AGREED_RPC) && sharedPreferencesUtil.getBoolean(Constants.PreferenceKeys.RPC_ENABLED) && !Utils.ServiceUtils.isServiceRunning(getApplicationContext(), RPCService.class)) {
            startActivity(rpcActIntent);
        }
    }
    
    public void applyTheme() {
        Intent maIntent = new Intent(this, getClass());
        maIntent.putExtra(Constants.PreferenceKeys.APPLY_THEME, true);
        maIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(maIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.ResultCodes.MA_startup:
            	if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(MainActivity.this, "Well...", Toast.LENGTH_LONG).show();
                }
            	return;
            case Constants.ResultCodes.MA_modsmenu:
            	if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ModsMenuFragment()).commit();
                } else {
                	Toast.makeText(MainActivity.this, "This area requires storage permission to read your mods.", Toast.LENGTH_LONG).show();
                }
            	return;
            default:
            	super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private long firstBackTime;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstBackTime > 1695) {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            firstBackTime = System.currentTimeMillis();
            return;
        }
        super.onBackPressed();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Utils.ServiceUtils.isServiceRunning(this, RPCService.class)) stopService(rpcServIntent);
    }
}
