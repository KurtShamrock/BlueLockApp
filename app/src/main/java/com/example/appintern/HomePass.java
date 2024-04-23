package com.example.appintern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePass extends AppCompatActivity {
    BottomNavigationView bnv;
    CardView setMode;
    TextView modeView;
    SharedPreferenceHelper storage;
    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TAG","OK");

        setContentView(R.layout.activity_ask_to_set_pass);
        bnv=findViewById(R.id.nav1);
        bnv.setSelectedItemId(R.id.setPass);
        storage=new SharedPreferenceHelper(this.getApplicationContext());
        if(storage.getStringValue("mode").equals("On")){
            startService(new Intent(this, LockService.class));

        }
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
        modeView=findViewById(R.id.mode);
        if(storage.getStringValue("mode")!=null){
            modeView.setText(storage.getStringValue("mode"));
        }else{
            storage.saveStringValue("mode","Off");
        }
        if(storage.getStringValue("mode").equals("On")){
            setMode.setCardBackgroundColor(R.color.themeColor);
        }else{
            setMode.setCardBackgroundColor(Color.parseColor("#E0E6E3"));
        }
        setMode.setOnClickListener(view -> {
            if(modeView.getText().toString().equals("On")){
                startActivityForResult(new Intent(this, EnterPassword.class),1212);
            }else{
                startActivityForResult(new Intent(this, NewPassword.class),1234);
            }

        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            if(resultCode==RESULT_OK){

            storage.saveStringValue("mode","On");
            setMode.setCardBackgroundColor(R.color.themeColor);
            modeView.setText(storage.getStringValue("mode"));
            }
        }
        if (requestCode == 1212) {
            if(resultCode==RESULT_OK){

                storage.saveStringValue("mode","Off");
                setMode.setCardBackgroundColor(Color.parseColor("#E0E6E3"));
                modeView.setText(storage.getStringValue("mode"));
            }
        }

    }
}
