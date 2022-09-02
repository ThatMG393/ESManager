package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.thatmg393.esmanager.Constants;
import com.thatmg393.esmanager.MainActivity;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.Utils;
import com.thatmg393.esmanager.rpc.RPCService;

public class SettingsPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
	
    private SwitchPreference darkMode;
    private SwitchPreference discordRPC;
    private CheckBoxPreference sendCrashes;
    
    private AlertDialog adb;

    public SettingsPreferenceFragment() { }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main_settings_preference, rootKey);
        
        darkMode = findPreference("interface_darkmode");
        discordRPC = findPreference("misc_discordrpc");
        sendCrashes = findPreference("app_sendcrashrep");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        darkMode.setChecked(MainActivity.sharedPreferencesUtil.getBoolean(Constants.PreferenceKeys.DARK_MODE));
        discordRPC.setChecked(MainActivity.sharedPreferencesUtil.getBoolean(Constants.PreferenceKeys.RPC_ENABLED));
        sendCrashes.setChecked(MainActivity.sharedPreferencesUtil.getBoolean(Constants.PreferenceKeys.SEND_CRASH));
        
        refreshPref();
        
        adb = new AlertDialog.Builder(getContext()).create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adb.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "misc_discordrpc":
                if (sharedPreferences.getBoolean(key, false)) {
                    if (!MainActivity.sharedPreferencesUtil.getBoolean(Constants.PreferenceKeys.AGREED_RPC)) {
                        adb.setTitle(getString(R.string.discord_rpc_ask_title));
                        adb.setMessage(getString(R.string.discord_rpc_ask_message));
                        adb.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.discord_rpc_ask_accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.sharedPreferencesUtil.addBoolean(Constants.PreferenceKeys.AGREED_RPC, true);
                                if (!Utils.ServiceUtils.isServiceRunning(getContext(), RPCService.class)) {
                                    requireActivity().startActivity(MainActivity.rpcActIntent);
                                }
                                dialogInterface.dismiss();
                            }
                        });
                        adb.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.discord_rpc_ask_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                discordRPC.setChecked(false);
                                refreshPref();
                                dialogInterface.dismiss();
                            }
                        });
                        adb.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                        	public void onCancel(DialogInterface dialog) {
                            	discordRPC.setChecked(false);
                                refreshPref();
                        	}
                        });
                        adb.show();
                    } else {
                        if (MainActivity.sharedPreferencesUtil.getBoolean(Constants.PreferenceKeys.AGREED_RPC) && !Utils.ServiceUtils.isServiceRunning(getContext(), RPCService.class)) {
                            requireActivity().startActivity(MainActivity.rpcActIntent);
                        }
                    }
                    refreshPref();
                } else {
                    refreshPref();
                }
                break;
                
        	case "interface_darkmode":
            	refreshPref();
                MainActivity.getInstance().applyTheme();
            	break;
                
            case "app_sendcrashrep":
            	refreshPref();
            	break;
        }
    }
    
    private void refreshPref() {
        MainActivity.sharedPreferencesUtil.addBoolean(Constants.PreferenceKeys.RPC_ENABLED, discordRPC.isChecked());
        MainActivity.sharedPreferencesUtil.addBoolean(Constants.PreferenceKeys.DARK_MODE, darkMode.isChecked());
        MainActivity.sharedPreferencesUtil.addBoolean(Constants.PreferenceKeys.SEND_CRASH, sendCrashes.isChecked());
    }
}
