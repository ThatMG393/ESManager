package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

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
        sp = (SwitchPreference) findPreference("pref_settings_dcrpc");

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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("pref_settings_dcrpc")) {
            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            boolean test = sharedPreferences.getBoolean(s, false);
            if (test) {
                if (!MainActivity.sharedPreferencesUtil.getBoolean("agreed_rpc")) {
                    adb.setTitle("Warning!");
                    adb.setMessage("This is a unsafe feature!\r\nIt might make your Discord account vulnerable to hackers!\r\n\r\nAre you sure you want to turn on this feature?");

                    adb.setButton(DialogInterface.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.sharedPreferencesUtil.addBoolean("agreed_rpc", true);
                            if (!Utils.ServiceUtils.checkIfServiceIsRunning(getContext(), RPCService.class)) {
                                requireActivity().startActivity(MainActivity.rpcActIntent);
                            }
                            dialogInterface.dismiss();
                        }
                    });

                    adb.setButton(DialogInterface.BUTTON_NEGATIVE,"No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sp.setChecked(false);
                            MainActivity.sharedPreferencesUtil.addBoolean("isSPChecked", sp.isChecked());
                            dialogInterface.dismiss();
                        }
                    });

                    adb.show();
                } else {
                    if (sp.isChecked() && MainActivity.sharedPreferencesUtil.getBoolean("agreed_rpc") && !Utils.ServiceUtils.checkIfServiceIsRunning(getContext(), RPCService.class)) {
                        requireActivity().startActivity(MainActivity.rpcActIntent);
                        MainActivity.sharedPreferencesUtil.addBoolean("isRPCRunning", Utils.ServiceUtils.checkIfServiceIsRunning(getContext(), RPCService.class));
                    }
                    else if (!sp.isChecked() && Utils.ServiceUtils.checkIfServiceIsRunning(getContext(), RPCService.class)) {
                        MainActivity.sharedPreferencesUtil.addBoolean("isRPCRunning", Utils.ServiceUtils.checkIfServiceIsRunning(getContext(), RPCService.class));
                    }
                }
                MainActivity.sharedPreferencesUtil.addBoolean("isSPChecked", sp.isChecked());
            } else {
                MainActivity.sharedPreferencesUtil.addBoolean("isSPChecked", sp.isChecked());
            }
        }
    }
}

