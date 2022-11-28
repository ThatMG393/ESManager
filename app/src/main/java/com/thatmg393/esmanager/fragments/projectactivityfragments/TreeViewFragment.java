package com.thatmg393.esmanager.fragments.projectactivityfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolderFactory;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.viewholders.FileTreeViewHolder;
import com.thatmg393.esmanager.viewholders.FolderTreeViewHolder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TreeViewFragment extends Fragment {
	private RecyclerView rv;
	
	private TreeViewAdapter tvAdapter;
	private List<TreeNode> tnList;
	
	@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_project_treeview_fragment, container, false);
    }
	
	@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
		initialize();
		
		TreeNode root = crawlStorageFiles(new File("/storage/emulated/0/ESManager"));
		tnList.add(root);
		tvAdapter.updateTreeNodes(tnList);
	}
	
	@Override
	public void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
	}
	
	public TreeNode crawlStorageFiles(File parentPath) {
    	if (parentPath.isDirectory())  {
        	TreeNode node = new TreeNode(parentPath.getName(), R.layout.exp_folder);
        	for (File file : parentPath.listFiles()) {
            	node.addChild(crawlStorageFiles(file));
        	}
        	return node;
    	} else {
        	TreeNode node = new TreeNode(parentPath.getName(), R.layout.exp_file);
        	return node;
    	}
	}
	
	private void initialize() {
		TreeViewHolderFactory tvFactory = (v, layout) -> {
			if (layout == R.layout.exp_file) return new FileTreeViewHolder(v);
			return new FolderTreeViewHolder(v);
		};
		tvAdapter = new TreeViewAdapter(tvFactory);
		
		rv = requireView().findViewById(R.id.treeview_main);
		rv.setAdapter(tvAdapter);
		rv.setLayoutManager(new LinearLayoutManager(getContext()));

		
		tnList = new ArrayList<TreeNode>();
	}
}
