package com.example.healthcare.MedicationsModule.MedicationsSaveApi;

import com.google.gson.annotations.SerializedName;

public class SaveMedicationList {
    @SerializedName("medicationId")
    private int id;
    private String name;
    private String code;
    private String frequencyCode;
    private String customFrequency;
    private int quantity;
    private String frequency;
    private String notes;
    private String nonProprietaryId;
    private String effectiveDate;
    private String lastEffectiveDate;
    private String invalidFlag;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getFrequencyCode() {
        return frequencyCode;
    }

    public String getCustomFrequency() {
        return customFrequency;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getNotes() {
        return notes;
    }

    public String getNonProprietaryId() {
        return nonProprietaryId;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public String getLastEffectiveDate() {
        return lastEffectiveDate;
    }

    public String getInvalidFlag() {
        return invalidFlag;
    }
}
