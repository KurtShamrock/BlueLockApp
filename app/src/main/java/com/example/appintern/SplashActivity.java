package com.example.appintern;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start home activity
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(SplashActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));

                        startActivityForResult(intent, 1234);
                    }else{
                        startActivity(new Intent(SplashActivity.this, WallpaperApp.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, WallpaperApp.class));
                    finish();
                }


                // on the below line we are finishing
                // our current activity.

            }
        }, 500);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            startActivity(new Intent(this,
                    WallpaperApp.class));

        }
        finish();
    }
}
