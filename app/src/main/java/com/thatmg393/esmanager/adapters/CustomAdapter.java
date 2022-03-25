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
import com.thatmg393.esmanager.data.ModProperties;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<ModProperties>
{
    
    Context mContext;
    int resource;
    List<ModProperties> data;
    
    public CustomAdapter(Context mContext, int resource, List<ModProperties> data)
    {
        super(mContext, resource, data);
        
        this.mContext = mContext;
        this.resource = resource;
        this.data = data;
    }

    public void updateModPList(List<ModProperties> newData) {
        data.clear();
        data.addAll(newData);
        this.notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        
        View view = inflater.inflate(R.layout.listview_main, null);
        
        TextView txtModName = view.findViewById(R.id.modName);
        TextView txtModDesc = view.findViewById(R.id.modDesc);
        TextView txtModAuthor = view.findViewById(R.id.modAuthor);
        TextView txtModVersion = view.findViewById(R.id.modVersion);
        ImageView txtModPreview = view.findViewById(R.id.modPreview);
        
        ModProperties mp = data.get(position);
        
        //BitmapFactory.Options config = new BitmapFactory.Options();
        //config.inSampleSize = 4; //Large = Smaller
        
        Bitmap preview = BitmapFactory.decodeFile(mp.getPreviewImgPath());
        
        
        txtModName.setText(Html.fromHtml(mp.getName()));
        txtModDesc.setText(Html.fromHtml(mp.getDesc()));
        txtModAuthor.setText(Html.fromHtml(mp.getAuthor()));
        txtModVersion.setText(Html.fromHtml(mp.getVersion()));
        txtModPreview.setImageBitmap(preview);
        
        return view;
    } 
}
