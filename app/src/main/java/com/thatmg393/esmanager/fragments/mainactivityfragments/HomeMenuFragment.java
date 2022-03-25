package com.thatmg393.esmanager.fragments.mainactivityfragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thatmg393.esmanager.MainActivity;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.services.DiscordRPC;

import java.util.List;

public class HomeMenuFragment extends Fragment {

    private static final String appPackageName = "com.evertechsandbox";

    private static boolean isESLaunched = false;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homemenu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button launchGameButton = getView().findViewById(R.id.launch_game);
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
    }

    private void LaunchGame() {

        Intent esIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.evertechsandbox");
        if (esIntent != null) {
            try {
                startActivity(esIntent);
                MainActivity.sharedPreferenceHelper.addBoolean("isESRunning", isAppRunning(getContext()));

                if (!MainActivity.sharedPreferenceHelper.getBoolean("isServiceRPCRunning") && MainActivity.sharedPreferenceHelper.getBoolean("isSPChecked") && MainActivity.sharedPreferenceHelper.getBoolean("agreed_rpc"))
                {
                    Intent intent = new Intent(getContext(), DiscordRPC.class);
                    getActivity().startService(intent);
                }
            } catch (android.content.ActivityNotFoundException anfe) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe2) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        }
    }

    private boolean isAppRunning(final Context context) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(appPackageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
