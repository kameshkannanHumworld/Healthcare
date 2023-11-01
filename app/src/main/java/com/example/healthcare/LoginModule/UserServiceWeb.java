
/*
    Interface for Login using the Web Api
        params1 - username
        params2 - password
*/
package com.example.healthcare.LoginModule;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserServiceWeb {
    @FormUrlEncoded
    @POST("login")
    Call<LoginWebResponse> loginUser(
            @Field("username") String username,
            @Field("password") String password

    );
}
