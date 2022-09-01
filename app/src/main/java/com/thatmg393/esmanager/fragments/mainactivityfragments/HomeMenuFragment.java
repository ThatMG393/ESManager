package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thatmg393.esmanager.Constants;
import com.thatmg393.esmanager.MainActivity;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.Utils;
import com.thatmg393.esmanager.rpc.RPCGlobal;
import com.thatmg393.esmanager.rpc.RPCService;
import com.thatmg393.esmanager.rpc.RPCNotInitializedException;

public class HomeMenuFragment extends Fragment {
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homemenu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button launchGameButton = requireView().findViewById(R.id.launch_game);

        launchGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchGame();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.sharedPreferencesUtil.addBoolean(Constants.PreferenceKeys.ES_RUNNING, Utils.ActivityUtils.checkIfAppIsRunning(getContext(), Constants.ES_PKG_NAME));
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity.sharedPreferencesUtil.addBoolean(Constants.PreferenceKeys.ES_RUNNING, Utils.ActivityUtils.checkIfAppIsRunning(getContext(), Constants.ES_PKG_NAME));
    }

    private void LaunchGame() {
        Intent esIntent = getActivity().getPackageManager().getLaunchIntentForPackage(Constants.ES_PKG_NAME);
        if (esIntent != null) {
            try {
                startActivity(esIntent);
                MainActivity.sharedPreferencesUtil.addBoolean(Constants.PreferenceKeys.ES_RUNNING, Utils.ActivityUtils.checkIfAppIsRunning(getContext(), Constants.ES_PKG_NAME));

                if (MainActivity.sharedPreferencesUtil.getBoolean(Constants.PreferenceKeys.RPC_ENABLED) && MainActivity.sharedPreferencesUtil.getBoolean(Constants.PreferenceKeys.AGREED_RPC) && Utils.ServiceUtils.isServiceRunning(getContext(), RPCService.class)) {
                    RPCGlobal.getServiceInstance().sendPresence();
                }
            } catch (ActivityNotFoundException anfe) {
                openAppPage();
            } catch (RPCNotInitializedException rpcnie) {
            	//TODO: Initialize RPCService
            }
        } else { openAppPage(); }
    }
    
    private void openAppPage() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Constants.ES_PKG_NAME)));
        } catch (ActivityNotFoundException x) {
        	try {
            	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + Constants.ES_PKG_NAME)));
            } catch (ActivityNotFoundException y) {
            	Toast.makeText(getContext(), "Are you using a Nokia 3310? I cannot seem to open Google Play and your Browser.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
