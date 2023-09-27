package com.example.healthcare.MedicationsModule.FrequencyDropdown;

import com.google.gson.annotations.SerializedName;

public class MedicationFrequency {

    @SerializedName("code")
    private String code;

    @SerializedName("description")
    private String description;




    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

