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
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        Button launchGame = findViewById(R.id.launch_game);
        launchGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchGame();
            }
        });
    }
    
    private void LaunchGame()
    {
        boolean isGPPresent = false; 

        Intent esIntent = getPackageManager().getLaunchIntentForPackage("com.evertechsandbox");
        if (esIntent != null) {
            try
            {
                startActivity(esIntent);
            }
            catch (android.content.ActivityNotFoundException anfe)
            {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe2) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        }
    }
}
