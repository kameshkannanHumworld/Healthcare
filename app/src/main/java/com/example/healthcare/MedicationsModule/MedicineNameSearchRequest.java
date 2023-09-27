package com.example.healthcare.MedicationsModule;

public class MedicineNameSearchRequest {
    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getIsCarePlan() {
        return isCarePlan;
    }

    public void setIsCarePlan(String isCarePlan) {
        this.isCarePlan = isCarePlan;
    }

    private String medName;
    private String isCarePlan;

}
