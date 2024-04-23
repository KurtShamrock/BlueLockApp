package com.example.appintern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePass extends AppCompatActivity {
    BottomNavigationView bnv;
    CardView setMode,setPoint;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_ask_to_set_pass);
        bnv=findViewById(R.id.nav);

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId=menuItem.getItemId();
                if(itemId==R.id.wallpaper){
                    startActivity(new Intent(getApplicationContext(), WallpaperApp.class));
                    return true;
                } else if(itemId==R.id.setPass){
//                    startActivity(new Intent(getApplicationContext(), .class));
                    return true;
                } else if(itemId==R.id.gallery){
//                        startActivity(new Intent(getApplicationContext(),));
                    return true;
                }
                return false;

            }
        });
        setMode=findViewById(R.id.set_mode);
        setPoint=findViewById(R.id.set_point);
        setMode.setOnClickListener(view -> {

        });
    }
}
