package com.example.ai_road;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logoImage);
        TextView title = findViewById(R.id.appName);

        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        Animation textAnimation = AnimationUtils.loadAnimation(this, R.anim.text_zoom_in);

        logo.startAnimation(logoAnimation);
        title.startAnimation(textAnimation);

        if (logo != null && title != null) {
            logo.startAnimation(logoAnimation);
            title.startAnimation(textAnimation);
        }

        new Handler().postDelayed(() -> {
            startActivity(new Intent(Splash.this, Login.class));
            finish();
        }, SPLASH_DURATION);
    }

}
