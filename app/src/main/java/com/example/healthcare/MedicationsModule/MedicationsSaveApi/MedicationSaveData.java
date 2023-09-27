package com.example.healthcare.MedicationsModule.MedicationsSaveApi;

import java.util.List;

public class MedicationSaveData {
    public String getTimestamp() {
        return timestamp;
    }

    public String getCarePlanDate() {
        return carePlanDate;
    }

    public int getCareplanId() {
        return careplanId;
    }

    public List<SaveMedicationList> getSaveMedicationLists() {
        return saveMedicationLists;
    }

    public String getActiveDiseases() {
        return activeDiseases;
    }

    public Integer getDiagnosisId() {
        return diagnosisId;
    }

    private String timestamp;
    private String carePlanDate;
    private int careplanId;
    private List<SaveMedicationList> saveMedicationLists;
    private String activeDiseases;
    private Integer diagnosisId;
}
