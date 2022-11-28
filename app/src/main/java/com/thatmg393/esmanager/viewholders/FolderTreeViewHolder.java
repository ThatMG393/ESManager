package com.thatmg393.esmanager.viewholders;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewHolder;
import org.apache.commons.io.FilenameUtils;
import com.thatmg393.esmanager.R;

public class FolderTreeViewHolder extends TreeViewHolder {
	private final TextView tv;
	
	public FolderTreeViewHolder(@NonNull View itemView) {
        super(itemView);
		tv = itemView.findViewById(R.id.exp_folder_display);
    }

    @Override
    public void bindTreeNode(TreeNode node) {
        super.bindTreeNode(node);
		String nodeName = node.getValue().toString();
		tv.setText(nodeName);
		
        switch (FilenameUtils.getExtension(nodeName)) {
			case "lua": // TODO: Import icons
			case "obj": setDrawableRight(R.drawable.ic_folder_black); break;
			default: setDrawableRight(R.drawable.ic_info_black); break;
		}
    }
	
	private void setDrawableRight(@DrawableRes int drawable) {
		tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0);
	}
}
