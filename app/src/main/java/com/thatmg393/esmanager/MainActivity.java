package com.thatmg393.esmanager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.core.splashscreen.SplashScreen;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.thatmg393.esmanager.activity.BaseActivity;
import com.thatmg393.esmanager.models.DiscordProfile;
import com.thatmg393.esmanager.fragments.mainactivityfragments.HomeMenuFragment;
import com.thatmg393.esmanager.fragments.mainactivityfragments.ModsMenuFragment;
import com.thatmg393.esmanager.fragments.mainactivityfragments.SettingsPreferenceFragment;
import com.thatmg393.esmanager.interfaces.IOnActivityResult;
import com.thatmg393.esmanager.interfaces.IRPCListener;
import com.thatmg393.esmanager.rpc.DiscordRPC;
import com.thatmg393.esmanager.utils.PermissionUtils;
import com.thatmg393.esmanager.utils.SharedPreference;

import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static WeakReference<MainActivity> mInstance;
    public static MainActivity getInstance() {
        return mInstance.get();
    }
    
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setup();
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.main_toolbar));

        bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setOnItemSelectedListener(
                new BottomNavigationView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.bottom_nav_homeMenu:
                                Utils.ActivityUtils.changeFragmentWithAnim(
                                        getSupportFragmentManager().beginTransaction(),
                                        R.id.fragment_container,
                                        new HomeMenuFragment());
                                break;
                            case R.id.bottom_nav_modsMenu:
                                if (PermissionUtils.isPermissionDenied(getApplicationContext(), Utils.app_perms[0])) {
                                    PermissionUtils.askForPermission(
                                            MainActivity.this,
                                            Utils.app_perms[0],
                                            Constants.ResultCodes.MA_modsmenu);
                                            
                                    bottomNav.setSelectedItemId(R.id.bottom_nav_homeMenu);
                                    Utils.ActivityUtils.changeFragmentWithAnim(
                                        getSupportFragmentManager().beginTransaction(),
                                        R.id.fragment_container,
                                        new HomeMenuFragment());
                                } else {
                                    Utils.ActivityUtils.changeFragmentWithAnim(
                                            getSupportFragmentManager().beginTransaction(),
                                            R.id.fragment_container,
                                            new ModsMenuFragment());
                                }
                                break;
                            case R.id.bottom_nav_settingsMenu:
                                Utils.ActivityUtils.changeFragmentWithAnim(
                                        getSupportFragmentManager().beginTransaction(),
                                        R.id.fragment_container,
                                        new SettingsPreferenceFragment());
                                break;
                        }
                        return true;
                    }
                });

        if (getIntent().getBooleanExtra(Constants.PreferenceKeys.APPLY_THEME, false)) {
            bottomNav.setSelectedItemId(R.id.bottom_nav_settingsMenu);
            gonChange = false;
        } else {
            bottomNav.setSelectedItemId(R.id.bottom_nav_homeMenu);
        }
        
        PermissionUtils.askForPermission(this, Utils.app_perms[0], Constants.ResultCodes.MA_startup);
        
        getExternalFilesDir(null);
        String[] sA = new String[] { "/Projects", "/Logs" };
        for (String p : sA) {
            new File(Constants.ESM_ROOT_FLDR + p).mkdirs();
        }
    }

    private void setup() {
        SharedPreference.newInstance(this, Constants.PREF_FILE_NAME);
        startLogging();
        mInstance = new WeakReference<MainActivity>(this);
        Utils.ActivityUtils.setThemeAuto(this);
        
        // RPC startup.
        DiscordRPC.initializeInstance(getApplication()); // Initiate the class
        DiscordRPC.getInstance().addListener(new IRPCListener() {
			@Override
			public void onReady(DiscordProfile dp) { 
				System.out.println(dp.getFullUsername());
				System.out.println(dp.getAvatarUrl());
			}
		
			@Override
			public void onError(Exception err) {
                System.out.println("We have an error....");
                err.printStackTrace();
            }
		
			@Override
			public void onSessionsReplace(String oldStatus, String newStatus) { }
		
			@Override
			public void onStop(String reason, int code) { }
		});
        
        if (SharedPreference.getInstance().getBool(Constants.PreferenceKeys.AGREED_RPC) && 
            SharedPreference.getInstance().getBool(Constants.PreferenceKeys.RPC_ENABLED)) {
            DiscordRPC.getInstance().startRPCService();
        }
		
		FileProviderRegistry.getInstance().addFileProvider(new AssetsFileResolver(getAssets()));
    }
    
    private boolean gonChange;
    public void applyTheme() {
        Intent maIntent = new Intent(this, getClass());
        maIntent.putExtra(Constants.PreferenceKeys.APPLY_THEME, true);
        maIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(maIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        gonChange = true;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.ResultCodes.MA_startup:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(MainActivity.this, "Well...", Toast.LENGTH_LONG).show();
                }
                return;
            case Constants.ResultCodes.MA_modsmenu:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utils.ActivityUtils.changeFragmentWithAnim(
                            getSupportFragmentManager().beginTransaction(),
                            R.id.fragment_container,
                            new ModsMenuFragment());
                } else {
                    Toast.makeText(MainActivity.this, "This feature requires storage permission", Toast.LENGTH_LONG).show();
                    bottomNav.setSelectedItemId(R.id.bottom_nav_homeMenu);
                    Utils.ActivityUtils.changeFragmentWithAnim(
                                        getSupportFragmentManager().beginTransaction(),
                                        R.id.fragment_container,
                                        new HomeMenuFragment());
                }
                return;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private long firstBackTime;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstBackTime > 1695) {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            firstBackTime = System.currentTimeMillis();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
		if (!gonChange) DiscordRPC.getInstance().stopRPCService();
    }
    
    // I don't know if this is the way you do it but... it works
    private List<IOnActivityResult> Lioar = new ArrayList<IOnActivityResult>();
    public void addOnActivityResultListener(IOnActivityResult ioar) {
        Lioar.add(ioar);
    }
    
    public void launchActivityForResult(Intent i, int code) {
        startActivityForResult(i, code);
    }
    
    private IOnActivityResult tmpIoar;
    public void launchActivityForResult(Intent i, int code, IOnActivityResult ioar) {
        startActivityForResult(i, code);
        tmpIoar = ioar;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
        if (tmpIoar != null) {
            tmpIoar.onActivityResult(requestCode, resultCode, data);
            tmpIoar = null;
            System.gc(); // Call to free 'tmpIoar' allocated memory?
        }
        
        if (Lioar.size() < 0) return;
        for (IOnActivityResult ioar : Lioar) {
            ioar.onActivityResult(requestCode, resultCode, data);
        }
    }
}
