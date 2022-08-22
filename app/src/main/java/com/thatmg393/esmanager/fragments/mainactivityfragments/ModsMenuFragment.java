package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import com.thatmg393.esmanager.adapters.ModListAdapter;
import com.thatmg393.esmanager.data.ModProperties;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ModsMenuFragment extends Fragment {

    public final static String mPath = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.evertechsandbox/files/mods/";
    
    private ModListAdapter modLA;
    private ArrayList<ModProperties> modLT;
    private ListView modLV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_modmenu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button createMod = getView().findViewById(R.id.mod_createNewMod);
        createMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog createModPopup = new AlertDialog.Builder(getContext()).create(); 
                createModPopup.setTitle("Create new mod");
                
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View promptView = layoutInflater.inflate(R.layout.dialog_createnewmod, null);

                final EditText project_modName_field = (EditText) promptView.findViewById(R.id.project_modName);
                final EditText project_modDesc_field = (EditText) promptView.findViewById(R.id.project_modDesc);
                final EditText project_modPath_field = (EditText) promptView.findViewById(R.id.project_modPath);
                project_modPath_field.setText(Environment.getExternalStorageDirectory().toString() + "/ESManager/Projects/" + project_modName_field.getText().toString());
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
                createModPopup.setButton(DialogInterface.BUTTON_POSITIVE, "Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        if (new File(project_modPath_field.getText().toString()).exists()) {
                            dialog.dismiss();
                        }
                        File modPath = new File(project_modPath_field.getText().toString());

                        Intent cmai = new Intent(getContext(), CreateModActivity.class);
                        cmai.putExtra("projectModName", project_modName_field.getText().toString());
                        cmai.putExtra("projectModDesc", project_modDesc_field.getText().toString());
                        cmai.putExtra("projectModPath", project_modPath_field.getText().toString());
                        
                        dialog.dismiss();
                        modPath.mkdirs();
                        startActivity(cmai);
                    }
                });

                createModPopup.setButton(DialogInterface.BUTTON_NEGATIVE, "", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                });
                
                createModPopup.setView(promptView);
                createModPopup.show();
            }
        });
        modLT = new ArrayList<>();
        modLA = new ModListAdapter(getContext(), modLT);
        modLV = requireView().findViewById(R.id.mod_listView);
        modLV.setAdapter(modLA);
        findAllMods();
    }
    /*
    @Override
    public void onResume() {
        super.onResume();
        dirList.clear();
        findAllMods();
    }
    */
	private long postDelayMillis = 40;
    private void findAllMods() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final Handler mainThread = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File[] modsArray = new File(mPath).listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File curFile) {
                            return curFile.isDirectory();
                        }
                    });
                    if (modLT.size() > 0) modLT.clear();
                    if (modsArray == null || modsArray.length < 1) {
                        modLT.add(new ModProperties("No mod/s found.", "Please download some mods.", null, null, null, true));
                    } else {
                    	for (File modFolder : modsArray) {
                    		String jPath = modFolder.getAbsolutePath() + "/info.json";
                            if (new File(jPath).exists()) {
                                try {
                                    FileInputStream jsonContents = new FileInputStream(jPath);
                                    JSONObject parsedJson = new JSONObject(IOUtils.toString(jsonContents, StandardCharsets.UTF_8));
                                	String preview = modFolder.getAbsolutePath() + IOUtils.DIR_SEPARATOR + parsedJson.getString("preview");
                                    
                        	    	modLT.add(new ModProperties(parsedJson.getString("name"),
                                							parsedJson.getString("description"),
                                            				parsedJson.getString("author"),
                                            				parsedJson.getString("version"),
                                            				preview, false));
                                                            
                                    jsonContents.close();                        
                                } catch (JSONException jse) {
                                    Utils.LoggerUtils.logErr(jse.toString());
                            		mainThread.postDelayed(new Runnable() {
                						@Override
                    					public void run() {
                    						Toast.makeText(getContext(), "Internal error occurred.", Toast.LENGTH_LONG).show();
                    					}
                					}, postDelayMillis);
                                }
                    	    }
                    	}
                    }
                } catch (IOException ioe) {
                	Utils.LoggerUtils.logErr(ioe.toString());
            		mainThread.postDelayed(new Runnable() {
                		@Override
                    	public void run() {
                    		Toast.makeText(getContext(), "Internal error occurred.", Toast.LENGTH_LONG).show();
                    	}
                	}, postDelayMillis);
                }
                modLV.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        modLA.updateData(modLT);
                    }
                }, postDelayMillis);
            }
        });
    }
}
