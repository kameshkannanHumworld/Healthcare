/*
*       Interface to save the medicine in Api
*           params1 - token
*           params2 - save request
*/

package com.example.healthcare.MedicationsModule.MedicationsSaveApi;

import com.example.healthcare.MedicationsModule.MedicationsValidations.ValidationApiRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SaveApiService {

    @POST("medications")
    Call<SaveApiResponse> sendDatatoDatabase(
            @Header("X-Auth-Token") String token,
            @Body SaveApiRequest saveApiRequest
    );
}
