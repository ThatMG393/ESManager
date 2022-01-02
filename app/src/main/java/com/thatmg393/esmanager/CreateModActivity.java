package com.thatmg393.esmanager;

import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class CreateModActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected static String project_modName;
    protected static String project_modDesc;
    private DrawerLayout drawerLayout;

    public static final String setProject_modName(String val) {
        project_modName = val;
        return val;
    }

    public static final String setProject_modDesc(String val) {
        project_modDesc = val;
        return val;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createmod);
        Toolbar toolbar = findViewById(R.id.createmod_toolbar);

        /* Useless might be useful later
        final ImageButton menuButton = findViewById(R.id.createmod_menu_button);
        menuButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                float deg = menuButton.getRotation() + 180F;
                menuButton.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
            }
        });
         */

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            TextView nav_header_modName = (TextView) findViewById(R.id.header_modName);
            if (nav_header_modName != null) {
                nav_header_modName.setText(Html.fromHtml(extras.getString("projectModName")));
            }

            TextView nav_header_modDesc = (TextView) findViewById(R.id.header_modDesc);
            if (nav_header_modDesc != null) {
                nav_header_modName.setText(Html.fromHtml(extras.getString("projectModDesc")));
            }
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toast.makeText(this, extras.getString("projectModName"), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, extras.getString("projectModDesc"), Toast.LENGTH_SHORT).show();

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.createmod_fragment_container, new ProjectEditorFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_project_editor);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dropdown_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_project_dropdown_save:
                Toast.makeText(getApplicationContext(), "Saved.", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_project_editor:
                getSupportFragmentManager().beginTransaction().replace(R.id.createmod_fragment_container, new ProjectEditorFragment()).commit();
                break;

            case R.id.nav_project_explorer:

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
}
