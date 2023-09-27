package com.example.healthcare.MedicationsModule.ViewMedications;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ViewMedicationResponse {

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ViewMedicationData> getData() {
        return data;
    }

    public void setData(List<ViewMedicationData> data) {
        this.data = data;
    }

    public List<ViewMedicationData> getActiveMedications() {
        return activeMedications;
    }

    public void setActiveMedications(List<ViewMedicationData> activeMedications) {
        this.activeMedications = activeMedications;
    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    private Integer id;
    private String status;

    @SerializedName("data")
    private List<ViewMedicationData> data;

    @SerializedName("activeMedications")
    private List<ViewMedicationData> activeMedications;

    private Integer logId;

}
