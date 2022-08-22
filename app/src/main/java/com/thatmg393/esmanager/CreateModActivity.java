package com.thatmg393.esmanager;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.thatmg393.esmanager.Utils;
import com.thatmg393.esmanager.fragments.createmodfragments.ProjectEditorFragment;
import com.thatmg393.esmanager.fragments.createmodfragments.ProjectExplorerFragment;
import com.thatmg393.esmanager.fragments.createmodfragments.ProjectInfoFragment;
import com.thatmg393.esmanager.fragments.mainactivityfragments.SettingsMenuPreferenceFragment;

import java.io.File;
import java.io.IOException;

public class CreateModActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;

    // private final String[] dropdownLists = {"New Script", "Import 3D Object", "Save and Play", "Save only", "Settings", "Exit"};

    public static String projectPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createmod);
        Toolbar toolbar = findViewById(R.id.createmod_toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            TextView nav_header_modName = findViewById(R.id.header_modName);
            if (nav_header_modName != null) {
                nav_header_modName.setText(Html.fromHtml(extras.getString("projectModName")));
            }

            TextView nav_header_modDesc = findViewById(R.id.header_modDesc);
            if (nav_header_modDesc != null) {
                nav_header_modName.setText(Html.fromHtml(extras.getString("projectModDesc")));
            }

            projectPath = extras.getString("projectModPath");
        }
        
        askForPerm();

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.createmod_fragment_container, new ProjectEditorFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_project_editor);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dropdown_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_project_dropdown_newscript:
                Toast.makeText(getApplicationContext(), "New Script Created", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_project_dropdown_addNew3DObject:
                Toast.makeText(getApplicationContext(), "Imported 3D Object Created", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_project_dropdown_savenlaunch:
                Toast.makeText(getApplicationContext(), "Saved and launching", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_project_dropdown_save:
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_project_dropdown_openst:
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsMenuPreferenceFragment()).commit();
                break;

            case R.id.nav_project_dropdown_exit:
                Toast.makeText(getApplicationContext(), "Exiting", Toast.LENGTH_LONG).show();
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_project_editor:
                getSupportFragmentManager().beginTransaction().replace(R.id.createmod_fragment_container, new ProjectEditorFragment()).commit();
                break;

            case R.id.nav_project_explorer:
                getSupportFragmentManager().beginTransaction().replace(R.id.createmod_fragment_container, new ProjectExplorerFragment()).commit();
                break;

            case R.id.nav_project_info:
                getSupportFragmentManager().beginTransaction().replace(R.id.createmod_fragment_container, new ProjectInfoFragment()).commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
            	if (grantResults.length <= 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(CreateModActivity.this, "Please grant storage permission to continue.", Toast.LENGTH_LONG).show();
                    // finish();
                    askForPerm();
                } else {
                	createPFiles();
                }
            	return;
        }
    }
    
    private void askForPerm() {
        if (Utils.ActivityUtils.isPermissionDenied(getApplicationContext(), Utils.app_perms[0])) {
            Utils.ActivityUtils.askForPermission(this, Utils.app_perms[0], 1);
        } else {
        	createPFiles();
        }
    }
    
    private void createPFiles() {
        
    }
}
