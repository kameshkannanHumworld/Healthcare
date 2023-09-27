package com.example.healthcare.MedicationsModule.FrequencyDropdown;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface MedicationFrequencyService {

    @GET("hum-codes/CPLN-MEDI-FREQ")
    Call<MedicationFrequencyResponse> getMedicationFrequencies(
            @Header("X-Auth-Token") String token,
            @Query("id") int id
    );
}

