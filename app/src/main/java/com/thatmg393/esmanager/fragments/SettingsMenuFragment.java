package com.thatmg393.esmanager.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.thatmg393.esmanager.DiscordRPC;
import com.thatmg393.esmanager.R;

public class SettingsMenuFragment extends Fragment
{
    boolean acceptedConsequences = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settingsmenu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button firstSettings = getView().findViewById(R.id.settings_rcp);
        Switch fistSettingsSwitch = getView().findViewById(R.id.settings_rcp_switch);

        firstSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (fistSettingsSwitch.isChecked())
                {
                    if (acceptedConsequences)
                    {

                    }
                    else
                    {
                        AlertDialog.Builder bldr = new AlertDialog.Builder(getContext());

                        bldr.setTitle(R.string.discord_rcp_ask_title);
                        bldr.setMessage(R.string.discord_rcp_ask_message);

                        bldr.setPositiveButton(R.string.discord_rcp_ask_accept, new DialogInterface.OnClickListener()

                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                acceptedConsequences = true;
                                Intent rcpIntent = new Intent(getContext(), DiscordRPC.class);
                                startActivity(rcpIntent);
                            }
                        });

                        bldr.setNegativeButton(R.string.discord_rcp_ask_cancel, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {

                            }
                        });

                        bldr.create().show();
                    }
                }
            }
        });


    }
}
