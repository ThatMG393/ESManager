package com.thatmg393.esmanager.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentCreator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
	public static final byte[] BUFFER_8K = new byte[8 * 1024];
	public static final byte[] BUFFER_16K = new byte[16 * 1024];
	public static final byte[] BUFFER_32K = new byte[32 * 1024];
	
    public static boolean doesDirectoryExists(String directory, boolean autoCreate) {
        File f = new File(directory);
        if (!f.exists() && (autoCreate && !f.mkdirs())) return false;
        return true;
    }
    
    public static boolean doesFileExists(String file, boolean autoCreate) {
        try {
            File f = new File(file);
            if (!f.exists() && (autoCreate && !f.createNewFile())) return false;
            
            return true;
        } catch (IOException ioe) { }
        return false;
    }
	
	public static boolean copyAssetFileToExternal(Context context, String assetName, String externalDest) {
		AssetManager am = context.getAssets();
		if (externalDest == null) { externalDest = context.getExternalFilesDir(null).getAbsolutePath(); }
		if (assetName == null) { assetName = ""; }
		assetName = assetName.trim();
		
		String[] assets = new String[]{};
		try { assets = am.list(assetName); } 
		catch (IOException ioe) { return false; }
		
		if (assets.length < 0) { return false; }
		for (String asset : assets) {
			if (asset != assetName) { break; }
			
			OutputStream os;
			try (InputStream is = am.open(asset)) {
				File dest = new File(externalDest, asset);
				os = new FileOutputStream(dest);
				
				if (!copyFile(is, os)) { throw new IOException("Cannot copy file!"); }
			} catch (IOException ioe) { return false; }
		}
		return true;
	}
	
	public static boolean copyFile(InputStream is, OutputStream os) {
		try (InputStream _is = is; OutputStream _os = os) {
			byte[] buffer = BUFFER_16K;
			int cursor;
			while ((cursor = _is.read(buffer)) != -1) {
				_os.write(buffer, 0, cursor);
			}
		} catch (IOException ioe) { return false; }
		return true;
	}
	
	public static boolean copyFile(InputStream is, OutputStream os, int bufferSize) {
		try (InputStream _is = is; OutputStream _os = os) {
			byte[] buffer = new byte[bufferSize * 1024];
			int cursor;
			while ((cursor = _is.read(buffer)) != -1) {
				_os.write(buffer, 0, cursor);
			}
		} catch (IOException ioe) { return false; }
		return true;
	}
	
	public static String openPlainFile(Uri path) {
		return openPlainFile(new File(path.toString()));
	}
	
	public static String openPlainFile(File file) {
		try {
			Content c = ContentCreator.fromStream(new FileInputStream(file));
			return c.toString();
		} catch (Exception e) { e.printStackTrace(); }
		return null;
	}
	
	public static Content openPlainFileC(File file) {
		try {
			return ContentCreator.fromStream(new FileInputStream(file));
		} catch (Exception e) { e.printStackTrace(); }
		return null;
	}
}
