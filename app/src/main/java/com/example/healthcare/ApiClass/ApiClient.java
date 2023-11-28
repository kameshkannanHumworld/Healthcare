package com.example.healthcare.ApiClass;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static Retrofit retrofit = null;
    public static Retrofit retrofitWeb = null;

    //For the mobile dev base url
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://dev-api.humhealth.com/HumHealthMobileDev/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    //for the web dev base url
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


