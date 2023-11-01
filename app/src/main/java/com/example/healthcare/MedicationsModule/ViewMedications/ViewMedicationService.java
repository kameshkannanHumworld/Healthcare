/*
 *   This interface is to view the medicine details
 *      params1 - patientId
 *      params2 - careplanID
 *      params3 - token
 *      params4 - user ID (decode the Token and get the ID)
 */


package com.example.healthcare.MedicationsModule.ViewMedications;

import com.example.healthcare.MedicationsModule.FrequencyDropdown.MedicationFrequencyResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ViewMedicationService {

    @GET("medications/active/{patientId}/{careplanId}")
    Call<ViewMedicationResponse> getMedicationsList(
            @Path("patientId") int patientId,
            @Path("careplanId") int careplanId,
            @Header("X-Auth-Token") String token,
            @Query("id") int id

    );
}
