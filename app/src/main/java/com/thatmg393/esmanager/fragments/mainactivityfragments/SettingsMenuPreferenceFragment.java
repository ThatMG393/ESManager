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

public class SettingsMenuPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public SettingsMenuPreferenceFragment() { }

    // private final SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();

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
            SwitchPreference sp = (SwitchPreference) findPreference(s);
            boolean test = sharedPreferences.getBoolean(s, false);
            if (test) {
                if (!MainActivity.sharedPreferences.getBoolean("agreed_rpc", false))
                {
                    AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                    adb.setTitle("Warning!");
                    adb.setMessage("This is a unsafe feature!\r\nIt might make your Discord account vulnerable to hackers!\r\n\r\nAre you sure you want to turn on this feature?");

                    adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            MainActivity.editor.putBoolean("agreed_rpc", true);
                            MainActivity.editor.apply();
                        }
                    });
                }
            }
        }
    }
}

