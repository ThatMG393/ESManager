package com.thatmg393.esmanager;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.thatmg393.esmanager.fragments.ProjectEditorFragment;

public class CreateModActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createmod);
        Toolbar toolbar = findViewById(R.id.createmod_toolbar);
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
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        String[] dropdownLists = {"New Script", "Import 3D Object", "Save and Play", "Save only", "Settings", "Exit"};

        Spinner dropdown = findViewById(R.id.project_dropdown);
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dropdownLists);
        dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(dropdownAdapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                switch (pos)
                {
                    case 0:
                        Toast.makeText(getApplicationContext(), "New Script Created", Toast.LENGTH_LONG).show();
                        break;

                    case 1:
                        Toast.makeText(getApplicationContext(), "Imported 3D Object Created", Toast.LENGTH_LONG).show();
                        PickFile();
                        break;

                    case 2:
                        Toast.makeText(getApplicationContext(), "Saved and launching", Toast.LENGTH_LONG).show();
                        break;

                    case 3:
                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
                        break;

                    case 4:
                        Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG).show();
                        break;

                    case 5:
                        Toast.makeText(getApplicationContext(), "Exited", Toast.LENGTH_LONG).show();
                        CreateModActivity.this.finish();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.createmod_fragment_container, new ProjectEditorFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_project_editor);
        }
    }

    private void PickFile() {
        /* Disabled for some reason.
        SingleFilePickerDialog singleFilePickerDialog = new SingleFilePickerDialog(getApplicationContext(),
                () -> Toast.makeText(getApplicationContext(), "Canceled!!", Toast.LENGTH_SHORT).show(),
                files -> Toast.makeText(getApplicationContext(), files[0].getPath(), Toast.LENGTH_SHORT).show());
        singleFilePickerDialog.show();
         */
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
