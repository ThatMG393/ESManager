package com.thatmg393.esmanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class CreateModActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawerLayout;

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

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);

        NavigationView navigationView  = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.createmod_fragment_container, new ProjectEditorFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_project_editor);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
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
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }
}
