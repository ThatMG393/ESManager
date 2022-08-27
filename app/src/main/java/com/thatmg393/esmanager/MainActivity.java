package com.thatmg393.esmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thatmg393.esmanager.fragments.mainactivityfragments.HomeMenuFragment;
import com.thatmg393.esmanager.fragments.mainactivityfragments.ModsMenuFragment;
import com.thatmg393.esmanager.fragments.mainactivityfragments.SettingsPreferenceFragment;
import com.thatmg393.esmanager.rpc.RPCActivity;
import com.thatmg393.esmanager.rpc.RPCService;

public class MainActivity extends AppCompatActivity {

    public static Utils.SharedPreferenceUtil sharedPreferencesUtil;
    public static Intent rpcActIntent;
    public static Intent rpcServIntent;
    
    public static MainActivity mInstance;
    public static MainActivity getInstance() {
        return mInstance;
    }
    
    private BottomNavigationView bottomNav;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mInstance = this;
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
                	getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeMenuFragment()).commit();
                	break;
            	case R.id.bottom_nav_modsMenu:
                	if (Utils.ActivityUtils.isPermissionDenied(getApplicationContext(), Utils.app_perms[0])) {
                        Utils.ActivityUtils.askForPermission(MainActivity.this, Utils.app_perms[0], 2);
                    } else {
                    	getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ModsMenuFragment()).commit();
                    }
                	break;
            	case R.id.bottom_nav_settingsMenu:
                	getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsPreferenceFragment()).commit();
                	break;
        		}
        		return true;
            }
        });
        
        
        if (getIntent().getBooleanExtra("fromSettingsFragment", false)) {
            bottomNav.setSelectedItemId(R.id.bottom_nav_settingsMenu);
        } else {
        	bottomNav.setSelectedItemId(R.id.bottom_nav_homeMenu);
        }
        if (Utils.ActivityUtils.isPermissionDenied(this, Utils.app_perms[0])) Utils.ActivityUtils.askForPermission(this, Utils.app_perms[0], 1);
    }

    private void setup() {
        rpcServIntent = new Intent(getApplicationContext(), RPCService.class);
        rpcActIntent = new Intent(getApplicationContext(), RPCActivity.class);
        sharedPreferencesUtil = new Utils.SharedPreferenceUtil("sharedPrefs", MainActivity.this);

        // RPC startup.
        if (sharedPreferencesUtil.getBoolean("agreed_rpc") && sharedPreferencesUtil.getBoolean("discordrpc") && !Utils.ServiceUtils.isServiceRunning(getApplicationContext(), RPCService.class)) {
            startActivity(rpcActIntent);
        }
    }
    
    public void applyTheme(boolean fromSF) {
        Intent maIntent = new Intent(this, getClass());
        maIntent.putExtra("fromSettingsFragment", fromSF);
        maIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(maIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
            	if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(MainActivity.this, "Well...", Toast.LENGTH_LONG).show();
                }
            	return;
            case 2:
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
