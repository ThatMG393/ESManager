package com.thatmg393.esmanager.fragments.createmodfragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.Utils;

public class ProjectInfoFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return Utils.ActivityUtils.setFragmentTheme(getContext(), R.layout.fragment_project_info, container);
    }
}
