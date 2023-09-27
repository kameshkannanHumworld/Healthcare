package com.example.healthcare.MedicationsModule.MedicationsSaveApi;

public class SaveApiRequest {
    private Integer patientId;
    private Integer careplanId;
    private Integer medicationId;
    private Integer code;
    private String name;
    private String notes;
    private String effectiveDate;
    private String lastEffectiveDate;
    private String frequency;
    private String customFrequency;
    private Integer quantity;
    private String activeFlag;
    private String productCode;
    private Integer visitId;
    private String isFavoriteFlag;
    private Integer logId;
    private String careplanLogMessageUserInput;
    private String careplanLogMessage;

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public void setCareplanId(Integer careplanId) {
        this.careplanId = careplanId;
    }

    public void setMedicationId(Integer medicationId) {
        this.medicationId = medicationId;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public void setLastEffectiveDate(String lastEffectiveDate) {
        this.lastEffectiveDate = lastEffectiveDate;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setCustomFrequency(String customFrequency) {
        this.customFrequency = customFrequency;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setActiveFlag(String activeFlag) {
        this.activeFlag = activeFlag;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setVisitId(Integer visitId) {
        this.visitId = visitId;
    }

    public void setIsFavoriteFlag(String isFavoriteFlag) {
        this.isFavoriteFlag = isFavoriteFlag;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public void setCareplanLogMessageUserInput(String careplanLogMessageUserInput) {
        this.careplanLogMessageUserInput = careplanLogMessageUserInput;
    }

    public void setCareplanLogMessage(String careplanLogMessage) {
        this.careplanLogMessage = careplanLogMessage;
    }
}
