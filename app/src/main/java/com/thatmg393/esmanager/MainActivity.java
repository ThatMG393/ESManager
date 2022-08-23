package com.thatmg393.esmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thatmg393.esmanager.fragments.mainactivityfragments.HomeMenuFragment;
import com.thatmg393.esmanager.fragments.mainactivityfragments.ModsMenuFragment;
import com.thatmg393.esmanager.fragments.mainactivityfragments.SettingsMenuPreferenceFragment;
import com.thatmg393.esmanager.services.RPCActivity;
import com.thatmg393.esmanager.services.RPCService;

public class MainActivity extends AppCompatActivity {

    public static Utils.SharedPreferenceUtil sharedPreferencesUtil;
    public static Intent rpcActIntent;
    public static Intent rpcServIntent;
    
    private BottomNavigationView bottomNav;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
            		case R.id.bottom_nav_homeMenu:
                	getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeMenuFragment()).commit();
                	break;
            	case R.id.bottom_nav_modsMenu:
                	Utils.ActivityUtils.askForPermission(MainActivity.this, Utils.app_perms[0], 2);
                	break;
            	case R.id.bottom_nav_settingsMenu:
                	getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsMenuPreferenceFragment()).commit();
                	break;
        		}
        		return true;
            }
        });
        bottomNav.setSelectedItemId(R.id.bottom_nav_homeMenu);
        
        Utils.ActivityUtils.askForPermission(this, Utils.app_perms[0], 1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        rpcServIntent = new Intent(getApplicationContext(), RPCService.class);
        rpcActIntent = new Intent(getApplicationContext(), RPCActivity.class);
        sharedPreferencesUtil = new Utils.SharedPreferenceUtil("sharedPrefs", MainActivity.this);

        // RPC startup.
        if (sharedPreferencesUtil.getBoolean("agreed_rpc") && sharedPreferencesUtil.getBoolean("isSPChecked") && !Utils.ServiceUtils.isServiceRunning(getApplicationContext(), RPCService.class)) {
            startActivity(rpcActIntent);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
            	if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(MainActivity.this, "Well...", Toast.LENGTH_LONG).show();
                }
            	return;
            case 2:
            	if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        if (System.currentTimeMillis() - firstBackTime > 2000) {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            firstBackTime = System.currentTimeMillis();
            return;
        }
        super.onBackPressed();
    }
}
