package com.thatmg393.esmanager;

import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;

public class HomeMenuFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homemenu, container, false);
    }
}
