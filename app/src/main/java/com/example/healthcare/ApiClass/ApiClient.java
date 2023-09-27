package com.example.healthcare.ApiClass;

import androidx.annotation.NonNull;

import com.example.healthcare.LoginModule.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static Retrofit retrofit = null;
    public static Retrofit retrofitWeb = null;


    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://dev-api.humhealth.com/HumHealthMobileDev/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getWebClient() {

        if (retrofitWeb == null) {
            retrofitWeb = new Retrofit.Builder()
                    .baseUrl("https://dev-api.humhealth.com/HumHealthDevAPI/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWeb;
    }
}


