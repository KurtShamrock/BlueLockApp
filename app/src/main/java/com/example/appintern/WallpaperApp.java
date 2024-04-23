package com.example.appintern;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WallpaperApp extends AppCompatActivity {
    private ArrayList<ImageModel> modelClassList;
    private RecyclerView recyclerView;
    Adapter adapter;
    EditText editText;
    ImageButton search;
    BottomNavigationView bnv;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaper_app);
        //getSupportActionBar().hide();
        recyclerView=findViewById(R.id.recyclerview);
        editText=findViewById(R.id.searchText);
        search=findViewById(R.id.searchBtn);

        modelClassList=new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setHasFixedSize(true);
        adapter=new Adapter(getApplicationContext(),modelClassList);
        recyclerView.setAdapter(adapter);
        findphotos();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query=editText.getText().toString().trim().toLowerCase();
                if(query.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter theme", Toast.LENGTH_SHORT).show();
                }
                else{
                    getSearchImage(query);
                }
            }
        });
        bnv=findViewById(R.id.nav);

        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId=menuItem.getItemId();
                if(itemId==R.id.wallpaper){
//                    startActivity(new Intent(getApplicationContext(), WallpaperApp.class));
                    return true;
                } else if(itemId==R.id.setPass){
                    startActivity(new Intent(getApplicationContext(), HomePass.class));
                    return true;
                } else if(itemId==R.id.gallery){
//                        startActivity(new Intent(getApplicationContext(),));
                    return true;
                }
                return false;

            }
        });
    }

    private void getSearchImage(String query) {
        ApiUtilities.getApiInterface().getSearchImage(query,1,80).enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                modelClassList.clear();
                if(response.isSuccessful()) {
                    modelClassList.addAll(response.body().getPhotos());
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't get image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {

            }
        });
    }
    private void findphotos() {
        ApiUtilities.getApiInterface().getImage(1,80).enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                if(response.isSuccessful()) {
                    modelClassList.addAll(response.body().getPhotos());
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't get image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {

            }
        });
    }
}
