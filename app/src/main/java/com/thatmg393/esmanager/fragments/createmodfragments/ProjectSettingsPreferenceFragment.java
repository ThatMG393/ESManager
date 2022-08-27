package com.thatmg393.esmanager.fragments.createmodfragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.Utils;

public class ProjectSettingsPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    
    public ProjectSettingsPreferenceFragment() { }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.createmod_settings_preference, rootKey);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.setBackgroundColor(Utils.ActivityUtils.ResourceUtils.getCurrentThemeColorToInt(getContext(), android.R.attr.windowBackground));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
            case "editor_word_wrap": return;
        }
    }
}

