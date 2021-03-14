package com.example.registerloginsp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DELAY=2500;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
        getSupportActionBar().hide();

        getWindow().setBackgroundDrawable(null);
        initializeView();
        animateLogo();
        goTomainactivity();
    }

    private void initializeView(){
        imageView=findViewById(R.id.imageView);

    }

    private void animateLogo(){
        Animation fadingInAnimation= AnimationUtils.loadAnimation(this,R.anim.fade_in);
        fadingInAnimation.setDuration(SPLASH_DELAY);
        imageView.startAnimation(fadingInAnimation);
    }

    private void goTomainactivity(){
        new Handler().postDelayed(()-> {
            startActivity(new Intent(SplashActivity.this, StartActivity.class));
            finish();
        },SPLASH_DELAY);

    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}