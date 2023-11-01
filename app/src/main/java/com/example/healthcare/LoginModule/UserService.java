/*
    Interface for Login using the Mob dev Api
        params1 - username
        params2 - password
*/

package com.example.healthcare.LoginModule;

import retrofit2.Call;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
public interface UserService {

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> loginUser(
            @Field("username") String username,
            @Field("password") String password

    );





}

