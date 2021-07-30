package com.shoutlab.coronabd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shoutlab.coronabd.Onboarding.OnboardingActivity;

public class SplashActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private Animation zoomInAndRotate, fadeIn;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Animation file definition
        zoomInAndRotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_and_rotate);
        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

        preferenceManager = new PreferenceManager(SplashActivity.this);

        //Initialize Firebase
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                if(preferenceManager.getIsLoggedIn()){
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }
        };
        handler.postDelayed(r, 1500);

        animate();
    }

    private void animate() {
        ImageView logoImageView = findViewById(R.id.covid_logo);
        TextView appName = findViewById(R.id.app_name);

        appName.startAnimation(fadeIn);
        logoImageView.startAnimation(zoomInAndRotate);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(SplashActivity.this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
