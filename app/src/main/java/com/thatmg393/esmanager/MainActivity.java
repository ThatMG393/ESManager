package com.thatmg393.esmanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ask Permissions
        int WRITE_EXTERNAL_STORAGE_PERM_CODE = 28;
        int READ_EXTERNAL_STORAGE_PERM_CODE = 29;
        int INTERNET_PERM_CODE = 30;
        int SYSTEM_ALERT_WINDOW_PERM_CODE = 31;

        // android.permission.SYSTEM_ALERT_WINDOW
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERM_CODE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERM_CODE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_PERM_CODE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, SYSTEM_ALERT_WINDOW_PERM_CODE);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.bottom_nav_homeMenu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        rpcServIntent = new Intent(getApplicationContext(), RPCService.class);
        rpcActIntent = new Intent(getApplicationContext(), RPCActivity.class);
        sharedPreferencesUtil = new Utils.SharedPreferenceUtil("sharedPrefs", MainActivity.this);

        if (sharedPreferencesUtil.getBoolean("isSPChecked") && !Utils.ServiceUtils.checkIfServiceIsRunning(getApplicationContext(), RPCService.class))
        {
            startActivity(rpcActIntent);
        } else {
            System.out.println(sharedPreferencesUtil);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottom_nav_homeMenu:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeMenuFragment()).commit();
                            break;
                        case R.id.bottom_nav_modsMenu:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ModsMenuFragment()).commit();
                            break;
                        case R.id.bottom_nav_settingsMenu:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsMenuPreferenceFragment()).commit();
                            break;
                    }

                    return true;
                }
            };

    private long firstBackTime;

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - firstBackTime > 2000) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            firstBackTime = System.currentTimeMillis();
            return;
        }

        super.onBackPressed();
    }
}
