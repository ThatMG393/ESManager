package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

import com.thatmg393.esmanager.Constants;
import com.thatmg393.esmanager.CreateModActivity;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.adapters.ModListAdapter;
import com.thatmg393.esmanager.data.ModProperties;
import com.thatmg393.esmanager.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class ModsMenuFragment extends Fragment {

    public final static String mPath = Constants.ES_ROOT_FLDR + "/mods";
    
    private ModListAdapter modLA;
    
    private ListView modLV;
    
    private ExecutorService modParserExec;
    private boolean forcedShutdown = false;

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

                createModPopup.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                });
                createModPopup.setView(promptView);
                createModPopup.show();
            }
        });
        setup();
        findAllMods();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (modParserExec != null) {
            if (!modParserExec.isTerminated()) {
                modParserExec.shutdownNow();
                forcedShutdown = true;
            }
        }
    }
    
    private void findAllMods() {
        final String modJson = "/info.json";
        modParserExec = Executors.newFixedThreadPool(1);
        modParserExec.execute(new Runnable() {
            @Override
            public void run() {
                File[] modFolders = new File(mPath).listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File curFile, String filename) {
                        return new File(curFile, filename).isDirectory();
                    }
                });
                if (modLA.getData().size() > 0) modLA.clearData();
                if (modFolders == null || modFolders.length < 1) {
                    modLV.post(new Runnable() {
                    	@Override
                    	public void run() {
                    		modLA.addData(new ModProperties("No mod/s found", "Please download some mod/s in-game.", null ,null, null, true));
                		}
                	});
                    return;
                } else {
                	for (File folder : modFolders) {
                    	File jsonFile = new File(folder.getAbsolutePath() + modJson);
                    	if (jsonFile.exists()) {
                        	try {
                            	InputStream jsonIS = new FileInputStream(jsonFile);
                            	JSONObject j = new JSONObject(IOUtils.toString(jsonIS, StandardCharsets.UTF_8));
                            	
                                String modName = j.getString("name");
                                String modDesc = j.getString("description");
                                String modAuthor = j.getString("author");
                                String modVersion = j.getString("version");
                                String modPreview = folder.getAbsolutePath() + IOUtils.DIR_SEPARATOR_UNIX + j.getString("preview");
                            	
                                modLV.post(new Runnable() {
                                	@Override
                                	public void run() {
                                    	modLA.addData(new ModProperties(modName, modDesc, modAuthor, modVersion, modPreview, false));
                                	}
                            	});
                        	} catch (IOException | JSONException ex) {
                            	Utils.ThreadUtils.runOnMainThread(getContext(), new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Internal error occurred.", Toast.LENGTH_LONG).show();
                                    }
                                });
                        	}
                    	}
                	}
                }
            }
        }); 
    }
    
    private void setup() {
        modLA = new ModListAdapter(getContext(), new ArrayList<ModProperties>());
        modLV = requireView().findViewById(R.id.mod_listView);
        modLV.setAdapter(modLA);
    }
}
