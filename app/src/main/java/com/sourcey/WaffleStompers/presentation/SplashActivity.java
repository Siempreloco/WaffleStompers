package com.sourcey.WaffleStompers.presentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.sourcey.WaffleStompers.R;
import com.sourcey.WaffleStompers.objects.Constants;

public class SplashActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this,LoginActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, Constants.SPLASH_DISPLAY_LENGTH);
    }
}