package com.thatmg393.esmanager.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.models.ModProperties;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModListAdapter extends ArrayAdapter<ModProperties> {

    private Context mContext;
    private List<ModProperties> data;

    public ModListAdapter(Context mContext, List<ModProperties> data) {
        super(mContext, R.layout.listview_main, data);

        this.mContext = mContext;
        this.data = data;
    }
    
    public void addData(ModProperties data) {
        if (data != null) {
            this.data.add(data);
        }
        notifyDataSetChanged();
    }

    public void updateData(List<ModProperties> data) {
        if (data != null && data.size() > 0) {
            this.data.clear();
            this.data.addAll(data);
        }
        notifyDataSetChanged();
    }
    
    public void clearData() { 
        if (data != null && data.size() > 0) data.clear();
    }
    
    public List<ModProperties> getData() {
        return data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
        	convertView = inflater.inflate(R.layout.listview_main, parent, false);
		}

        TextView txtModName = convertView.findViewById(R.id.modName);
        TextView txtModDesc = convertView.findViewById(R.id.modDesc);
        TextView txtModAuthor = convertView.findViewById(R.id.modAuthor);
        TextView txtModVersion = convertView.findViewById(R.id.modVersion);
        ImageView ivModPreview = convertView.findViewById(R.id.modPreview);

        ModProperties mp = data.get(position);

        txtModName.setText(Html.fromHtml(mp.getName(), Html.FROM_HTML_MODE_COMPACT));
        txtModDesc.setText(Html.fromHtml(mp.getDesc(), Html.FROM_HTML_MODE_COMPACT));

        if (mp.getNoMods()) {
            txtModAuthor.setVisibility(View.GONE);
            txtModVersion.setVisibility(View.GONE);
            ivModPreview.setVisibility(View.GONE);
        } else {
            txtModAuthor.setText(Html.fromHtml(mp.getAuthor(), Html.FROM_HTML_MODE_COMPACT));
            txtModVersion.setText(Html.fromHtml(mp.getVersion(), Html.FROM_HTML_MODE_COMPACT));
            Bitmap preview = BitmapFactory.decodeFile(mp.getPreviewImgPath());
            ivModPreview.setImageBitmap(preview);
        }

        return convertView;
    }
}

