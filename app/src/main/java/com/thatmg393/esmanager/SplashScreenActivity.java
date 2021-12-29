package com.thatmg393.esmanager;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreenActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        EasySplashScreen config = new EasySplashScreen(SplashScreenActivity.this)
            .withFullScreen()
            .withTargetActivity(MainActivity.class)
            .withSplashTimeOut(3000)
            .withBackgroundColor(Color.parseColor("#1a1b29"))
            .withAfterLogoText("ESManager")
            .withFooterText("Copyright 2021")
            .withLogo(R.mipmap.ic_launcher_round);
        
        config.getFooterTextView().setTextColor(Color.WHITE);
        config.getAfterLogoTextView().setTextColor(Color.WHITE);
        
        View easySplashScreen = config.create();
        
        setContentView(easySplashScreen);
    }
    
}
