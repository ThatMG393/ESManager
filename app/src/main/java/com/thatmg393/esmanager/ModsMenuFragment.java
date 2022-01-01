package com.thatmg393.esmanager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.thatmg393.esmanager.ModProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.RelativeLayout;
import android.widget.FrameLayout;
import android.content.Intent;

public class ModsMenuFragment extends Fragment
{
    
    String[] modlists = {"More Buttons", "1 Part Piston", "Test1Bruh","Test2Breh"};

    List<ModProperties> mp;
    ListView lv;
    
    private boolean isListLoaded = false;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_modmenu, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        final Button createMod = getView().findViewById(R.id.createmodBut);
        createMod.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                View promptView = layoutInflater.inflate(R.layout.prompt, null);

                final AlertDialog alertD = new AlertDialog.Builder(this).create();

                EditText userInput = (EditText) promptView.findViewById(R.id.userInput);

                Button btnAdd1 = (Button) promptView.findViewById(R.id.btnAdd1);

                Button btnAdd2 = (Button) promptView.findViewById(R.id.btnAdd2);

                btnAdd1.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {

                        // btnAdd1 has been clicked

                    }
                });

                btnAdd2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        // btnAdd2 has been clicked

                    }
                });

                alertD.setView(promptView);

                alertD.show();

            }
        });
        
        System.out.println("isListLoaded? = " + isListLoaded);
        if (isListLoaded != true)
        {
            mp = new ArrayList<>();
            lv = getView().findViewById(R.id.modList);
            findAllMods();
            CustomAdapter ca = new CustomAdapter(getActivity().getApplicationContext(), 0, mp);

            if (lv != null)
            {
                lv.setAdapter(ca);
                isListLoaded = true;
                System.out.println("isListLoaded? = " + isListLoaded);
            }
        }
    }
    
    public final static String path = Environment.getExternalStorageDirectory().toString() + "/Android/com.evertechsandbox/files/mods/";
    public String jsonPath = null;
    public String previewPath = null;

    JSONObject json;

    public final void findAllMods()
    {
        File file = new File(path);
        String[] directories = file.list(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                    return new File(current, name).isDirectory();
                }
            });

        for ( String folders : directories)
        {
            try
            {
                jsonPath = path + folders + "/info.json";

                File checkdir1 = new File(jsonPath);

                if (checkdir1.exists() == true)
                {
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

                    if (name != null && desc != null && author != null && version != null)
                    {
                        mp.add(new ModProperties(name, desc, author, version, previewPath));
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}
