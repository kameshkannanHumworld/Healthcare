package com.example.healthcare.MedicationsModule;

import java.util.List;

public class MedicineNameApiResponse {
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    private String status;
    private Data data;
}
