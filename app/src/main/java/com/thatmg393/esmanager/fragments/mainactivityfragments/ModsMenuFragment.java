package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.thatmg393.esmanager.adapters.CustomAdapter;
import com.thatmg393.esmanager.data.ModProperties;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModsMenuFragment extends Fragment {

    public final static String path = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.evertechsandbox/files/mods/";

    private AlertDialog createModPopup;

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

                Button project_createMod = (Button) promptView.findViewById(R.id.project_createButton);
                Button project_cancelCreateMod = (Button) promptView.findViewById(R.id.project_cancelButton);

                project_createMod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ((project_modName_field.getText().toString().isEmpty() && project_modDesc_field.getText().toString().isEmpty()) || (project_modName_field.getText().toString().equals("Mod Name") && project_modDesc_field.getText().toString().equals("Mod Description"))) {
                            Toast.makeText(getContext(), "Field cannot be empty or be the default value.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Created.", Toast.LENGTH_SHORT).show();

                            Intent cmai = new Intent(getContext(), CreateModActivity.class);
                            cmai.putExtra("projectModName", project_modName_field.getText().toString());
                            cmai.putExtra("projectModDesc", project_modDesc_field.getText().toString());

                            createModPopup.dismiss();

                            startActivity(cmai);
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

        findAllMods(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void findAllMods(View view) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(getActivity().getMainLooper());

        List<ModProperties> lmp = new ArrayList<>();

        View loading_view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.dialog_loading, null);
        AlertDialog loading_diag = new AlertDialog.Builder(ModsMenuFragment.this.getContext()).create();

        loading_diag.setView(loading_view);
        loading_diag.setCancelable(false);

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
                        if (new File(path + folders + "/info.json").exists()) {
                            final InputStream modjs = new FileInputStream(path + folders + "/info.json");
                            JSONObject json = new JSONObject(IOUtils.toString(modjs, StandardCharsets.UTF_8));

                            lmp.add(new ModProperties(json.getString("name"),
                                    json.getString("description"),
                                    json.getString("author"),
                                    json.getString("version"),
                                    path + folders + "/" + json.getString("preview")));
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                loading_diag.dismiss();
                return;
            }

            handler.post(() -> {
                loading_diag.dismiss();
                ListView modLv = view.findViewById(R.id.modList);
                final CustomAdapter camp = new CustomAdapter(getContext(), 0, Objects.requireNonNull(lmp));
                modLv.setAdapter(camp);
            });
        });
    }
}
