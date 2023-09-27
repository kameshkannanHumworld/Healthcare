package com.example.healthcare.MedicationsModule.DeleteMedications;

import com.example.healthcare.MedicationsModule.MedicationsValidations.ValidationApiRequest;
import com.example.healthcare.MedicationsModule.ViewMedications.ViewMedicationResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DeleteApiService {

    @POST("medications/invalid")
    Call<DeleteApiResponse> deleteMedications(
            @Header("X-Auth-Token") String token,
            @Body DeleteApiRequest deleteApiRequest
    );

}
