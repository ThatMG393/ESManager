package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.thatmg393.esmanager.BuildConfig;
import com.thatmg393.esmanager.Constants;
import com.thatmg393.esmanager.MainActivity;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.Utils;
import com.thatmg393.esmanager.interfaces.IOnSharedPreferenceChange;
import com.thatmg393.esmanager.rpc.DiscordRPC;
import com.thatmg393.esmanager.ui.DualActionSwitchPreference;
import com.thatmg393.esmanager.utils.SharedPreference;

public class SettingsPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, IOnSharedPreferenceChange {
    private SwitchPreference darkMode;
    private DualActionSwitchPreference discordRPC;
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
		
        darkMode.setChecked(SharedPreference.getInstance().getBool(Constants.PreferenceKeys.DARK_MODE));
		discordRPC.setChecked(SharedPreference.getInstance().getBool(Constants.PreferenceKeys.RPC_ENABLED));
		discordRPC.setSwitchClickListener(new DualActionSwitchPreference.DualActionSwitchPreferenceListener() {
			@Override
			public void onCheckedChanged(SwitchCompat btnV, boolean isChecked) { }
			
			@Override
			public void onClick(View v) {
				Utils.ActivityUtils.changeFragmentWithAnim(
                            getActivity().getSupportFragmentManager().beginTransaction(),
                            R.id.fragment_container,
                            new DiscordRPCSettingsFragment());
			}
		});
        sendCrashes.setChecked(SharedPreference.getInstance().getBool(Constants.PreferenceKeys.SEND_CRASH));
		
        Preference ps = findPreference("about_version");
		ps.setTitle(ps.getTitle() + ": " + BuildConfig.VERSION_NAME);
		
        adb = new AlertDialog.Builder(getContext()).create();
        adb.dismiss();
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
		SharedPreference.getInstance().addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        adb.dismiss();
        if (discordRPC.isChecked() && !SharedPreference.getInstance().getBool(Constants.PreferenceKeys.AGREED_RPC)) {
            discordRPC.setChecked(false);
        }
		SharedPreference.getInstance().removeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "misc_discordrpc":
                if (sharedPreferences.getBoolean(key, false)) {
                    if (!SharedPreference.getInstance().getBool(Constants.PreferenceKeys.AGREED_RPC)) {
                        adb.setTitle(getString(R.string.discord_rpc_ask_title));
                        adb.setMessage(getString(R.string.discord_rpc_ask_message));
                        adb.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.discord_rpc_ask_accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreference.getInstance().addBool(Constants.PreferenceKeys.AGREED_RPC, true);
                                DiscordRPC.getInstance().startRPCService();
                                
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
                        DiscordRPC.getInstance().startRPCService();
                    }
                    refreshPref();
                } else {
                    DiscordRPC.getInstance().stopRPCService();
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
	
	@Override
	public void onChange(String key, Object value) {
		if (key.equals(Constants.PreferenceKeys.RPC_ENABLED)) {
			discordRPC.setChecked(SharedPreference.getInstance().getBool(Constants.PreferenceKeys.RPC_ENABLED));
		}
	}
    
    public void refreshPref() {
        SharedPreference.getInstance().addBool(Constants.PreferenceKeys.RPC_ENABLED, discordRPC.isChecked());
        SharedPreference.getInstance().addBool(Constants.PreferenceKeys.DARK_MODE, darkMode.isChecked());
        SharedPreference.getInstance().addBool(Constants.PreferenceKeys.SEND_CRASH, sendCrashes.isChecked());
    }
}