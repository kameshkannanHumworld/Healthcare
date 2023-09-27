package com.example.healthcare.LoginModule;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserServiceWeb {
    @FormUrlEncoded
    @POST("login") // Assuming "login" is the endpoint for login in your API
    Call<LoginWebResponse> loginUser(
            @Field("username") String username,
            @Field("password") String password

    );
}
