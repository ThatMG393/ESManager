package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thatmg393.esmanager.CreateModActivity;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.Utils;
import com.thatmg393.esmanager.adapters.CustomAdapter;
import com.thatmg393.esmanager.data.ModProperties;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModsMenuFragment extends Fragment {

    public final static String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.evertechsandbox/files/mods/";

    private AlertDialog createModPopup;
    private List<ModProperties> lmp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_modmenu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createModPopup = new AlertDialog.Builder(getContext()).create();

        final Button createMod = getView().findViewById(R.id.createmodBut);
        createMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View promptView = layoutInflater.inflate(R.layout.dialog_createnewmod, null);

                createModPopup.setTitle("Create new mod");

                final EditText project_modName_field = (EditText) promptView.findViewById(R.id.project_modName);
                final EditText project_modDesc_field = (EditText) promptView.findViewById(R.id.project_modDesc);
                final EditText project_modPath_field = (EditText) promptView.findViewById(R.id.project_modPath);
                project_modPath_field.setText(Environment.getExternalStorageDirectory().toString() + "/ESManager/Projects/" + project_modName_field.getText().toString());

                Button project_createMod = (Button) promptView.findViewById(R.id.project_createButton);
                Button project_cancelCreateMod = (Button) promptView.findViewById(R.id.project_cancelButton);

                project_modName_field.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        project_modPath_field.setText(Environment.getExternalStorageDirectory().toString() + "/ESManager/Projects/" + project_modName_field.getText().toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                project_createMod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ((project_modName_field.getText().toString().isEmpty() && project_modDesc_field.getText().toString().isEmpty()) || (project_modName_field.getText().toString().equals("Mod Name") && project_modDesc_field.getText().toString().equals("Mod Description"))) {
                            Toast.makeText(getContext(), "Field cannot be empty or be the default value.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Created.", Toast.LENGTH_SHORT).show();

                            File modPath = new File(project_modPath_field.getText().toString());

                            Intent cmai = new Intent(getContext(), CreateModActivity.class);
                            cmai.putExtra("projectModName", project_modName_field.getText().toString());
                            cmai.putExtra("projectModDesc", project_modDesc_field.getText().toString());
                            cmai.putExtra("projectModPath", project_modPath_field.getText().toString());

                            createModPopup.dismiss();

                            /*
                                if (e.toString().contains("denied") || e.toString().contains("Denied")) {
                                    Toast.makeText(getContext(), "Access denied. Asking for external storage access.", Toast.LENGTH_SHORT).show();
                                    getActivity().requestPermissions(new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 69);
                                    if (modPath.mkdir()) {
                                        startActivity(cmai);
                                    }
                                }
                             */

                            try {
                                if (Utils.ActivityUtils.arePermissionsDenied(getActivity().getApplicationContext(), Utils.app_perms)) {
                                    getActivity().requestPermissions(Utils.app_perms, 69418);
                                }
                                modPath.mkdirs();
                                startActivity(cmai);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                project_cancelCreateMod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createModPopup.dismiss();
                        Toast.makeText(getContext(), "Cancelled.", Toast.LENGTH_SHORT).show();
                    }
                });
                createModPopup.setView(promptView);
                createModPopup.show();
            }
        });
         try {
             if (Utils.ActivityUtils.arePermissionsDenied(getActivity().getApplicationContext(), Utils.app_perms)) {
                 getActivity().requestPermissions(Utils.app_perms, 69418);
                 findAllMods(view);
             } else {
                 findAllMods(view);
             }

         } catch (Exception e) {
             e.printStackTrace();
         }
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        lmp.clear();


        findAllMods(requireView());
    }

     */

    private void findAllMods(View view) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(getActivity().getMainLooper());

        lmp = new ArrayList<>();

        View loading_view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.dialog_loading, null);
        AlertDialog loading_diag = new AlertDialog.Builder(ModsMenuFragment.this.getContext()).create();

        loading_diag.setView(loading_view);
        // loading_diag.setCancelable(false);

        loading_diag.show();
        executor.execute(() -> {
            try {
                File file = new File(path);
                String[] fldrs = file.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File current, String name) {
                        return new File(current, name).isDirectory();
                    }
                });

                if (fldrs == null || fldrs.length < 1) {
                    lmp.add(new ModProperties("No mod/s found!", "Please download some mods!", "", "", ""));
                } else {
                    for (String folders : fldrs) {
                        String jsonPath = path + folders + "/info.json";

                        try {
                            if (new File(jsonPath).exists()) {
                                InputStream modjs = new FileInputStream(jsonPath);
                                JSONObject json = new JSONObject(IOUtils.toString(modjs, StandardCharsets.UTF_8));

                                String name = json.getString("name");
                                String description = json.getString("description");
                                String author = json.getString("author");
                                String version = json.getString("version");
                                String preview = path + folders + "/" + json.getString("preview");

                                lmp.add(new ModProperties(name, description, author, version, preview));
                            }
                        } catch (JSONException jse) {
                            jse.printStackTrace();
                        } catch (FileNotFoundException fnfe) {
                            if (fnfe.toString().contains("denied") || fnfe.toString().contains("Denied")) {
                                loading_diag.dismiss();
                                handler.post(() -> {
                                    Toast.makeText(getContext(), "Access denied. Asking for external storage access.", Toast.LENGTH_SHORT).show();
                                    getActivity().requestPermissions(new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 69);
                                });
                            }
                        }
                    }
                }
            } catch (IOException fnfe) {
                handler.post(() -> {
                    Toast.makeText(getContext(), "Internal error occurred.", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            handler.post(() -> {
                loading_diag.dismiss();
                ListView modLv = view.findViewById(R.id.modList);
                final CustomAdapter camp = new CustomAdapter(getContext(), 0, lmp);
                modLv.setAdapter(camp);
            });
        });
    }
}
