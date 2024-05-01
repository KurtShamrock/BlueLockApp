package com.example.appintern;

import android.annotation.SuppressLint;
import android.util.Log;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtilities {
    static {
        System.loadLibrary("keys");
    }
    public static native String getApi();
    private static Retrofit retrofit=null;
    public static final String API = getApi();
    public static ApiInterface getApiInterface() {
        if(retrofit==null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor())
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiInterface.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiInterface.class);

    }
}
