package com.example.epey_scraper.splash;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.epey_scraper.R;
import com.example.epey_scraper.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final TextView bigText = findViewById(R.id.splash_big_text);
        final TextView littleText = findViewById(R.id.splash_little_text);

//        make fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        bigText.animate()
                .alpha(1)
                .setStartDelay(500)
                .setDuration(1500)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        littleText.animate()
                                .alpha(1)
                                .setDuration(1500)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }).start();
                    }
                }).start();
    }
}
