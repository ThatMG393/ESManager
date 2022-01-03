package com.thatmg393.esmanager.fragments;

import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;

import com.thatmg393.esmanager.R;

public class HomeMenuFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homemenu, container, false);
    }
}
