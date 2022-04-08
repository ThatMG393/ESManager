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

        adb = new AlertDialog.Builder(getContext()).create();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp.setChecked(MainActivity.sharedPreferencesUtil.getBoolean("isSPChecked"));
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
                        Utils.ActivityUtils.newDialog(getContext(), getString(R.string.discord_rcp_ask_accept), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.sharedPreferencesUtil.addBoolean("agreed_rpc", true);
                                if (!Utils.ServiceUtils.checkIfServiceIsRunning(getContext(), RPCService.class)) {
                                    requireActivity().startActivity(MainActivity.rpcActIntent);
                                }
                                dialogInterface.dismiss();
                            }
                        }, getString(R.string.discord_rcp_ask_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                sp.setChecked(false);
                                MainActivity.sharedPreferencesUtil.addBoolean("isSPChecked", sp.isChecked());
                                dialogInterface.dismiss();
                            }
                        }, getString(R.string.discord_rcp_ask_title), getString(R.string.discord_rcp_ask_message));

                    } else {
                        if (!Utils.ServiceUtils.checkIfServiceIsRunning(getContext(), RPCService.class)) {
                            requireActivity().startActivity(MainActivity.rpcActIntent);
                        }
                        MainActivity.sharedPreferencesUtil.addBoolean("isRPCRunning", Utils.ServiceUtils.checkIfServiceIsRunning(getContext(), RPCService.class));
                    }
                    MainActivity.sharedPreferencesUtil.addBoolean("isSPChecked", sp.isChecked());
                } else {
                    MainActivity.sharedPreferencesUtil.addBoolean("isSPChecked", sp.isChecked());
                }
                break;

            case "editor_cm":
                MainActivity.sharedPreferencesUtil.addString("shouldUseCM", String.valueOf(sharedPreferences.getBoolean(key, false)));
                break;
        }
    }
}

