package com.thatmg393.esmanager.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import com.thatmg393.esmanager.adapters.CustomAdapter;
import com.thatmg393.esmanager.data.ModProperties;
import com.thatmg393.esmanager.R;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ModsMenuFragment extends Fragment {

    public final static String path = Environment.getExternalStorageDirectory().toString() + "/Android/com.evertechsandbox/files/mods/";
    public String jsonPath = null;
    public String previewPath = null;
    String[] modlists = {"More Buttons", "1 Part Piston", "Test1Bruh", "Test2Breh"};
    List<ModProperties> mp;
    ListView lv;
    JSONObject json;
    private boolean isListLoaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_modmenu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button createMod = getView().findViewById(R.id.createmodBut);
        createMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View promptView = layoutInflater.inflate(R.layout.dialog_createnewmod, null);

                final AlertDialog createModPopup = new AlertDialog.Builder(getContext()).create();

                //Customizers
                createModPopup.setTitle("Create new mod");

                //Field
                final EditText project_modName_field = (EditText) promptView.findViewById(R.id.project_modName);
                final EditText project_modDesc_field = (EditText) promptView.findViewById(R.id.project_modDesc);

                //Buttons
                Button project_createMod = (Button) promptView.findViewById(R.id.project_createButton);
                Button project_cancelCreateMod = (Button) promptView.findViewById(R.id.project_cancelButton);

                //Button Events
                project_createMod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (project_modName_field.getText().toString().isEmpty() && project_modDesc_field.getText().toString().isEmpty()) {
                            Toast.makeText(getContext(), "Field cannot be blank.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Created.", Toast.LENGTH_SHORT).show();

                            Intent cmai = new Intent(getContext(), CreateModActivity.class);

                            //Send data to activity
                            cmai.putExtra("projectModName", project_modName_field.getText().toString());
                            cmai.putExtra("projectModDesc", project_modDesc_field.getText().toString());

                            //Close dialog
                            createModPopup.dismiss();

                            //Launch!
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

                //Finalizer
                createModPopup.setView(promptView);
                createModPopup.show();


                //Toast.makeText(getContext(), "Very buggy almost done!", Toast.LENGTH_SHORT).show();
            }
        });

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promptView = layoutInflater.inflate(R.layout.dialog_loading, null);

        final AlertDialog loading_diag = new AlertDialog.Builder(getContext()).create();
        loading_diag.setView(promptView);

        System.out.println("isListLoaded? = " + isListLoaded);
        if (isListLoaded != true) {
            loading_diag.show();
            mp = new ArrayList<>();
            lv = getView().findViewById(R.id.modList);
            findAllMods();
            CustomAdapter ca = new CustomAdapter(getActivity().getApplicationContext(), 0, mp);

            if (lv != null) {
                lv.setAdapter(ca);
                isListLoaded = true;
                System.out.println("isListLoaded? = " + isListLoaded);
                loading_diag.dismiss();
            }
        }
    }

    public final void findAllMods() {
        File file = new File(path);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        for (String folders : directories) {
            try {
                jsonPath = path + folders + "/info.json";

                File checkdir1 = new File(jsonPath);

                if (checkdir1.exists() == true) {
                    InputStream istr = new FileInputStream(jsonPath);
                    String convertedJson = IOUtils.toString(istr, "UTF-8");

                    json = new JSONObject(convertedJson);

                    String preview = json.getString("preview");

                    previewPath = path + folders + "/" + preview;

                    //System.out.println("Image path is: " + previewPath);
                    //Value should be '...Mods/everlogic/textures/preview.png'

                    String name = json.getString("name");
                    String desc = json.getString("description");
                    String author = json.getString("author");
                    String version = json.getString("version");

                    if (name != null && desc != null && author != null && version != null) {
                        mp.add(new ModProperties(name, desc, author, version, previewPath));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
