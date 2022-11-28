package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.thatmg393.esmanager.Constants;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.interfaces.IOnSharedPreferenceChange;
import com.thatmg393.esmanager.rpc.DiscordRPC;
import com.thatmg393.esmanager.utils.SharedPreference;

public class DiscordRPCSettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, IOnSharedPreferenceChange {
	private SwitchPreference discordRPC;
	
	private AlertDialog adb;
	
	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		setPreferencesFromResource(R.xml.discordrpc_settings_preference, rootKey);
		
		discordRPC = findPreference("misc_discordrpc");
	}
	
	@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		discordRPC.setChecked(SharedPreference.getInstance().getBool(Constants.PreferenceKeys.RPC_ENABLED));
		
		adb = new AlertDialog.Builder(getContext()).create();
        adb.dismiss();
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
                } else {
                    DiscordRPC.getInstance().stopRPCService();
                }
				break;
		}
	}
	
	@Override
	public void onChange(String key, Object value) {
		if (key.equals(Constants.PreferenceKeys.RPC_ENABLED)) {
			discordRPC.setChecked(SharedPreference.getInstance().getBool(Constants.PreferenceKeys.RPC_ENABLED));
		}
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
	
	public void refreshPref() {
        SharedPreference.getInstance().addBool(Constants.PreferenceKeys.RPC_ENABLED, discordRPC.isChecked());
    }
}
