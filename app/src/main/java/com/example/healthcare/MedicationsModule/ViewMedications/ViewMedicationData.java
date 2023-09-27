package com.example.healthcare.MedicationsModule.ViewMedications;
import com.google.gson.annotations.SerializedName;

public class ViewMedicationData {

    @SerializedName("medicationId")
    private Integer medicationId;

    private String name;
    private String code;
    private String frequencyCode;
    private String customFrequency;
    private Integer quantity;
    private String frequency;
    private String notes;

    @SerializedName("nonProprietaryId")
    private String nonProprietaryId;

    private String effectiveDate;
    private String lastEffectiveDate;
    private String invalidFlag;

    public Integer getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Integer medicationId) {
        this.medicationId = medicationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFrequencyCode() {
        return frequencyCode;
    }

    public void setFrequencyCode(String frequencyCode) {
        this.frequencyCode = frequencyCode;
    }

    public String getCustomFrequency() {
        return customFrequency;
    }

    public void setCustomFrequency(String customFrequency) {
        this.customFrequency = customFrequency;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNonProprietaryId() {
        return nonProprietaryId;
    }

    public void setNonProprietaryId(String nonProprietaryId) {
        this.nonProprietaryId = nonProprietaryId;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getLastEffectiveDate() {
        return lastEffectiveDate;
    }

    public void setLastEffectiveDate(String lastEffectiveDate) {
        this.lastEffectiveDate = lastEffectiveDate;
    }

    public String getInvalidFlag() {
        return invalidFlag;
    }

    public void setInvalidFlag(String invalidFlag) {
        this.invalidFlag = invalidFlag;
    }
}
