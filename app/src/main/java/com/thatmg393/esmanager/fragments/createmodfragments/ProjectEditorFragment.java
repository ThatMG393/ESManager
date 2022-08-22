package com.thatmg393.esmanager.fragments.createmodfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amrdeveloper.codeview.CodeView;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.classes.LuaSyntaxManager;

import java.util.HashMap;
import java.util.Map;

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

        CodeView codeView = requireView().findViewById(R.id.code_editor);
        codeView.addSyntaxPattern(LuaSyntaxManager.lua_keywords, LuaSyntaxManager.lua_keywords_colors);
        codeView.addSyntaxPattern(LuaSyntaxManager.lua_operators, LuaSyntaxManager.lua_operators_colors);
        codeView.addSyntaxPattern(LuaSyntaxManager.lua_chars, LuaSyntaxManager.lua_chars_colors);

        Map<Character, Character> pairMap = new HashMap<>();
        pairMap.put('{', '}');
        pairMap.put('[', ']');
        pairMap.put('(', ')');
        pairMap.put('<', '>');
        pairMap.put('"', '"');

        codeView.setPairCompleteMap(pairMap);
    }
}
