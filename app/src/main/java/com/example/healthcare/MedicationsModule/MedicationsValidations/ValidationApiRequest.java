package com.example.healthcare.MedicationsModule.MedicationsValidations;

public class ValidationApiRequest {
    private Integer patientId;
    private Integer careplanId;

    private Integer medicationId;
    private String name;
    private String effectiveDate;
    private String lastEffectiveDate;
    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getCareplanId() {
        return careplanId;
    }

    public void setCareplanId(int careplanId) {
        this.careplanId = careplanId;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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



    public Integer getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Integer medicationId) {
        this.medicationId = medicationId;
    }

}
