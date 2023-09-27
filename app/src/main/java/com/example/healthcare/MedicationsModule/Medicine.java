package com.example.healthcare.MedicationsModule;

public class Medicine {
    private String mediProprietaryName;
    private String mediNonProprietaryName;
    private String nonProprietaryNameWithDosage;
    private String proprietaryNameWithDosage;

    public Integer getMediProdId() {
        return mediProdId;
    }

    public void setMediProdId(Integer mediProdId) {
        this.mediProdId = mediProdId;
    }

    private Integer mediProdId;
    public String getMediProprietaryName() {
        return mediProprietaryName;
    }

    public void setMediProprietaryName(String mediProprietaryName) {
        this.mediProprietaryName = mediProprietaryName;
    }

    public String getMediNonProprietaryName() {
        return mediNonProprietaryName;
    }

    public void setMediNonProprietaryName(String mediNonProprietaryName) {
        this.mediNonProprietaryName = mediNonProprietaryName;
    }



    public String getNonProprietaryNameWithDosage() {
        return nonProprietaryNameWithDosage;
    }

    public void setNonProprietaryNameWithDosage(String nonProprietaryNameWithDosage) {
        this.nonProprietaryNameWithDosage = nonProprietaryNameWithDosage;
    }

    public String getProprietaryNameWithDosage() {
        return proprietaryNameWithDosage;
    }

    public void setProprietaryNameWithDosage(String proprietaryNameWithDosage) {
        this.proprietaryNameWithDosage = proprietaryNameWithDosage;
    }



}

