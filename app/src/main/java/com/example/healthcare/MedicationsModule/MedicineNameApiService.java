/*
*   this interface is to search the medicine by type the medicine name
*/

package com.example.healthcare.MedicationsModule;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MedicineNameApiService {
    @FormUrlEncoded
    @POST("medications/names")
    Call<MedicineNameApiResponse> searchMedicine(
            @Header("X-Auth-Token") String token,
            @Field("medName") String medName,
            @Field("isCarePlan") String isCarePlan
    );

}

