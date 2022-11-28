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

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.textfield.TextInputLayout;
import com.thatmg393.esmanager.Constants;
import com.thatmg393.esmanager.ProjectActivity;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.adapters.ModListAdapter;
import com.thatmg393.esmanager.models.ModProperties;
import com.thatmg393.esmanager.Utils;

import com.thatmg393.esmanager.utils.PermissionUtils;
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
				NewModDialogFragment.display(getActivity().getSupportFragmentManager());
            }
        });
        setup();
        if (PermissionUtils.isPermissionDenied(getContext(), Utils.app_perms[0])) {
            modLV.post(new Runnable() {
                @Override
                public void run() {
                	Toast.makeText(getContext(), "Please grant storage permission on\tSettings -> App -> ESManager -> Permissions -> Storage", Toast.LENGTH_LONG).show();
        		}
            });
        } else {
        	findAllMods();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (modParserExec != null) {
            if (!modParserExec.isTerminated() || !modParserExec.isShutdown()) {
                modParserExec.shutdownNow();
            }
        }
    }
    
    private void findAllMods() {
        final String modJson = "/info.json";
        modParserExec = Executors.newFixedThreadPool(4);
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
                            	modLV.post(new Runnable() {
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
    
    private boolean validateFields(String modName, String modDesc, String modPath) {
        if (modName.trim().isEmpty()
         && modDesc.trim().isEmpty()
         && modPath.trim().isEmpty()) return false;
        
        return true;
    }
}
