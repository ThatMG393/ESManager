package com.thatmg393.esmanager.fragments.createmodfragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thatmg393.esmanager.CreateModActivity;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.data.FilePojo;
import com.thatmg393.esmanager.adapters.FolderAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProjectExplorerFragment extends Fragment {
    
    private static final String rootPath = CreateModActivity.projectPath;
    private String curPath = rootPath;
    
    private ArrayList<FilePojo> dirList;
    private ArrayList<FilePojo> foldersList;
    private ArrayList<FilePojo> filesList;
    private Comparator<FilePojo> compAscending = new Comparator<FilePojo>() {
		@Override
		public int compare(FilePojo f1, FilePojo f2) {
			return f1.getName().compareTo(f2.getName());
		}
	};
    
    private Activity mFAct;
    private ListView explorer;
    private FolderAdapter fAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_explorer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mFAct = getActivity();
		
        explorer = requireView().findViewById(R.id.project_explorer);
        initExplorer();
        loadExplorer(curPath);
        explorer.setAdapter(fAdapter);
    }

    private void loadExplorer(String path) {
        File[] files = new File(path).listFiles();
        
        if (files == null) return;
        if (foldersList.size() > 0) foldersList.clear();
        if (filesList.size() > 0) filesList.clear();
        if (dirList.size() > 0) dirList.clear();
        
        for (File file : files) {
            if (file.isDirectory()) {
                foldersList.add(new FilePojo(file.getName(), true));
            } else {
            	filesList.add(new FilePojo(file.getName(), false));
            }
        }
        
        Collections.sort(foldersList, compAscending);
        Collections.sort(filesList, compAscending);
        dirList.addAll(foldersList);
        dirList.addAll(filesList);
        
        fAdapter = new FolderAdapter(mFAct, dirList);
    }
    
    private void onListItemClick(int position) {
        if (!fAdapter.getItem(position).isFolder()) {
            // TODO: Open file on editor.
        } else {
        	curPath = curPath + "/" + fAdapter.getItem(position).getName();
            loadExplorer(curPath);
        }
    }
    
    private void onListItemHold(int position) {
        if (!fAdapter.getItem(position).isFolder()) {
            // TODO: Show bottom buttons.
        } else {
        	// TODO: Show bottom buttons.
        }
    }
    
    private void initExplorer() {
        foldersList = new ArrayList<>();
		filesList = new ArrayList<>();
        dirList = new ArrayList<>();
        
        explorer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View lv, int position, long id) {
                onListItemClick(position);
            }
        });
        
        explorer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View lv, int position, long id) {
                onListItemHold(position);
                return true;
            }
        });
    }
}