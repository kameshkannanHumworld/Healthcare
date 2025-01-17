package com.example.healthcare.MedicationsModule.MedicationsSaveApi;

import java.util.List;

public class SaveApiResponse {
    public int getId() {
        return id;
    }

    public String getPreventativeMeasureGoalCode() {
        return preventativeMeasureGoalCode;
    }

    public String getStatus() {
        return status;
    }

    public MedicationSaveData getMedicationSaveData() {
        return medicationSaveData;
    }

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

    private int id;
    private String preventativeMeasureGoalCode;
    private String status;
    private MedicationSaveData medicationSaveData;
    private String timestamp;
    private String carePlanDate;
    private int careplanId;
    private List<SaveMedicationList> saveMedicationLists;
    private String activeDiseases;
    private Integer diagnosisId;
}
