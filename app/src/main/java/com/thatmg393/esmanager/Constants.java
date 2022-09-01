package com.thatmg393.esmanager;
import android.os.Environment;

public class Constants {
    public static final String PREF_FILE_NAME = "sharedprefs";
    public static final String ES_PKG_NAME = "com.evertechsandbox";
    public static final String ES_ROOT_FLDR = Environment.getExternalStorageDirectory().toString() + "/Android/data/com.evertechsandbox/files";
    
    public static class PreferenceKeys {
        public static final String DARK_MODE = "isDarkMode";
        public static final String AGREED_RPC = "takeTheRisk";
        public static final String RPC_ENABLED = "gameBroadcast";
        public static final String APPLY_THEME = "fromSPF";
        public static final String ES_RUNNING = "isGameRunning";
        public static final String SEND_CRASH = "sendWhoopsiesToDev";
    }
    
    public static class ResultCodes {
        public static final int MA_startup = 1;
        public static final int MA_modsmenu = 2;
        
        public static final int CMA_askforperm = 3;
        public static final int CMA_projectexp = 4;
    }
}