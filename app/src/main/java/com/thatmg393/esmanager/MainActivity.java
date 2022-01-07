package com.thatmg393.esmanager;

import android.Manifest;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thatmg393.esmanager.fragments.HomeMenuFragment;
import com.thatmg393.esmanager.fragments.ModsMenuFragment;
import com.thatmg393.esmanager.fragments.SettingsMenuPreferenceFragment;

public class MainActivity extends AppCompatActivity {

    private final int EXTERNAL_STORAGE_PERM_CODE = 28;

    Switch fistSettingsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ModLists = Created Mods
        //ModsList = Installed Mods

        //Ask Premisions

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERM_CODE);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.bottom_nav_homeMenu);
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
