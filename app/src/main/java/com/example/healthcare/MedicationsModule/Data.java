package com.example.healthcare.MedicationsModule;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("others")
    private List<Medicine> others;

    public List<Medicine> getOthers() {
        return others;
    }

    public void setOthers(List<Medicine> others) {
        this.others = others;
    }
}
