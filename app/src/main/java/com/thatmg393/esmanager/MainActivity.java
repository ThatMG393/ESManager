package com.thatmg393.esmanager;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    
    String[] modslists = {"Everlogic", "1x1 Thrusters", "Test1Breh","Test2Bruh"};
    
    private final int EXTERNAL_STORAGE_PERM_CODE = 28;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ModLists = Created Mods
        //ModsList = Installed Mods

        //Ask Premisions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERM_CODE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERM_CODE);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        bottomNav.setSelectedItemId(R.id.bottom_nav_homeMenu);
    }
    
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
        new BottomNavigationView.OnNavigationItemSelectedListener() 
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            
            switch (item.getItemId())
            {
                case R.id.bottom_nav_homeMenu:
                    selectedFragment = new HomeMenuFragment();
                    break;
                case R.id.bottom_nav_modsMenu:
                    selectedFragment = new ModsMenuFragment();
                    break;
                case R.id.bottom_nav_settingsMenu:
                    selectedFragment = new SettingsMenuFragment();
                    break;
            }
            
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            
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
