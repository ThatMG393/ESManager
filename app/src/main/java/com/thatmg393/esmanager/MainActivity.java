package com.thatmg393.esmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

        requestPermissions(Utils.app_perms, 69420);

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
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            firstBackTime = System.currentTimeMillis();
            return;
        }

        super.onBackPressed();
    }
}
