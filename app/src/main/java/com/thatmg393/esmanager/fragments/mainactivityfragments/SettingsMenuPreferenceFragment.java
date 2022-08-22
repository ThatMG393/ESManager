package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.thatmg393.esmanager.MainActivity;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.Utils;
import com.thatmg393.esmanager.services.RPCService;

public class SettingsMenuPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SwitchPreference sp;
    private AlertDialog adb;

    public SettingsMenuPreferenceFragment() { }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
        sp = findPreference("misc_dcrpc");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp.setChecked(MainActivity.sharedPreferencesUtil.getBoolean("isSPChecked"));
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
            case "misc_dcrpc":
                if (sharedPreferences.getBoolean(key, false)) {
                    if (!MainActivity.sharedPreferencesUtil.getBoolean("agreed_rpc")) {
                        adb.setTitle(getString(R.string.discord_rpc_ask_title));
                        adb.setMessage(getString(R.string.discord_rpc_ask_message));
                        adb.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.discord_rpc_ask_accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.sharedPreferencesUtil.addBoolean("agreed_rpc", true);
                                if (!Utils.ServiceUtils.isServiceRunning(getContext(), RPCService.class)) {
                                    requireActivity().startActivity(MainActivity.rpcActIntent);
                                }
                                dialogInterface.dismiss();
                            }
                        });
                        adb.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.discord_rpc_ask_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sp.setChecked(false);
                                refreshPref();
                                dialogInterface.dismiss();
                            }
                        });
                        adb.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                        	public void onCancel(DialogInterface dialog) {
                            	sp.setChecked(false);
                                refreshPref();
                        	}
                        });
                        adb.show();
                    } else {
                        if (MainActivity.sharedPreferencesUtil.getBoolean("agreed_rpc") && !Utils.ServiceUtils.isServiceRunning(getContext(), RPCService.class)) {
                            requireActivity().startActivity(MainActivity.rpcActIntent);
                        }
                    }
                    refreshPref();
                } else {
                    refreshPref();
                }
                break;
        }
    }
    
    private void refreshPref() {
        MainActivity.sharedPreferencesUtil.addBoolean("isSPChecked", sp.isChecked());
    }
}

