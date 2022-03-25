package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.thatmg393.esmanager.MainActivity;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.services.DiscordRPC;

public class SettingsMenuPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static SwitchPreference sp;

    public SettingsMenuPreferenceFragment() { }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            sp = (SwitchPreference) findPreference(s);
            boolean test = sharedPreferences.getBoolean(s, false);
            if (test) {
                if (!MainActivity.sharedPreferenceHelper.getBoolean("agreed_rpc")) {
                    AlertDialog adb = new AlertDialog.Builder(getContext()).create();
                    adb.setTitle("Warning!");
                    adb.setMessage("This is a unsafe feature!\r\nIt might make your Discord account vulnerable to hackers!\r\n\r\nAre you sure you want to turn on this feature?");

                    adb.setButton(DialogInterface.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.sharedPreferenceHelper.addBoolean("agreed_rpc", true);
                            if (!isMyServiceRunning(DiscordRPC.class)) {
                                Intent intent = new Intent(getContext(), DiscordRPC.class);
                                getActivity().startService(intent);
                            }
                            dialogInterface.dismiss();
                        }
                    });

                    adb.setButton(DialogInterface.BUTTON_NEGATIVE,"No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sp.setChecked(false);
                            dialogInterface.dismiss();
                        }
                    });

                    adb.show();
                } else {
                    if (sp.isChecked() && MainActivity.sharedPreferenceHelper.getBoolean("agreed_rpc")) {
                        if (!isMyServiceRunning(DiscordRPC.class)) {
                            Intent intent = new Intent(getContext(), DiscordRPC.class);
                            getActivity().startService(intent);
                        }
                    }
                }
                MainActivity.sharedPreferenceHelper.addBoolean("isSPChecked", sp.isChecked());

            }
            else {
                MainActivity.sharedPreferenceHelper.addBoolean("isSPChecked", sp.isChecked());
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

