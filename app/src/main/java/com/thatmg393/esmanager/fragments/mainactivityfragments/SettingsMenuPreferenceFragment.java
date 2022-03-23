package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.PreferenceFragmentCompat;

import com.thatmg393.esmanager.R;

public class SettingsMenuPreferenceFragment extends PreferenceFragmentCompat {

    public SettingsMenuPreferenceFragment() {}

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
    }
}
