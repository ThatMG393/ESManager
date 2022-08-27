package com.thatmg393.esmanager.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.data.FilePojo;

import java.util.ArrayList;

/**
 * Created by Kashif on 4/7/2018.
 * Modified by ThatMG393 on 18/7/2022
 */

public class FolderAdapter extends ArrayAdapter<FilePojo> {


	Activity context;
	ArrayList<FilePojo> dataList;

	public FolderAdapter(Activity context, ArrayList<FilePojo> dataList) {
		super(context, R.layout.exp_filerow, dataList);
		this.context = context;
		this.dataList = dataList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(R.layout.exp_filerow, parent, false);

		ImageView imageView = (ImageView) convertView.findViewById(R.id.exp_lv_icon);
		TextView name = (TextView) convertView.findViewById(R.id.exp_lv_name);

		if (dataList.get(position).isFolder()) {
			imageView.setImageResource(R.drawable.ic_folder_black);
		} else {
			imageView.setImageResource(R.drawable.ic_info_black);
		}
		
		name.setText(dataList.get(position).getName() );

		return convertView;
	}
}
