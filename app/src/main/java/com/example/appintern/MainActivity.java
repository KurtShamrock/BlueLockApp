package com.example.appintern;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {
    private static final String DEBUGTAG = "JWP";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button setPassBtn = (Button)findViewById(R.id.setPassword);
        Button setWallBtn = (Button)findViewById(R.id.setWallpaper);
        setPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("password", MODE_PRIVATE);
                boolean keyExists = sharedPreferences.contains("password");
                if (keyExists == false) {
                    startActivity(new Intent(MainActivity.this, NewPassword.class));
                } else {
                    startActivity(new Intent(MainActivity.this, EnterPassword.class));
                }
            }
        });
        setWallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WallpaperApp.class));
            }
        });
    }
}
