package com.thatmg393.esmanager.fragments.createmodfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.ui.CodeEditor;

public class ProjectEditorFragment extends Fragment {

    String start_code = "function onPlace() \r\n\r\nend" +
            "\r\n\r\nfunction start() \r\n\r\nend" +
            "\r\n\r\nfunction update() \r\n\r\nend" +
            "\r\n\r\nfunction fixedUpdate() \r\n\r\nend";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_editor, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CodeEditor cde = view.findViewById(R.id.code_editor);
        cde.append(start_code);

        // String[] languageKeywords = {"es.TimeScale", "es.DeltaTime", "es.FixedDeltaTime", "es.SetSoundVolume()", "es.SetSoundLoop()", "es.PlaySound()", "es.Player"};
    }
}
