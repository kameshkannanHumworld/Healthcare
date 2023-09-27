package com.example.healthcare.MedicationsModule.MedicationsValidations;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ValidationApiService {
    @POST("medications/validation")
    Call<ResponseBody> sendDatatoValidation(
            @Header("X-Auth-Token") String token,
            @Body ValidationApiRequest validationApiRequest
    );
}
