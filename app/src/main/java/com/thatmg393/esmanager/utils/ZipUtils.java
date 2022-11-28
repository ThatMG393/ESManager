package com.thatmg393.esmanager.utils;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
    private static final int BUFFER_SIZE = 1024 * 10;
    private static final Logger l = new Logger("ESManager/ZipUtils");
    
    public static void unzipFromAssets(Context ctx, String zipPath, String destination) {
        try {
            if (destination == null || destination.trim().isEmpty()) {
                destination = ctx.getFilesDir().getAbsolutePath();
            }
            
            InputStream is = ctx.getAssets().open(zipPath);
            
        } catch (IOException ioe) { }
    }
    
    public static void unzip(String zipPath, String destination) {
        try {
            FileInputStream fis = new FileInputStream(zipPath);
        } catch (FileNotFoundException fnfe) { }
    }
    
    public static void unzip(FileInputStream fis, String destination) {
        FileUtils.doesDirectoryExists(destination, true);
        
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze;
            
            while ((ze = zis.getNextEntry()) != null) {
                if (ze.isDirectory()) FileUtils.doesDirectoryExists(destination + ze.getName(), true);
                else {
                    if (FileUtils.doesFileExists(destination + ze.getName(), true)) {
                        FileOutputStream fos = new FileOutputStream(destination + ze.getName());
                        
                        int sector;
                        while ((sector = zis.read(buffer)) != -1) fos.write(buffer, 0, sector);
                        
                        zis.closeEntry();
                        fos.close();
                    } else { l.warn("Failed to create file! Skipping..."); }
                }
            }
            zis.close();
        } catch (IOException ioe) { l.err("Something went wrong..."); }
    }
}
