package com.thatmg393.esmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
        
        
        txtModName.setText(mp.getName());
        txtModDesc.setText(mp.getDesc());
        txtModAuthor.setText(mp.getAuthor());
        txtModVersion.setText(mp.getVersion());
        txtModPreview.setImageBitmap(preview);
        
        return view;
    } 
}
