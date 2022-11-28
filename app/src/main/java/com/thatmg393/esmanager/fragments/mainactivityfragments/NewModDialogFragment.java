package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;


import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.ProjectActivity;

public class NewModDialogFragment extends DialogFragment {

    public static final String TAG = "createmod-dialog";

    private Toolbar toolbar;

    public static NewModDialogFragment display(FragmentManager fragmentManager) {
        NewModDialogFragment exampleDialog = new NewModDialogFragment();
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(android.R.style.Animation_Dialog);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);

        toolbar = view.findViewById(R.id.fragment_dialog_toolbar);
		
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
        toolbar.setTitle("Some Title");
        toolbar.inflateMenu(R.menu.fragment_dialog_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent i = new Intent(getContext(), ProjectActivity.class);
				
				startActivity(i);
				dismiss();
				return true;
			}
		});
    }
}