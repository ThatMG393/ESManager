package com.thatmg393.esmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import br.tiagohm.codeview.CodeView;
import br.tiagohm.codeview.Language;

public class ProjectEditorFragment extends Fragment {

    private CodeView codeView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_editor, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] languageKeywords = {"es.TimeScale", "es.DeltaTime", "es.FixedDeltaTime", "es.SetSoundVolume()", "es.SetSoundLoop()", "es.PlaySound()", "es.Player"};

        codeView = (CodeView) requireView().findViewById(R.id.project_codeEditor);

        codeView.setLanguage(Language.LUA)
                .setShowLineNumber(true)
                .setStartLineNumber(0)
                .setCode("funtion onPlace() \r\n\r\nend" +
                        "\r\n\r\nfuntion start() \r\n\r\nend" +
                        "\r\n\r\nfunction update() \r\n\r\nend" +
                        "\r\n\r\nfunction fixedUpdate() \r\n\r\nend")
                .setZoomEnabled(true)
                .setWrapLine(true)
                .apply();
    }
}
