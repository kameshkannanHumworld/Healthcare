package com.example.healthcare.PojoClass;

public class MedicationPojoClass {
    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineFrequency() {
        return medicineFrequency;
    }

    public void setMedicineFrequency(String medicineFrequency) {
        this.medicineFrequency = medicineFrequency;
    }

    public String getMedicineQuantity() {
        return medicineQuantity;
    }

    public void setMedicineQuantity(String medicineQuantity) {
        this.medicineQuantity = medicineQuantity;
    }

    private String medicineName;
    private String medicineFrequency;
    private String medicineQuantity;

}
